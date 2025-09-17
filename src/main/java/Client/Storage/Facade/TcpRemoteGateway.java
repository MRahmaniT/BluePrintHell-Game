package Client.Storage.Facade;

import Client.Model.GameEntities.BlockSystem;
import Client.Model.GameEntities.Connection;
import Client.Model.GameEntities.Packet;
import Client.Model.GameEntities.Wire.Wire;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public final class TcpRemoteGateway implements RemoteGateway {

    private final String host;
    private final int port;
    private final String playerId;
    private final ObjectMapper M = new ObjectMapper();
    private final int timeoutMs;

    public TcpRemoteGateway(String host, int port, String playerId) {
        this(host, port, playerId, 5000);
    }
    public TcpRemoteGateway(String host, int port, String playerId, int timeoutMs) {
        this.host = host;
        this.port = port;
        this.playerId = playerId;
        this.timeoutMs = timeoutMs;
    }

    // -------- Loads --------
    @Override public List<BlockSystem> loadBlockSystems() { return getList("block-systems", new TypeReference<>(){}); }
    @Override public List<Connection>  loadConnections()  { return getList("connections",   new TypeReference<>(){}); }
    @Override public List<Wire>        loadWires()        { return getList("wires",         new TypeReference<>(){}); }
    @Override public List<Packet>      loadPackets()      { return getList("packets",       new TypeReference<>(){}); }

    // -------- Saves --------
    @Override public void saveBlockSystems(List<BlockSystem> v) { putList("block-systems", v); }
    @Override public void saveConnections(List<Connection> v)   { putList("connections",   v); }
    @Override public void saveWires(List<Wire> v)               { putList("wires",         v); }
    @Override public void savePackets(List<Packet> v)           { putList("packets",       v); }

    // -------- Helpers --------
    private <T> List<T> getList(String entity, TypeReference<List<T>> tr) {
        try {
            JsonNode req = M.createObjectNode()
                    .put("op", "get")
                    .put("entity", entity)
                    .put("playerId", playerId);
            JsonNode res = send(req);
            if (!res.path("ok").asBoolean(false)) {
                throw new RuntimeException("Server error: " + res.path("error").asText());
            }
            JsonNode data = res.get("data");
            return M.convertValue(data, tr);
        } catch (Exception e) {
            throw new RuntimeException("GET " + entity + " error", e);
        }
    }

    private <T> void putList(String entity, List<T> list) {
        try {
            JsonNode req = M.createObjectNode()
                    .put("op", "put")
                    .put("entity", entity)
                    .put("playerId", playerId)
                    .set("data", M.valueToTree(list));
            JsonNode res = send(req);
            if (!res.path("ok").asBoolean(false)) {
                throw new RuntimeException("Server error: " + res.path("error").asText());
            }
        } catch (Exception e) {
            throw new RuntimeException("PUT " + entity + " error", e);
        }
    }

    private JsonNode send(JsonNode req) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(timeoutMs);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            byte[] payload = M.writeValueAsBytes(req);
            out.writeInt(payload.length);
            out.write(payload);
            out.flush();

            int len = in.readInt();
            if (len <= 0 || len > (32 * 1024 * 1024)) {
                throw new IOException("Invalid frame length: " + len);
            }
            byte[] buf = in.readNBytes(len);
            if (buf.length != len) throw new EOFException("Short read");
            return M.readTree(buf);
        }
    }
}
