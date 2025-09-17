package Server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;

public class TcpInfernoServer {

    private static final int PORT = Integer.parseInt(System.getProperty("PORT", "5050"));
    private static final Path ROOT = Paths.get(System.getProperty("DATA_DIR", "server_data")).toAbsolutePath();
    private static final ObjectMapper M = new ObjectMapper();

    static class PlayerStore {
        volatile String blockSystems = "[]";
        volatile String connections  = "[]";
        volatile String wires        = "[]";
        volatile String packets      = "[]";
    }
    private static final ConcurrentHashMap<String, PlayerStore> DB = new ConcurrentHashMap<>();

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
                String reqStr = readFrame(in);
                if (reqStr == null) break;
                JsonNode req = M.readTree(reqStr);

                String op = text(req, "op");
                String entity = text(req, "entity");
                String playerId = text(req, "playerId");
                if (op == null || entity == null || playerId == null) {
                    writeFrame(out, "{\"ok\":false,\"error\":\"missing fields\"}");
                    continue;
                }
                PlayerStore ps = DB.computeIfAbsent(playerId, TcpInfernoServer::loadFromDisk);

                switch (op) {
                    case "get": {
                        String data = getEntity(ps, entity);
                        String res = "{\"ok\":true,\"data\":" + data + "}";
                        writeFrame(out, res);
                        break;
                    }
                    case "put": {
                        JsonNode data = req.get("data");
                        if (data == null || !data.isArray()) {
                            writeFrame(out, "{\"ok\":false,\"error\":\"data must be array\"}");
                            break;
                        }
                        String json = M.writeValueAsString(data);
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
        switch (entity) {
            case "block-systems": return ps.blockSystems;
            case "connections":   return ps.connections;
            case "wires":         return ps.wires;
            case "packets":       return ps.packets;
            default: return "[]";
        }
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
        switch (entity) {
            case "block-systems": return "BlockSystems.json";
            case "connections":   return "Connections.json";
            case "wires":         return "Wires.json";
            case "packets":       return "Packets.json";
            default: return "Unknown.json";
        }
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
}
