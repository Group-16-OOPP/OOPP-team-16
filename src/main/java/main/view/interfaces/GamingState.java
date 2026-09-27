package main.view.interfaces;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;

//State responsible for gameplay (levels, player, HUD, pause, transitions)
public class GamingState extends GameBaseState {

    public GamingState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.updateGameState();
    }

    @Override
    public void render(Graphics g) {
        game.renderGame(g);
    }

    @Override
    public void onEnter() {
        AudioController controller = AudioController.getInstance();
        AudioController.getInstance().stopAll();
        AudioController.getInstance().playRespawn();
        controller.playGameMusic();
    }

    @Override
    public void onExit() {
        AudioController.getInstance().stopAll();
    }
}
