package MVC.Model.GameEntities;

public class GameData implements java.io.Serializable {
    private double remainingWireLength;
    private String formatedTime;
    private int deliveredPackets;
    private int lostPackets;
    private int totalPackets;
    private int coins;

    public GameData(){}
    public GameData(double remainingWireLength, String formatedTime, int deliveredPackets, int lostPackets, int totalPackets, int coins) {
        this.remainingWireLength = remainingWireLength;
        this.formatedTime = formatedTime;
        this.deliveredPackets = deliveredPackets;
        this.lostPackets = lostPackets;
        this.totalPackets = totalPackets;
        this.coins = coins;
    }

    public double getRemainingWireLength() {
        return remainingWireLength;
    }

    public void setRemainingWireLength(double remainingWireLength) {
        this.remainingWireLength = remainingWireLength;
    }

    public String getFormatedTime() {
        return formatedTime;
    }

    public void setFormatedTime(String formatedTime) {
        this.formatedTime = formatedTime;
    }

    public int getDeliveredPackets() {
        return deliveredPackets;
    }

    public void setDeliveredPackets(int deliveredPackets) {
        this.deliveredPackets = deliveredPackets;
    }

    public int getLostPackets() {
        return lostPackets;
    }

    public void setLostPackets(int lostPackets) {
        this.lostPackets = lostPackets;
    }

    public int getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(int totalPackets) {
        this.totalPackets = totalPackets;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
