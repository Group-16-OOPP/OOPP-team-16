package main.states;

import java.awt.Graphics;

import main.Game;

//State responsible for gameplay (levels, player, HUD, pause, transitions)
public class PlayingState extends GameBaseState {

    public PlayingState(Game game) {
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
}

