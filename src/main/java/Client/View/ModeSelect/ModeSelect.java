package Client.View.ModeSelect;

import Client.Storage.Facade.StorageFacade;
import Client.Storage.Facade.TcpRemoteGateway;

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
            String host = javax.swing.JOptionPane.showInputDialog(null, "Server Host", "127.0.0.1");
            if (host == null || host.isBlank()) return false;
            String portStr = javax.swing.JOptionPane.showInputDialog(null, "Server Port", "5050");
            if (portStr == null || portStr.isBlank()) return false;
            int port = Integer.parseInt(portStr);

            String playerId = javax.swing.JOptionPane.showInputDialog(null, "Player ID", System.getProperty("user.name"));
            if (playerId == null || playerId.isBlank()) return false;

            var gateWay = new TcpRemoteGateway(host, port, playerId);
            StorageFacade.useRemote(gateWay);
            Client.Services.OnlineBootstrapper.bootstrap(gateWay);

            return true;
        } else if (r == 1) {
            StorageFacade.useLocal();
            return true;
        }
        return false;
    }
}
