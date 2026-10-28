package main.view.interfaces;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.controller.Game;
import main.controller.GameController;
import main.view.GameView;

//State responsible for gameplay (levels, player, HUD, pause, transitions)
public class GamingState extends GameBaseState {

    private final GameController controller;
    private final GameView view;

    public GamingState(Game game, GameController controller, GameView view) {
        super(game);
        this.controller = controller;
        this.view = view;
    }

    @Override
    public void update() {
        controller.updateGameplay();
    }

    @Override
    public void render(Graphics g) {
        view.renderGame(g);
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
