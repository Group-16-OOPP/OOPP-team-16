package main.view.interfaces;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.view.states.MainMenu;

// rendering and updating the main menu
public class MenuState extends GameBaseState {

    private final MainMenu mainMenu;

    public MenuState(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    public void update() {
        mainMenu.update();
    }

    @Override
    public void render(Graphics g) {
        mainMenu.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController controller = AudioController.getInstance();
        controller.playMenuMusic();
    }

    @Override
    public void onExit() {
    }
}
