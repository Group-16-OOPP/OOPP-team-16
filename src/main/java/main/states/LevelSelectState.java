package main.states;

import java.awt.Graphics;

import audio.controller.AudioController;
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

    @Override
    public void onEnter() {
        AudioController.getInstance().playJump();
    }
}
