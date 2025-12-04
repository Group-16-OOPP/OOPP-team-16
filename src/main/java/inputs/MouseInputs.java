package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.view.GamePanel;
import main.Game;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel panel;

    public void setGamePanel(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (panel == null) {
            return;
        }
        Game game = panel.getGame();
        if (game.getGameState() == Game.GameState.MENU) {
            game.mainMenu.mouseMoved(e.getX(), e.getY());
        } else if (game.getGameState() == Game.GameState.LEVEL_SELECT) {
            game.levelSelect.mouseMoved(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (panel == null) {
            return;
        }
        Game game = panel.getGame();
        if (game.getGameState() == Game.GameState.MENU) {
            game.mainMenu.mousePressed(e.getX(), e.getY());
        }else if (game.getGameState() == Game.GameState.LEVEL_SELECT) {
            game.levelSelect.mousePressed(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

}
