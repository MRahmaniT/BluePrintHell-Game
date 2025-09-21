package Modes.Client;

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

    // NEW: session key from server hello
    private volatile String sessionKeyBase64;

    public RemoteInputSink(String host, int port, String playerId) {
        this.host = host; this.port = port; this.playerId = playerId;
        doHello(); // <-- get session key once
    }

    private void doHello() {
        try (Socket s = new Socket(host, port);
             var out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
             var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8))) {

            ObjectNode req = M.createObjectNode();
            req.put("op", "hello");
            req.put("playerId", playerId);

            out.write(M.writeValueAsString(req));
            out.write('\n');
            out.flush();

            String line = in.readLine();
            if (line == null) throw new IOException("no hello response");
            var res = M.readTree(line);
            if (!res.path("ok").asBoolean(false)) throw new IOException("hello rejected: " + res.path("error").asText());
            this.sessionKeyBase64 = res.path("key").asText(null);
            if (sessionKeyBase64 == null || sessionKeyBase64.isBlank()) throw new IOException("missing session key");

        } catch (Exception e) {
            throw new RuntimeException("[RemoteInputSink] hello failed: " + e.getMessage(), e);
        }
    }

    private void sendLine(ObjectNode root) {
        // ensure we have a session key
        if (sessionKeyBase64 == null || sessionKeyBase64.isBlank()) doHello();

        try (Socket s = new Socket(host, port);
             var out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

            long ts = System.currentTimeMillis();
            String nonce = UUID.randomUUID().toString();

            // HMAC: op="cmd", entity="input", body = payload.toString()
            String body = root.get("payload") == null ? "" : root.get("payload").toString();
            String sig  = signBase64(sessionKeyBase64, "cmd", "input", playerId, body, ts, nonce);

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
