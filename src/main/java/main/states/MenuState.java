package main.states;

import java.awt.Graphics;

import main.Game;

//rendering and updating the main menu
public class MenuState extends GameBaseState {

    public MenuState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.mainMenu.update();
    }

    @Override
    public void render(Graphics g) {
        game.mainMenu.draw(g);
    }
}

