package main.view.interfaces;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.view.states.LevelSelect;

//level selection screen
public class LevelSelectState extends GameBaseState {

    private final LevelSelect levelSelect;

    public LevelSelectState(LevelSelect levelSelect) {
        this.levelSelect = levelSelect;
    }

    @Override
    public void update() {
        levelSelect.update();
    }

    @Override
    public void render(Graphics g) {
        levelSelect.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playJump();
    }
}
