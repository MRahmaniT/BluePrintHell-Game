package Modes;

import Modes.Client.JsonCommandSender;

public final class AppState {
    private AppState(){}

    public enum GameMode { ONLINE, OFFLINE, NONE }

    public static volatile GameMode mode = GameMode.NONE;
    public static volatile String playerId;
    public static volatile String serverHost;
    public static volatile int serverPort;

    public static volatile JsonCommandSender sender; // used in ONLINE
}
