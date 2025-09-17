package Server;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpInfernoServer {

    static class Session {
        final String keyBase64;
        final long createdAt;
        final LinkedHashSet<String> recentNonces = new LinkedHashSet<String>() {
            private boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) { return size() > 1024; }
        };
        Session(String keyBase64) { this.keyBase64 = keyBase64; this.createdAt = System.currentTimeMillis(); }
    }
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SecureRandom RNG = new SecureRandom();

    private static final int PORT = Integer.parseInt(System.getProperty("PORT", "5050"));
    private static final Path ROOT = Paths.get(System.getProperty("DATA_DIR", "server_data")).toAbsolutePath();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static class PlayerStore {
        volatile String blockSystems = "[]";
        volatile String connections  = "[]";
        volatile String wires        = "[]";
        volatile String packets      = "[]";
    }
    private static final ConcurrentHashMap<String, PlayerStore> DataBase = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(ROOT);
        try (ServerSocket ss = new ServerSocket(PORT)) {
            System.out.println("[TcpInfernoServer] Listening on tcp://127.0.0.1:" + PORT);
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> handle(s), "client-" + s.getRemoteSocketAddress()).start();
            }
        }
    }

    private static void handle(Socket s) {

        try (s; DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()))) {

            while (true) {
                String requestString = readFrame(in);
                if (requestString == null) break;
                JsonNode request = objectMapper.readTree(requestString);

                //Security
                String op = text(request, "op");
                String entity = text(request, "entity"); // may be null for hello
                String playerId = text(request, "playerId");
                if (op == null || playerId == null) {
                    writeFrame(out, "{\"ok\":false,\"error\":\"missing fields\"}");
                    return;
                }
                if ("hello".equals(op)) {
                    Session session = new Session(newSessionKeyBase64());
                    SESSIONS.put(playerId, session);
                    String res = "{\"ok\":true,\"key\":\"" + session.keyBase64 + "\",\"ts\":" + System.currentTimeMillis() + "}";
                    writeFrame(out, res);
                    return;
                }

                Session session = SESSIONS.get(playerId);
                if (session == null) {
                    writeFrame(out, "{\"ok\":false,\"error\":\"no session - call hello first\"}");
                    return;
                }

                long ts = request.path("ts").asLong(0L);
                String nonce = text(request, "nonce");
                String sig = text(request, "sig");
                if (nonce == null || sig == null || ts == 0L) {
                    writeFrame(out, "{\"ok\":false,\"error\":\"missing ts/nonce/sig\"}");
                    return;
                }
                long now = System.currentTimeMillis();
                if (Math.abs(now - ts) > 60_000) {
                    writeFrame(out, "{\"ok\":false,\"error\":\"ts skew\"}");
                    return;
                }
                synchronized (session.recentNonces) {
                    if (session.recentNonces.contains(nonce)) {
                        writeFrame(out, "{\"ok\":false,\"error\":\"replayed nonce\"}");
                        return;
                    }
                    session.recentNonces.add(nonce);
                    if (session.recentNonces.size() > 2048) {
                        // drop oldest
                        var it = session.recentNonces.iterator();
                        it.next(); it.remove();
                    }
                }

                String bodyForSig;
                if ("put".equals(op)) {
                    JsonNode data = request.get("data");
                    if (data == null || !data.isArray()) {
                        writeFrame(out, "{\"ok\":false,\"error\":\"data must be array\"}");
                        return;
                    }
                    bodyForSig = data.toString();
                } else {
                    bodyForSig = "";
                }

                if (!verifyHmac(session.keyBase64, op, entity, playerId, bodyForSig, ts, nonce, sig)) {
                    writeFrame(out, "{\"ok\":false,\"error\":\"bad signature\"}");
                    return;
                }
                // -------------------------------------------

                PlayerStore ps = DataBase.computeIfAbsent(playerId, TcpInfernoServer::loadFromDisk);

                switch (op) {
                    case "get": {
                        assert entity != null;
                        String data = getEntity(ps, entity);
                        String res = "{\"ok\":true,\"data\":" + data + "}";
                        writeFrame(out, res);
                        break;
                    }
                    case "put": {
                        JsonNode data = request.get("data");
                        if (data == null || !data.isArray()) {
                            writeFrame(out, "{\"ok\":false,\"error\":\"data must be array\"}");
                            break;
                        }
                        String json = objectMapper.writeValueAsString(data);
                        assert entity != null;
                        setEntity(ps, entity, json);
                        saveToDisk(playerId, fileName(entity), json);
                        writeFrame(out, "{\"ok\":true}");
                        break;
                    }
                    default:
                        writeFrame(out, "{\"ok\":false,\"error\":\"unknown op\"}");
                }
            }
        } catch (Exception e) {
            // log and close
            e.printStackTrace();
        }
    }

    private static String text(JsonNode n, String f) { return (n.get(f) != null && n.get(f).isTextual()) ? n.get(f).asText() : null; }

    private static String getEntity(PlayerStore ps, String entity) {
        return switch (entity) {
            case "block-systems" -> ps.blockSystems;
            case "connections" -> ps.connections;
            case "wires" -> ps.wires;
            case "packets" -> ps.packets;
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
            case "connections" -> "Connections.json";
            case "wires" -> "Wires.json";
            case "packets" -> "Packets.json";
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

    private static String readFrame(DataInputStream in) throws IOException {
        try {
            int len = in.readInt();
            if (len <= 0 || len > (32 * 1024 * 1024)) return null;
            byte[] buf = in.readNBytes(len);
            if (buf.length != len) return null;
            return new String(buf, StandardCharsets.UTF_8);
        } catch (EOFException eof) {
            return null;
        }
    }
    private static void writeFrame(DataOutputStream out, String json) throws IOException {
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        out.writeInt(b.length);
        out.write(b);
        out.flush();
    }

    //security
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
