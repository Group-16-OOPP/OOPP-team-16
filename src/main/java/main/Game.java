package main;

import java.awt.Graphics;

import Levels.LevelManager;
import entities.Player;

public class Game implements Runnable{

   private GamePanel gamePanel;
   private GameWindow gameWindow;
   private Thread gametThread;
   private final int FPS_SET = 120;
   private final int UPS_SET = 200;

   private Player player;
   private LevelManager levelManager;

   public final static int TILES_DEAFULT_SIZE = 32;
   public final static float  SCALE = 1.0f;
   public final static int  TILES_IN_WIDTH = 40;
   public final static int  TILES_IN_HEIGHT = 25;
   public final static int  TILES_SIZE = (int)(TILES_DEAFULT_SIZE * SCALE);
   public final static int  GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
   public final static int  GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

  public Game() {
    initClasses();
    gamePanel = new GamePanel(this);
    gameWindow = new GameWindow(gamePanel);
    gamePanel.requestFocus();
    
    startGameLoop();
  }

  private void initClasses() {
    levelManager = new LevelManager(this);
    player = new Player(200,550, (int)(32 * SCALE),(int)(32 * SCALE));
    player.loadLvlData(levelManager.getCurrentLvl().getLevelData());

  }

  private void update() {
    player.update();
    levelManager.update();
  }

  public void render(Graphics g){
    levelManager.draw(g);
    player.render(g);
  }

  private void startGameLoop(){
    gametThread = new Thread(this);
    gametThread.start();
  }





    @Override
    public void run() {
      double timePerFrame = 1000000000.0 / FPS_SET;
      double timePerUpdate = 1000000000.0 / UPS_SET;

      long previousTime = System.nanoTime();

      int frames = 0;
      int updates = 0;
      long lastCheck = System.currentTimeMillis();

      double deltaU = 0;
      double deltaF = 0;

      while (true) { 
        long currentTime = System.nanoTime();

        deltaU += (currentTime - previousTime) / timePerUpdate;
        deltaF += (currentTime - previousTime) / timePerFrame;
        previousTime = currentTime;
        if (deltaU >= 1) {
          update();
          updates ++;
          deltaU --;
        }
        if (deltaF >= 1) {
          gamePanel.repaint();
          frames++;
          deltaF--;
        }
        if (System.currentTimeMillis() - lastCheck >= 1000) {
          lastCheck = System.currentTimeMillis();
          System.out.println("FPS: " + frames + "UPS: " + updates);
          frames = 0;
          updates = 0;
        }
      }
    }

    public Player getPlayer(){
      return player;
    }

    public void windowFocusLost() {
      player.resetDirBooleans();
    }
 }
 