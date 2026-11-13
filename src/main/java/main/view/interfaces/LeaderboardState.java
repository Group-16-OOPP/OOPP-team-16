package main.view.interfaces;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.view.states.Leaderboard;

///rendering and updating the leaderboard screen
public class LeaderboardState extends GameBaseState {

    private final Leaderboard leaderboard;

    public LeaderboardState(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public void update() {
        leaderboard.update();
    }

    @Override
    public void render(Graphics g) {
        leaderboard.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController.getInstance().playRespawn();
    }
}

