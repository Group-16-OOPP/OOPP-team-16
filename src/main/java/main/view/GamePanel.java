package main.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.function.Consumer;

import javax.swing.JPanel;

import static main.controller.Game.GAME_HEIGHT;
import static main.controller.Game.GAME_WIDTH;
import main.controller.inputs.KeyboardInputs;
import main.controller.inputs.MouseInputs;

public class GamePanel extends JPanel {
    private final Consumer<Graphics> renderAction;

    public GamePanel(Consumer<Graphics> renderAction, KeyboardInputs keyboardInputs,
            MouseInputs mouseInputs) {
        this.renderAction = renderAction;
        setPanelSize();
        addKeyListener(keyboardInputs);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("size:" + GAME_WIDTH + " : " + GAME_HEIGHT);
    }

    public void updateGame() {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderAction.accept(g);
    }
}
