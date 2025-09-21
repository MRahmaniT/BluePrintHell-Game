package MVC.View.ModeSelect;

import Modes.AppState;
import Modes.Client.JsonCommandSender;
import Modes.OnlineBootstrapper;
import Storage.Facade.StorageFacade;
import Storage.Facade.TcpRemoteGateway;

import javax.swing.*;

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

            // keep legacy gateway for bootstrap (optional)
            var gateway = new TcpRemoteGateway(host, port, playerId);
            StorageFacade.useRemote(gateway);
            OnlineBootstrapper.bootstrap(gateway); // اگر نیاز داری

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
}
