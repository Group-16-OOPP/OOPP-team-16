package main.states;

import java.awt.Graphics;

import main.Game;

//level selection screen
public class LevelSelectState extends GameBaseState {

    public LevelSelectState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.levelSelect.update();
    }

    @Override
    public void render(Graphics g) {
        game.levelSelect.draw(g);
    }
}
