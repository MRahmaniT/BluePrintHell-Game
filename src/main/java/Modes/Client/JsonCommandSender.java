package Modes.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public final class JsonCommandSender {

    private final String host;
    private final int port;
    private final String playerId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCommandSender(String host, int port, String playerId) {
        this.host = host; this.port = port; this.playerId = playerId;
    }

    private void send(ObjectNode node) {
        node.put("playerId", playerId);
        try (Socket s = new Socket(host, port);
             var out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            String json = objectMapper.writeValueAsString(node);
            out.write(json);
            out.write('\n'); // IMPORTANT: line-delimited for server readLine()
            out.flush();
        } catch (Exception e) {
            System.err.println("[JsonCommandSender] " + e.getMessage());
        }
    }

    public void sendKey(int keyCode, String keyName, boolean pressed) {
        var root = objectMapper.createObjectNode();
        root.put("cmd", "KEY");
        var payload = root.putObject("payload");
        payload.put("keyCode", keyCode);
        payload.put("keyName", keyName);
        payload.put("pressed", pressed);
        send(root);
    }

    public void sendMouse(String type, int button, double x, double y) {
        var root = objectMapper.createObjectNode();
        root.put("cmd", "MOUSE");
        var payload = root.putObject("payload");
        payload.put("type", type); // "Pressed" | "Released" | "CLICK" | "DRAG" | "MOVE"
        payload.put("button", button);
        payload.put("x", x);
        payload.put("y", y);
        send(root);
    }

    public void sendUi(String action, String payloadJsonNullable) {
        var root = objectMapper.createObjectNode();
        root.put("cmd", "UI");
        var payload = root.putObject("payload");
        payload.put("action", action);
        if (payloadJsonNullable != null) {
            try { payload.setAll((ObjectNode) objectMapper.readTree(payloadJsonNullable)); }
            catch (Exception ignored) {}
        }
        send(root);
    }
}
