package Modes.Client;

import Modes.AppState;
import Modes.InputSink;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static Modes.Security.HmacSigner.signBase64;

public class RemoteInputSink implements InputSink {
    private final String host;
    private final int port;
    private final String playerId;
    private final ObjectMapper M = new ObjectMapper();

    public RemoteInputSink(String host, int port, String playerId) {
        this.host = host; this.port = port; this.playerId = playerId;
    }

    private void sendLine(ObjectNode root) {

        if (AppState.sessionKey == null || AppState.sessionKey.isBlank()) {
            // No need to call doHello(). We should just fail or log an error.
            System.err.println("[RemoteInputSink] Session key is missing!");
            return;
        }

        try (Socket s = new Socket(host, port);
             var out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

            long ts = System.currentTimeMillis();
            String nonce = UUID.randomUUID().toString();

            String body = root.get("payload") == null ? "" : root.get("payload").toString();
            // Use AppState.sessionKey
            String sig  = signBase64(AppState.sessionKey, "cmd", "input", playerId, body, ts, nonce);

            root.put("playerId", playerId);
            root.put("ts", ts);
            root.put("nonce", nonce);
            root.put("sig", sig);

            out.write(M.writeValueAsString(root));
            out.write('\n');
            out.flush();
        } catch (Exception e) {
            System.err.println("[RemoteInputSink] " + e.getMessage());
        }
    }

    @Override
    public void keyTyped(char ch, int keyCode) {
        ObjectNode root = M.createObjectNode().put("cmd", "KEY");
        var p = root.putObject("payload");
        p.put("keyCode", keyCode).put("keyName", String.valueOf(Character.toUpperCase(ch))).put("pressed", true);
        sendLine(root);
    }

    @Override
    public void keyPressed(int keyCode, String keyName) {
        ObjectNode root = M.createObjectNode().put("cmd", "KEY");
        var p = root.putObject("payload");
        p.put("keyCode", keyCode).put("keyName", keyName).put("pressed", true);
        sendLine(root);
    }

    @Override
    public void keyReleased(int keyCode, String keyName) {
        ObjectNode root = M.createObjectNode().put("cmd", "KEY");
        var p = root.putObject("payload");
        p.put("keyCode", keyCode).put("keyName", keyName).put("pressed", false);
        sendLine(root);
    }

    @Override public void mouseDown(int button, int x, int y)  { sendMouse("DOWN",  button, x, y); }
    @Override public void mouseUp(int button, int x, int y)    { sendMouse("UP",    button, x, y); }
    @Override public void mouseClick(int button, int x, int y) { sendMouse("CLICK", button, x, y); }
    @Override public void mouseDrag(int button, int x, int y)  { sendMouse("DRAG",  button, x, y); }
    @Override public void mouseMove(int x, int y)              { sendMouse("MOVE", 0,      x, y); }

    @Override
    public void uiAction(String action, String payloadJson) {
        ObjectNode root = M.createObjectNode().put("cmd", "UI");
        var p = root.putObject("payload");
        p.put("action", action);
        if (payloadJson != null && !payloadJson.isBlank()) {
            try { p.setAll((ObjectNode) M.readTree(payloadJson)); } catch (Exception ignored) {}
        }
        sendLine(root);
    }

    private void sendMouse(String type, int button, int x, int y) {
        ObjectNode root = M.createObjectNode().put("cmd", "MOUSE");
        var p = root.putObject("payload");
        p.put("type", type).put("button", button).put("x", x).put("y", y);
        sendLine(root);
    }
}
