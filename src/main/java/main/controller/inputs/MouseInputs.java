package main.controller.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.controller.GameStateManager;
import main.view.states.LevelSelect;
import main.view.states.MainMenu;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private final GameStateManager gameStateManager;
    private final MainMenu mainMenu;
    private final LevelSelect levelSelect;

    public MouseInputs(GameStateManager gameStateManager, MainMenu mainMenu, LevelSelect levelSelect) {
        this.gameStateManager = gameStateManager;
        this.mainMenu = mainMenu;
        this.levelSelect = levelSelect;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            mainMenu.mouseMoved(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            levelSelect.mouseMoved(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            mainMenu.mouseClicked(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            levelSelect.mouseClicked(e.getX(), e.getY());
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
            mainMenu.mousePressed(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            levelSelect.mousePressed(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameStateManager.getGameState() == GameStateManager.GameState.MENU) {
            mainMenu.mouseReleased(e.getX(), e.getY());
        } else if (gameStateManager.getGameState() == GameStateManager.GameState.LEVEL_SELECT) {
            levelSelect.mouseReleased(e.getX(), e.getY());
        }
    }
}
