package Player;

public class PlayerState {
    private static Player currentPlayer;

    public static void setPlayer(Player player) {
        currentPlayer = player;
    }

    public static Player getPlayer() {
        return currentPlayer;
    }

    public static boolean isPlayerLoggedIn() {
        return currentPlayer != null;
    }

    public static void logout() {
        currentPlayer = null;
    }
}
