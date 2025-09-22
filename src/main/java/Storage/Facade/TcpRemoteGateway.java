package Storage.Facade;

import MVC.Model.GameEntities.BlockSystem;
import MVC.Model.GameEntities.Connection;
import MVC.Model.GameEntities.GameData;
import MVC.Model.GameEntities.Packet;
import MVC.Model.GameEntities.Wire.Wire;
import Modes.Security.HmacSigner;
import Storage.RealTime.GameEnvironment.GameDataStorage;
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

    private String sessionKeyBase64;

    public TcpRemoteGateway(String host, int port, String playerId) {
        this(host, port, playerId, 5000);
    }
    public TcpRemoteGateway(String host, int port, String playerId, int timeoutMs) {
        this.host = host;
        this.port = port;
        this.playerId = playerId;
        this.timeoutMs = timeoutMs;
        // handshake
        JsonNode helloReq = M.createObjectNode()
                .put("op", "hello")
                .put("playerId", playerId);
        JsonNode helloRes;
        try {
            helloRes = send(helloReq);
        } catch (IOException e) {
            throw new RuntimeException("Handshake failed (hello): " + e.getMessage(), e);
        }
        if (!helloRes.path("ok").asBoolean(false)) {
            throw new RuntimeException("Handshake rejected: " + helloRes.path("error").asText());
        }
        this.sessionKeyBase64 = helloRes.path("key").asText(null);
        if (this.sessionKeyBase64 == null || this.sessionKeyBase64.isBlank()) {
            throw new RuntimeException("Handshake missing session key");
        }
    }

    // -------- Loads --------
    @Override public List<BlockSystem> loadBlockSystems() { return getList("block-systems", new TypeReference<>(){}); }
    @Override public List<Connection>  loadConnections()  { return getList("connections",   new TypeReference<>(){}); }
    @Override public List<Wire>        loadWires()        { return getList("wires",         new TypeReference<>(){}); }
    @Override public List<Packet>      loadPackets()      { return getList("packets",       new TypeReference<>(){}); }
    @Override public GameData          loadGameData()     { return get("gameData",      new TypeReference<>(){}); }

    // -------- Saves --------
    @Override public void saveBlockSystems(List<BlockSystem> v) { putList("block-systems", v); }
    @Override public void saveConnections(List<Connection> v)   { putList("connections",   v); }
    @Override public void saveWires(List<Wire> v)               { putList("wires",         v); }
    @Override public void savePackets(List<Packet> v)           { putList("packets",       v); }
    @Override public void saveGameData(GameData v)              { put("gameData",      v); }

    // -------- Helpers --------
    private JsonNode sendSigned(String op, String entity, String bodyOrNull) throws IOException {
        long ts = System.currentTimeMillis();
        String nonce = HmacSigner.randomNonce();
        String sig = HmacSigner.signBase64(sessionKeyBase64, op, entity, playerId, bodyOrNull, ts, nonce);

        var node = M.createObjectNode()
                .put("op", op)
                .put("entity", entity)
                .put("playerId", playerId)
                .put("ts", ts)
                .put("nonce", nonce)
                .put("sig", sig);

        if (bodyOrNull != null) {
            // body is a JSON array string; attach as parsed JSON
            try {
                node.set("data", M.readTree(bodyOrNull));
            } catch (Exception e) {
                // should not happen; bodyOrNull comes from mapper.writeValueAsString(list)
                node.put("data_raw", bodyOrNull);
            }
        }
        return send(node);
    }

    private <T> List<T> getList(String entity, TypeReference<List<T>> tr) {
        try {
            JsonNode res = sendSigned("get", entity, null);
            if (!res.path("ok").asBoolean(false)) {
                throw new RuntimeException("Server error: " + res.path("error").asText());
            }
            JsonNode data = res.get("data");
            return M.convertValue(data, tr);
        } catch (Exception e) {
            throw new RuntimeException("GET " + entity + " error", e);
        }
    }
    private <T> T get(String entity, TypeReference<T> tr) {
        try {
            JsonNode res = sendSigned("get", entity, null);
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
            String body = M.writeValueAsString(list);
            JsonNode res = sendSigned("put", entity, body);
            if (!res.path("ok").asBoolean(false)) {
                throw new RuntimeException("Server error: " + res.path("error").asText());
            }
        } catch (Exception e) {
            throw new RuntimeException("PUT " + entity + " error", e);
        }
    }
    private <T> void put(String entity, T list) {
        try {
            String body = M.writeValueAsString(list);
            JsonNode res = sendSigned("put", entity, body);
            if (!res.path("ok").asBoolean(false)) {
                throw new RuntimeException("Server error: " + res.path("error").asText());
            }
        } catch (Exception e) {
            throw new RuntimeException("PUT " + entity + " error", e);
        }
    }


    // In TcpRemoteGateway.java

    private JsonNode send(JsonNode req) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            // Use the correct line-delimited text protocol
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), java.nio.charset.StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));

            // 1. Write the JSON as a string followed by a newline
            String jsonRequest = M.writeValueAsString(req);
            out.write(jsonRequest);
            out.write('\n');
            out.flush();

            // 2. Read the response line from the server
            String jsonResponse = in.readLine();
            if (jsonResponse == null) {
                throw new IOException("Server closed connection or sent empty response.");
            }
            return M.readTree(jsonResponse);
        }
    }
}
