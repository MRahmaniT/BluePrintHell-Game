package Player;

public class Player {
    private String username;
    private String password; // optional

    public Player() {
        //For Jackson
    }
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    //Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Player: " + username;
    }
}
