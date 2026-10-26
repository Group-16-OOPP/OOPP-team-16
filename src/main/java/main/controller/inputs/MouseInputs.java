package main.controller.inputs;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.controller.Game;
import main.controller.GameStateManager;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private final GameStateManager gameStateManager;
    private final Game game; // only for menu class delegation
    public MouseInputs(GameStateManager gameStateManager, Game game)
    {
        this.gameStateManager = gameStateManager;
        this.game = game;
    }
    @Override
    public void mouseDragged(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            game.mainMenu.mouseMoved(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            game.levelSelect.mouseMoved(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
     
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            game.mainMenu.mouseClicked(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            game.levelSelect.mouseClicked(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            game.mainMenu.mousePressed(e.getX(), e.getY());
        }else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            game.levelSelect.mousePressed(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
       
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            game.mainMenu.mouseReleased(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            game.levelSelect.mouseReleased(e.getX(), e.getY());
        }
    }

}
