package Player;

public class Player {
    private String username;
    private String password;
    private int goldCount, levelNumber;
    private boolean isLogin;

    public Player() {
        //For Jackson
    }
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.goldCount = 0;
        this.isLogin = false;
        this.levelNumber = 1;
    }

    //Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getGoldCount(){return goldCount;}

    public int getLevelNumber(){return levelNumber;}

    public boolean isLogin(){return isLogin;}

    //Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGoldCount(int goldCount) {
        this.goldCount = goldCount;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public String toString() {
        return "Player: " + username;
    }
}
