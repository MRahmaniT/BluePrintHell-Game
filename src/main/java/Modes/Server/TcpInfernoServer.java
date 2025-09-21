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

    // ===== Config & infra =====
    private static final int PORT = Integer.parseInt(System.getProperty("PORT", "5050"));
    private static final Path ROOT = Paths.get(System.getProperty("DATA_DIR", "server_data")).toAbsolutePath();
    private static final ObjectMapper M = new ObjectMapper();

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
                JsonNode req = M.readTree(line);

                // NEW format? (cmd at root)
                String cmdRoot = text(req, "cmd");
                String op = text(req, "op");
                String playerId = text(req, "playerId");

                if (playerId == null || playerId.isBlank()) {
                    writeLine(out, err("missing playerId"));
                    continue;
                }

                if (op == null && cmdRoot == null) {
                    writeLine(out, err("missing op/cmd"));
                    continue;
                }

                // HELLO
                if ("hello".equals(op)) {
                    Session session = new Session(newSessionKeyBase64());
                    SESSIONS.put(playerId, session);
                    writeLine(out, okWith("\"key\":\"" + session.keyBase64 + "\",\"ts\":" + System.currentTimeMillis()));
                    continue;
                }

                // Security
                Session session = SESSIONS.get(playerId);
                if (session == null) {
                    writeLine(out, err("no session - call hello first"));
                    continue;
                }

                long ts = req.path("ts").asLong(0L);
                String nonce = text(req, "nonce");
                String sig = text(req, "sig");
                if (nonce == null || sig == null || ts == 0L) {
                    writeLine(out, err("missing ts/nonce/sig"));
                    continue;
                }
                long now = System.currentTimeMillis();
                if (Math.abs(now - ts) > 60_000) {
                    writeLine(out, err("ts skew"));
                    continue;
                }
                synchronized (session.recentNonces) {
                    if (session.recentNonces.contains(nonce)) {
                        writeLine(out, err("replayed nonce"));
                        continue;
                    }
                    session.recentNonces.add(nonce);
                    // trim to 2048
                    while (session.recentNonces.size() > 2048) {
                        var it = session.recentNonces.iterator();
                        if (it.hasNext()) { it.next(); it.remove(); }
                    }
                }

                // body & fields for signature
                String entity;
                String bodyForSig;
                String opForSig;

                if (cmdRoot != null) {
                    // NEW format
                    opForSig = "cmd";
                    entity = "input";
                    JsonNode payload = req.get("payload");
                    bodyForSig = (payload == null) ? "" : payload.toString();
                } else {
                    // OLD format
                    opForSig = op;
                    entity = text(req, "entity");
                    if (entity == null) entity = "";
                    if ("put".equals(op)) {
                        JsonNode data = req.get("data");
                        bodyForSig = (data == null) ? "" : data.toString();
                    } else {
                        bodyForSig = "";
                    }
                }

                if (!verifyHmac(session.keyBase64, opForSig, entity, playerId, bodyForSig, ts, nonce, sig)) {
                    writeLine(out, err("bad signature"));
                    continue;
                }

                // Dispatch
                if (cmdRoot != null) {
                    // NEW: KEY / MOUSE / UI
                    JsonNode payload = req.get("payload");
                    switch (cmdRoot) {
                        case "KEY"   -> handleKey(playerId, payload);
                        case "MOUSE" -> handleMouse(playerId, payload);
                        case "UI"    -> handleUi(playerId, payload);
                        default      -> { writeLine(out, err("unknown cmd")); continue; }
                    }
                    writeLine(out, ok());
                } else {
                    // get/put
                    PlayerStore playerStore = DataBase.computeIfAbsent(playerId, TcpInfernoServer::loadFromDisk);
                    switch (op) {
                        case "get" -> {
                            String ent = text(req, "entity");
                            if (ent == null) { writeLine(out, err("missing entity")); break; }
                            String data = getEntity(playerStore, ent);
                            writeLine(out, okWith("\"data\":" + data));
                        }
                        case "put" -> {
                            String ent = text(req, "entity");
                            if (ent == null) { writeLine(out, err("missing entity")); break; }
                            JsonNode data = req.get("data");
                            if (data == null || !data.isArray()) {
                                writeLine(out, err("data must be array"));
                                break;
                            }
                            String json = M.writeValueAsString(data);
                            setEntity(playerStore, ent, json);
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

    // ===== Handlers for game logic =====
    private static void handleKey(String playerId, JsonNode payLoad) throws Exception {
        if (payLoad == null) return;
        String keyName = payLoad.path("keyName").asText("");
        boolean pressed = payLoad.path("pressed").asBoolean(true);
        int keyCode = payLoad.path("keyCode").asInt(0);

        if (!pressed) return; // usually act on key down; tweak as needed
        switch (keyName) {
            case "P"      -> {
                if (!gamePanel.isIntersected()) {
                    gamePanel.handleP();
                }
            }
            case "ESCAPE" -> GameController.escape();
            case "Left"   -> PlayerController.moveLeft();
            case "Right"  -> PlayerController.moveRight();
            default       -> GameController.onKey(keyCode, keyName);
        }
    }

    private static void handleMouse(String playerId, JsonNode p) throws Exception {
        if (p == null) return;
        String type = p.path("type").asText("");
        int button = p.path("button").asInt(0);
        double x = p.path("x").asDouble();
        double y = p.path("y").asDouble();

        switch (type) {
            case "DOWN"  -> WiringManager.handleMousePress(x, y, button);
            case "UP"    -> WiringManager.handleMouseRelease(x, y, button);
            case "CLICK" -> WiringManager.handleMouseClick(x, y, button);
            case "DRAG"  -> WiringManager.handleMouseDrag(x, y, button);
            case "MOVE"  -> HoverManager.handleMove(x, y);
            default      -> { /* ignore */ }
        }
    }

    private static void handleUi(String playerId, JsonNode p) throws Exception {
        if (p == null) return;
        String action = p.path("action").asText("");
        switch (action) {
            case "OPEN_SHOP"    -> { GameState.setRunning(false); UI.shopOpen(); }
            case "SELECT_ITEM"  -> Shop.buy(p.path("itemId").asText(""));
            case "CLOSE_SHOP"   -> { UI.shopClose(); GameState.setRunning(true); }
            case "PAUSE"        -> GameController.pause();
            case "RESUME"       -> GameController.resume();
            default             -> UI.onAction(action, p);
        }
    }

    // ===== Helpers (compat storage) =====
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
            case "block-systems": ps.blockSystems = json; break;
            case "connections":   ps.connections  = json; break;
            case "wires":         ps.wires        = json; break;
            case "packets":       ps.packets      = json; break;
            default: /* ignore */ ;
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

    // ===== IO helpers (line-delimited) =====
    private static void writeLine(BufferedWriter out, String json) throws IOException {
        out.write(json);
        out.write('\n');
        out.flush();
    }
    private static String ok() { return "{\"ok\":true}"; }
    private static String okWith(String kvPairs) { return "{\"ok\":true," + kvPairs + "}"; }
    private static String err(String msg) { return "{\"ok\":false,\"error\":\"" + msg + "\"}"; }
    private static String text(JsonNode n, String f) { return (n != null && n.get(f) != null && n.get(f).isTextual()) ? n.get(f).asText() : null; }

    // ===== Security =====
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
