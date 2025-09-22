package MVC.View.GamePage;

import MVC.Controller.GameLogic;
import MVC.Controller.Systems.ChangeBlocksLight;
import MVC.Model.GameEntities.Packet;

import MVC.View.GamePage.State.*;
import MVC.View.Main.MainFrame;
import Modes.AppState;
import Modes.Client.LocalInputSink;
import Modes.Client.RemoteInputSink;
import Modes.InputSink;
import Storage.Facade.StorageFacade;
import Storage.RealTime.Snapshots.PacketSnapshots;
import MVC.View.GameEnvironment.Background.BuildBackground;
import MVC.View.GameEnvironment.Options.HUDPanel;
import MVC.View.GameEnvironment.Options.ShopPanel;

import MVC.View.Render.GameShapes.System.GameShape;

import MVC.View.Render.GameShapes.Wire.WireShape;
import MVC.View.Render.GameShapes.Packet.PacketRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    //For Resolution
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public int screenSizeX = screenSize.width;
    public int screenSizeY = screenSize.height;
    int fontSize = screenSizeX / 80;
    int buttonsWidth = screenSizeX / 10;
    int buttonsHeight = screenSizeY / 20;
    int buttonSpace = 10;

    //For Background
    private Image backgroundImage;
    private final List<GameShape> shapes = new ArrayList<>();

    private int mousePointX;
    private int mousePointY;

    //For HUD
    private final HUDPanel hudPanel;

    //For Shop
    private final ShopPanel shopPanel;

    //For Pause
    private PausePanel pausePanel;

    //For GameOver
    private final GameOverPanel gameOverPanel;

    //For Win
    private final WinPanel winPanel;

    //For Save and Load
    private SavePanel savePanel;
    private LoadPanel loadPanel;

    private final GameLogic gameLogic;

    Thread paint;

    private final InputSink input;
    public GamePanel(){
        if (AppState.mode == AppState.GameMode.ONLINE) {
            input = new RemoteInputSink(AppState.serverHost, AppState.serverPort, AppState.playerId);
            gameLogic = new GameLogic(this, input, true);
        } else if (AppState.mode == AppState.GameMode.OFFLINE){
            input = new LocalInputSink(this);
            gameLogic = new GameLogic(this, input, false);
        } else {
            input = new LocalInputSink(this);
            gameLogic = new GameLogic(this, input, false);
        }


        setLayout(null);

        //Add Save and Load
        savePanel = new SavePanel(
                this.gameLogic::saveGame,
                this.gameLogic::returnToMenu,
                () -> {
                    savePanel.setVisible(false);
                    this.gameLogic.getGameTimer().start();
                    this.gameLogic.setRunning(true); }
        );
        savePanel.setBounds(0, 0, screenSizeX, screenSizeY);
        savePanel.setVisible(false);
        add(savePanel);
        setComponentZOrder(savePanel, 0);


        if (AppState.mode == AppState.GameMode.ONLINE) {
            // Online Mode Actions
            loadPanel = new LoadPanel(
                    () -> { // Action for "Load Data"
                        gameLogic.setMadeDecision(true);
                        loadPanel.setVisible(false);
                    },
                    () -> { // Action for "Start New Game"
                        gameLogic.startNewOnlineGame();
                        loadPanel.setVisible(false);
                    },
                    this.gameLogic::returnToMenu // "Back to Menu" can be the same
            );
        } else {
            // Offline Mode Actions (your original code)
            loadPanel = new LoadPanel(
                    () -> {
                        loadPanel.setVisible(false);
                    },
                    this.gameLogic::retryLevel,
                    this.gameLogic::returnToMenu
            );
        }
        loadPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        loadPanel.setVisible(false);
        add(loadPanel);
        setComponentZOrder(loadPanel, 0);

        //Add Background
        try {
            backgroundImage = ImageIO.read(new File("Resources/background2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BuildBackground.buildBackground(screenSizeX, screenSizeY, shapes);

        //Add Shop Button
        JButton shopButton = new JButton("Shop");
        shopButton.setBounds(screenSizeX - buttonsWidth - buttonSpace,
                             screenSizeY - buttonsHeight - buttonSpace,
                                buttonsWidth, buttonsHeight);
        shopButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        shopButton.setFocusable(false);
        add(shopButton);


        JLabel timeLabel = new JLabel(gameLogic.getTimeController().getFormattedTime());
        timeLabel.setBounds((int) (0.025*screenSizeX),
                            (int) (0.025*screenSizeY),
                            (int) (0.1f*screenSizeX),
                            (int) (0.1f*screenSizeY));
        timeLabel.setFont(new Font("Arial", Font.BOLD, (int) (1.5*fontSize)));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setText("DAY 1");
        add(timeLabel);

        //Add HUD
        hudPanel = new HUDPanel(screenSizeX, screenSizeY, fontSize);
        hudPanel.setVisible(false);
        add(hudPanel);
        setComponentZOrder(hudPanel, 0);

        //Add Shop
        shopPanel = new ShopPanel(this);
        shopPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        shopPanel.setVisible(false);
        add(shopPanel);
        setComponentZOrder(shopPanel, 0);

        //Add Pause
        pausePanel = new PausePanel(
                () -> {
                    pausePanel.setVisible(false);
                    gameLogic.getGameTimer().start();
                    this.gameLogic.setRunning(true); },
                this.gameLogic::retryLevel,
                this.gameLogic::showSavePanel
        );
        pausePanel.setBounds(0, 0, screenSizeX, screenSizeY);
        pausePanel.setVisible(false);
        add(pausePanel);
        setComponentZOrder(pausePanel, 0);

        //Add GameOver
        gameOverPanel = new GameOverPanel(
                this.gameLogic::retryLevel,
                this.gameLogic::returnToMenu
        );
        gameOverPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        gameOverPanel.setVisible(false);
        add(gameOverPanel);
        setComponentZOrder(gameOverPanel, 0);

        //Add Win
        winPanel = new WinPanel(
                this.gameLogic::proceedToNextLevel,
                this.gameLogic::retryLevel,
                this.gameLogic::returnToMenu
        );
        winPanel.setBounds(0, 0, screenSizeX, screenSizeY);
        winPanel.setVisible(false);
        add(winPanel);
        setComponentZOrder(winPanel, 0);

        // Painter
        paint = new Thread(this::repaint);

        // run logic
        if (this.isVisible()) {
            gameLogic.Run();
        }
        if (!gameLogic.isMadeDecision()) {
            loadPanel.setVisible(true);
        }

        shopButton.addActionListener(_ ->input.uiAction("OPEN_SHOP", null));

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                input.keyTyped(e.getKeyChar(), e.getKeyCode());
            }
            @Override
            public void keyPressed(KeyEvent e) {
                input.keyPressed(e.getKeyCode(), KeyEvent.getKeyText(e.getKeyCode()));
            }
            @Override
            public void keyReleased(KeyEvent e) {
                input.keyReleased(e.getKeyCode(), KeyEvent.getKeyText(e.getKeyCode()));
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { input.mouseClick(e.getButton(), mousePointX, mousePointY); }
            @Override public void mousePressed(MouseEvent e)  { input.mouseDown(e.getButton(),  mousePointX, mousePointY); }
            @Override public void mouseReleased(MouseEvent e) { input.mouseUp(e.getButton(),    mousePointX, mousePointY); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                mousePointX = e.getX() - getWidth() / 2;
                mousePointY = e.getY() - getHeight() / 2;
                //input.mouseMove(mousePointX, mousePointY);
            }
            @Override public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
                //input.mouseDrag(e.getButton(), mousePointX, mousePointY);
            }
        });

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    //Paint
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2d.translate(cx, cy);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        //Shapes
        for(GameShape gameShape : shapes){
            gameShape.draw(g2d);
        }

        //Block Shapes
        if (!gameLogic.getBlockShapes().isEmpty()) {
            ChangeBlocksLight.changeBlocksLight(gameLogic.getBlockShapes());

            for (GameShape gameShape : gameLogic.getBlockShapes()) {
                gameShape.draw(g2d);
            }

            //For Lines
            for (WireShape wireShape : gameLogic.getWireShapes()) {
                wireShape.draw(g2d);
            }
        }

        if (gameLogic.getWiringManager().isDragging()) {
            gameLogic.getWiringManager().drawDrag(g2d, new Point(mousePointX, mousePointY));
        }
        if (gameLogic.getWiringManager().isFilleting()) {
            gameLogic.getWiringManager().drawFillet(new Point(mousePointX, mousePointY));
        }
        if (gameLogic.getBlockManager().isDragging()) {
            gameLogic.getBlockManager().drawDrag(mousePointX, mousePointY);
        }

        //intersect
        boolean willIntersect = false;
        for (GameShape blockShape : gameLogic.getBlockShapes()) {
            for (WireShape wireShape : gameLogic.getWireShapes()) {
                Shape s1 = blockShape.getShape();
                BasicStroke stroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                if (wireShape.getWirePath() == null) continue;
                Shape s2 = stroke.createStrokedShape(wireShape.getWirePath());
                if (s1 == null || s2 == null) continue;

                Area a1 = new Area(s1);
                a1.intersect(new Area(s2));

                g2d.setColor(Color.red);
                g2d.draw(a1);

                if (!a1.isEmpty()){
                    willIntersect = true;
                }
            }
        }
        gameLogic.setIntersected(willIntersect);


        //Packet
        if (!gameLogic.isInterrupted()) {
            List<Packet> packets = StorageFacade.loadPackets();
            for (Packet p : packets) {
                if (!p.isOnWire()) continue;
                PacketRenderer.draw(g2d, p);
            }
        } else {
            List<Packet> packets = PacketSnapshots.LoadPacketSnapshots(gameLogic.getTimeController().getTime());
            for (Packet p : packets) {
                if (!p.isOnWire()) continue;
                PacketRenderer.draw(g2d, p);
            }
        }

        g2d.dispose();
    }

    public HUDPanel getHudPanel () {
        return hudPanel;
    }

    public WinPanel getWinPanel () {
        return winPanel;
    }

    public GameOverPanel getGameOverPanel () {
        return gameOverPanel;
    }

    public PausePanel getPausePanel () {
        return pausePanel;
    }

    public ShopPanel getShopPanel () {
        return shopPanel;
    }

    public SavePanel getSavePanel () {
        return savePanel;
    }

    public Thread getPainter () {
        return paint;
    }

    public GameLogic getGameLogic () {
        return gameLogic;
    }
}
