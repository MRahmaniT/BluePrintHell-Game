package MVC.View.GameEnvironment.Options;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {
    private final JLabel wireLabel;
    private final JLabel timeLabel;
    private final JLabel lossLabel;
    private final JLabel coinLabel;

    public HUDPanel(int width, int height, int fontSize) {
        setLayout(new GridLayout(4, 1));
        setBounds(10, height - 160, (int) (0.2*width - 20), 150);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 150));

        wireLabel = createHUDLabel(fontSize);
        timeLabel = createHUDLabel(fontSize);
        lossLabel = createHUDLabel(fontSize);
        coinLabel = createHUDLabel(fontSize);

        add(wireLabel);
        add(timeLabel);
        add(lossLabel);
        add(coinLabel);
    }

    private JLabel createHUDLabel(int fontSize) {
        JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Consolas", Font.BOLD, fontSize));
        return label;
    }

    public void update(double remainingWire, String time, int lostPackets, int totalPackets, int coins) {
        wireLabel.setText("WireShape Left: " + (int) remainingWire + " px");
        timeLabel.setText("Time: " + time);
        lossLabel.setText("Packet Loss: " + lostPackets + "/" + totalPackets);
        coinLabel.setText("Coins: " + coins);
    }
}

