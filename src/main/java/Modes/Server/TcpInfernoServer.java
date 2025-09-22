package Modes.Server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.concurrent.*;

public class TcpInfernoServer {

    // ===== Security session =====
    static class Session {
        final String keyBase64;
        final long createdAt;
        final LinkedHashSet<String> recentNonces = new LinkedHashSet<>();
        Session(String keyBase64) { this.keyBase64 = keyBase64; this.createdAt = System.currentTimeMillis(); }
    }
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SecureRandom RNG = new SecureRandom();

    // ===== Storage =====
    static class PlayerStore {
        volatile String blockSystems = "[]";
        volatile String connections  = "[]";
        volatile String wires        = "[]";
        volatile String packets      = "[]";
    }
    private static final ConcurrentHashMap<String, PlayerStore> DataBase = new ConcurrentHashMap<>();

    // ===== Game cores (per player) =====
    private static final ConcurrentHashMap<String, ServerGameCore> CORES = new ConcurrentHashMap<>();
    private static ServerGameCore coreFor(String playerId) {
        return CORES.computeIfAbsent(playerId, ServerGameCore::new);
    }

    public static PlayerStore getOrLoadPlayerStore(String playerId) {
        return DataBase.computeIfAbsent(playerId, TcpInfernoServer::loadFromDisk);
    }

    // ===== Config & infra =====
    public static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int PORT = Integer.parseInt(System.getProperty("PORT", "5050"));
    private static final Path ROOT = Paths.get(System.getProperty("DATA_DIR", "server_data")).toAbsolutePath();

    private static final int POOL_SIZE = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
    private static final ExecutorService POOL = new ThreadPoolExecutor(
            POOL_SIZE, POOL_SIZE,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            r -> {
                Thread t = new Thread(r, "inferno-client");
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void main(String[] args) throws Exception {
        Files.createDirectories(ROOT);
        try (ServerSocket ss = new ServerSocket(PORT)) {
            System.out.println("[TcpInfernoServer] Listening (line-delimited) on tcp://127.0.0.1:" + PORT);
            while (true) {
                Socket s = ss.accept();
                POOL.submit(() -> handle(s));
            }
        }
    }

    private static void handle(Socket s) {
        try (s;
             BufferedReader in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                JsonNode req = objectMapper.readTree(line);

                String cmdRoot = text(req, "cmd");     // NEW
                String op      = text(req, "op");      // OLD
                String playerId= text(req, "playerId");

                if (playerId == null || playerId.isBlank()) { writeLine(out, err("missing playerId")); continue; }
                if (op == null && cmdRoot == null) { writeLine(out, err("missing op/cmd")); continue; }

                // ---- HELLO (no signature) ----
                if ("hello".equals(op)) {
                    Session session = new Session(newSessionKeyBase64());
                    SESSIONS.put(playerId, session);
                    writeLine(out, okWith("\"key\":\"" + session.keyBase64 + "\",\"ts\":" + System.currentTimeMillis()));
                    continue;
                }

                // ---- Security (HMAC) ----
                Session session = SESSIONS.get(playerId);
                if (session == null) { writeLine(out, err("no session - call hello first")); continue; }

                long ts = req.path("ts").asLong(0L);
                String nonce = text(req, "nonce");
                String sig   = text(req, "sig");
                if (nonce == null || sig == null || ts == 0L) { writeLine(out, err("missing ts/nonce/sig")); continue; }
                long now = System.currentTimeMillis();
                if (Math.abs(now - ts) > 60_000) { writeLine(out, err("ts skew")); continue; }
                synchronized (session.recentNonces) {
                    if (session.recentNonces.contains(nonce)) { writeLine(out, err("replayed nonce")); continue; }
                    session.recentNonces.add(nonce);
                    while (session.recentNonces.size() > 2048) {
                        var it = session.recentNonces.iterator();
                        if (it.hasNext()) { it.next(); it.remove(); }
                    }
                }

                String entity;
                String body;
                String opForSig;

                if (cmdRoot != null) {
                    opForSig = "cmd";
                    entity   = "input";
                    JsonNode payload = req.get("payload");
                    body = (payload == null) ? "" : payload.toString();
                } else {
                    opForSig = op;
                    entity   = text(req, "entity");
                    if (entity == null) entity = "";
                    if ("put".equals(op)) {
                        JsonNode data = req.get("data");
                        body = (data == null) ? "" : data.toString();
                    } else {
                        body = "";
                    }
                }

                if (!verifyHmac(session.keyBase64, opForSig, entity, playerId, body, ts, nonce, sig)) {
                    writeLine(out, err("bad signature"));
                    continue;
                }

                // ---- Dispatch ----
                if (cmdRoot != null) {
                    // NEW: KEY/MOUSE/UI to server core
                    ServerGameCore core = coreFor(playerId);
                    core.Run();
                    JsonNode p = req.get("payload");
                    switch (cmdRoot) {
                        case "KEY"   -> {
                            String name = p.path("keyName").asText("");
                            int keyCode = p.path("keyCode").asInt(0);
                            boolean pressed = p.path("pressed").asBoolean(true);
                            if (pressed) {
                                core.handleKeyPressed(keyCode, name);
                            } else {
                                core.handleKeyReleased(keyCode, name);
                            }
                        }
                        case "MOUSE" -> {
                            String type = p.path("type").asText("");
                            int button  = p.path("button").asInt(0);
                            int x = p.path("x").asInt();
                            int y = p.path("y").asInt();
                            switch (type) {
                                case "DOWN" -> core.handleMousePress(button, x, y);
                                case "UP" -> core.handleMouseRelease(button, x, y);
                                case "CLICK" -> core.handleMouseClicked(button, x, y);
                            }

                        }
                        case "UI"    -> {
                            String action = p.path("action").asText("");
                            String payloadJson = p == null ? null : p.toString();
                            switch (action) {
                                case "OPEN_SHOP" -> core.handleOpenShop();
                                case "RESET_LEVEL" -> {
                                    core.retryLevel();
                                    System.out.println("Player " + playerId + " requested a new game.");
                                }
                            }
                        }
                        default      -> { writeLine(out, err("unknown cmd")); continue; }
                    }
                    writeLine(out, ok());
                } else {
                    // OLD: get/put (compat for StorageFacade remote)
                    PlayerStore ps = getOrLoadPlayerStore(playerId);
                    switch (op) {
                        case "get" -> {
                            String ent = text(req, "entity");
                            if (ent == null) { writeLine(out, err("missing entity")); break; }
                            String data = getEntity(ps, ent);
                            writeLine(out, okWith("\"data\":" + data));
                        }
                        case "put" -> {
                            String ent = text(req, "entity");
                            if (ent == null) { writeLine(out, err("missing entity")); break; }
                            JsonNode data = req.get("data");
                            if (data == null || !data.isArray()) { writeLine(out, err("data must be array")); break; }
                            String json = objectMapper.writeValueAsString(data);
                            setEntity(ps, ent, json);
                            saveToDisk(playerId, fileName(ent), json);
                            writeLine(out, ok());
                        }
                        default -> writeLine(out, err("unknown op"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== compat storage helpers =====
    private static String getEntity(PlayerStore ps, String entity) {
        return switch (entity) {
            case "block-systems" -> ps.blockSystems;
            case "connections"   -> ps.connections;
            case "wires"         -> ps.wires;
            case "packets"       -> ps.packets;
            default -> "[]";
        };
    }
    private static void setEntity(PlayerStore ps, String entity, String json) {
        switch (entity) {
            case "block-systems" -> ps.blockSystems = json;
            case "connections"   -> ps.connections  = json;
            case "wires"         -> ps.wires        = json;
            case "packets"       -> ps.packets      = json;
        }
    }
    private static String fileName(String entity) {
        return switch (entity) {
            case "block-systems" -> "BlockSystems.json";
            case "connections"   -> "Connections.json";
            case "wires"         -> "Wires.json";
            case "packets"       -> "Packets.json";
            default -> "Unknown.json";
        };
    }
    private static PlayerStore loadFromDisk(String playerId) {
        PlayerStore ps = new PlayerStore();
        ps.blockSystems = readDisk(playerId, "BlockSystems.json");
        ps.connections  = readDisk(playerId, "Connections.json");
        ps.wires        = readDisk(playerId, "Wires.json");
        ps.packets      = readDisk(playerId, "Packets.json");
        return ps;
    }
    private static String readDisk(String playerId, String fileName) {
        Path p = ROOT.resolve(playerId).resolve(fileName);
        if (!Files.exists(p)) return "[]";
        try { return Files.readString(p, StandardCharsets.UTF_8); }
        catch (IOException e) { return "[]"; }
    }
    private static void saveToDisk(String playerId, String fileName, String body) throws IOException {
        Path dir = ROOT.resolve(playerId);
        Files.createDirectories(dir);
        Path p = dir.resolve(fileName);
        Files.writeString(p, body, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // ===== IO helpers =====
    private static void writeLine(BufferedWriter out, String json) throws IOException {
        out.write(json);
        out.write('\n');
        out.flush();
    }
    private static String ok() { return "{\"ok\":true}"; }
    private static String okWith(String kvPairs) { return "{\"ok\":true," + kvPairs + "}"; }
    private static String err(String msg) { return "{\"ok\":false,\"error\":\"" + msg + "\"}"; }
    private static String text(JsonNode n, String f) {
        return (n != null && n.get(f) != null && n.get(f).isTextual()) ? n.get(f).asText() : null;
    }

    // ===== Security helpers =====
    private static String newSessionKeyBase64() {
        byte[] key = new byte[32];
        RNG.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
    private static boolean verifyHmac(String keyBase64, String op, String entity, String playerId, String body, long ts, String nonce, String sigBase64) {
        try {
            String canonical = op + "\n" + entity + "\n" + playerId + "\n" + (body == null ? "" : body) + "\n" + ts + "\n" + nonce;
            byte[] expect = hmacSha256(Base64.getDecoder().decode(keyBase64), canonical.getBytes(StandardCharsets.UTF_8));
            byte[] got = Base64.getDecoder().decode(sigBase64);
            return slowEquals(expect, got);
        } catch (Exception e) {
            return false;
        }
    }
    private static byte[] hmacSha256(byte[] key, byte[] msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(msg);
    }
    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= (a[i] ^ b[i]);
        return r == 0;
    }
}
