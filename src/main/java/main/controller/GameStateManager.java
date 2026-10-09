package main.controller;

import main.view.interfaces.GameBaseState;
import main.view.interfaces.GamingState;
import main.view.interfaces.LeaderboardState;
import main.view.interfaces.LevelSelectState;
import main.view.interfaces.MenuState;

public class GameStateManager {

    public enum GameState {MENU, PLAYING, LEADERBOARD, LEVEL_SELECT}

    private GameState gameState = GameState.MENU;

    private GameBaseState currentState;
    private GamingState gamingState;
    private MenuState menuState;
    private LeaderboardState leaderboardState;
    private LevelSelectState levelSelectState;

    public GameStateManager(GamingState gamingState, MenuState menuState,
                           LeaderboardState leaderboardState, LevelSelectState levelSelectState) {
        this.gamingState = gamingState;
        this.menuState = menuState;
        this.leaderboardState = leaderboardState;
        this.levelSelectState = levelSelectState;
        this.gameState = GameState.MENU;
        this.currentState = menuState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameBaseState getCurrentState() {
        return currentState;
    }

    public void setGameState(GameState newState) {
        if (gameState == newState) {
            return;
        }

        GameBaseState previousState = currentState;
        gameState = newState;

        switch (newState) {
        case MENU -> currentState = menuState;
        case LEVEL_SELECT -> currentState = levelSelectState;
        case PLAYING -> currentState = gamingState;
        case LEADERBOARD -> currentState = leaderboardState;
        default -> {
            // needed to satisfy checkstyle
        }
        }

        if (previousState != null && previousState != currentState) {
            previousState.onExit();
        }
        if (currentState != null && previousState != currentState) {
            currentState.onEnter();
        }
    }
}
