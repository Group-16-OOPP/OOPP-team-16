package main.controller.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import audio.controller.AudioController;
import main.controller.Game;
import main.controller.GameStateManager;
import main.controller.inputs.commands.Command;
import main.controller.inputs.commands.GoToMenuCommand;
import main.controller.inputs.commands.JumpPressCommand;
import main.controller.inputs.commands.JumpReleaseCommand;
import main.controller.inputs.commands.MoveLeftPressCommand;
import main.controller.inputs.commands.MoveLeftReleaseCommand;
import main.controller.inputs.commands.MoveRightPressCommand;
import main.controller.inputs.commands.MoveRightReleaseCommand;
import main.controller.inputs.commands.TogglePauseCommand;
import main.model.GameModel;
import main.model.entities.Player;

public class KeyboardInputs implements KeyListener {
    private final Player player;
    private final AudioController audioController;
    private final GameStateManager gameStateManager;
    private final GameModel model;
    private final Game game; /// Game refrence only kept for menu refactor,,,,
    private boolean keyDown = false;

    private final Map<Integer, Command> pressedCommands = new HashMap<>();
    private final Map<Integer, Command> releasedCommands = new HashMap<>();

    public KeyboardInputs(Player player, AudioController audioController, 
        GameModel model, GameStateManager gameStateManager,Game game) {
        this.audioController = audioController;
        this.gameStateManager = gameStateManager;
        this.player = player;
        this.model = model;
        this.game = game;
        initCommands();
    }

    private void initCommands() {
        // movement
        Command moveLeftPress = new MoveLeftPressCommand(player);
        Command moveLeftRelease = new MoveLeftReleaseCommand(player);
        Command moveRightPress = new MoveRightPressCommand(player);
        Command moveRightRelease = new MoveRightReleaseCommand(player);

        // jump
        Command jumpPress = new JumpPressCommand(player);
        Command jumpRelease = new JumpReleaseCommand(player);

        // control
        Command togglePause = new TogglePauseCommand(model);
        Command goToMenu = new GoToMenuCommand(game); // game stays for menu interactions ONLY(for now)

        // map keys to commands (press)
        pressedCommands.put(KeyEvent.VK_A, moveLeftPress);
        pressedCommands.put(KeyEvent.VK_LEFT, moveLeftPress);
        pressedCommands.put(KeyEvent.VK_D, moveRightPress);
        pressedCommands.put(KeyEvent.VK_RIGHT, moveRightPress);

        pressedCommands.put(KeyEvent.VK_SPACE, jumpPress);
        pressedCommands.put(KeyEvent.VK_W, jumpPress);
        pressedCommands.put(KeyEvent.VK_UP, jumpPress);

        pressedCommands.put(KeyEvent.VK_P, togglePause);
        pressedCommands.put(KeyEvent.VK_ESCAPE, goToMenu);

        // map keys to commands (release)
        releasedCommands.put(KeyEvent.VK_A, moveLeftRelease);
        releasedCommands.put(KeyEvent.VK_LEFT, moveLeftRelease);
        releasedCommands.put(KeyEvent.VK_D, moveRightRelease);
        releasedCommands.put(KeyEvent.VK_RIGHT, moveRightRelease);

        releasedCommands.put(KeyEvent.VK_SPACE, jumpRelease);
        releasedCommands.put(KeyEvent.VK_W, jumpRelease);
        releasedCommands.put(KeyEvent.VK_UP, jumpRelease);
    }

    private boolean isEditingName() {
        return gameStateManager.getGameState() == 
               GameStateManager.GameState.MENU && // Game stays for Menu interactions ONLY(for now)
               game.mainMenu != null &&
               game.mainMenu.isEditingName();
    }

    private boolean isJumpKey(int keyCode) {
        return keyCode == KeyEvent.VK_SPACE ||
               keyCode == KeyEvent.VK_W ||
               keyCode == KeyEvent.VK_UP;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // when editing name in main menu, collect characters here
        if (isEditingName()) {
            game.mainMenu.handleNameKeyPressed(0, e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isEditingName()) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                game.mainMenu.handleNameKeyPressed(code, '\0');
            }
            return;
        }

        int keyCode = e.getKeyCode();

        // LEFT/RIGHT navigation for leaderboarding...
        if (game.getGameState() == GameStateManager.GameState.LEADERBOARD) {
            if (keyCode == KeyEvent.VK_LEFT) {
                game.leaderboard.previousLevel();
                return;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                game.leaderboard.nextLevel();
                return;
            }
        }

        //TODO, abstract this to the key in the command, or put it into a listener. choices..
        // Play jump sound once per press
        if (isJumpKey(keyCode) && !keyDown) {
            audioController.playJump();
            keyDown = true;
        }

        Command cmd = pressedCommands.get(keyCode);
        if (cmd != null) {
            cmd.execute();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isEditingName()) {
            return;
        }

        int keyCode = e.getKeyCode();
        if (isJumpKey(keyCode)) {
            keyDown = false;
        }

        Command cmd = releasedCommands.get(keyCode);
        if (cmd != null) {
            cmd.execute();
        }
    }
}
