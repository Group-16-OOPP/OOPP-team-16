package main.states;

import java.awt.Graphics;
import main.Game;

///rendering and updating the leaderboard screen
public class LeaderboardState extends GameBaseState {

    public LeaderboardState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.leaderboard.update();
    }

    @Override
    public void render(Graphics g) {
        game.leaderboard.draw(g);
    }
}

