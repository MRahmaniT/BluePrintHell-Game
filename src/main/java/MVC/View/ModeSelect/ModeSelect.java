package MVC.View.ModeSelect;

import Modes.AppState;
import Modes.Client.JsonCommandSender;
import Modes.OnlineBootstrapper;
import Storage.Facade.StorageFacade;
import Storage.Facade.TcpRemoteGateway;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public final class ModeSelect {
    public static boolean askAndConfigure() {
        String[] options = {"Online Mode", "Offline Mode", "Close"};
        int r = JOptionPane.showOptionDialog(null,
                "Choose mode:",
                "Game Mode Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (r == 0) {
            String host = JOptionPane.showInputDialog(null, "Server Host", "127.0.0.1");
            if (host == null || host.isBlank()) return false;
            String portStr = JOptionPane.showInputDialog(null, "Server Port", "5050");
            if (portStr == null || portStr.isBlank()) return false;
            int port = Integer.parseInt(portStr);

            String playerId = JOptionPane.showInputDialog(null, "Player ID", System.getProperty("user.name"));
            if (playerId == null || playerId.isBlank()) return false;

            try {
                String key = performHandshake(host, port, playerId);
                if (key == null || key.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Handshake failed: Missing session key.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                AppState.sessionKey = key; // Store the single key globally
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Handshake failed: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // keep legacy gateway for bootstrap (optional)
            var gateway = new TcpRemoteGateway(host, port, playerId);
            StorageFacade.useRemote(gateway);
            OnlineBootstrapper.bootstrap(gateway);

            // set global ONLINE mode + sender
            AppState.mode = AppState.GameMode.ONLINE;
            AppState.playerId = playerId;
            AppState.serverHost = host;
            AppState.serverPort = port;
            AppState.sender = new JsonCommandSender(host, port, playerId);

            return true;
        } else if (r == 1) {
            StorageFacade.useLocal();
            AppState.mode = AppState.GameMode.OFFLINE;
            AppState.playerId = null;
            AppState.sender = null;
            return true;
        }
        return false;
    }
    private static String performHandshake(String host, int port, String playerId) throws IOException {
        try (Socket s = new Socket(host, port);
             var out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
             var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8))) {

            var M = new ObjectMapper();
            ObjectNode req = M.createObjectNode();
            req.put("op", "hello");
            req.put("playerId", playerId);

            out.write(M.writeValueAsString(req));
            out.write('\n');
            out.flush();

            String line = in.readLine();
            if (line == null) throw new IOException("No hello response from server.");

            JsonNode res = M.readTree(line);
            if (!res.path("ok").asBoolean(false)) throw new IOException("Server rejected handshake: " + res.path("error").asText());

            return res.path("key").asText(null);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
