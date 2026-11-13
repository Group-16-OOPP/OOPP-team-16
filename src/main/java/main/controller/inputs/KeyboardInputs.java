package main.controller.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import audio.controller.AudioController;
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
import main.view.states.Leaderboard;
import main.view.states.MainMenu;

public class KeyboardInputs implements KeyListener {
    private final Player player;
    private final AudioController audioController;
    private final GameStateManager gameStateManager;
    private final GameModel model;
    private boolean keyDown = false;

    private final MainMenu mainMenu;
    private final Leaderboard leaderboard;
    private final Consumer<GameStateManager.GameState> stateChangeAction;

    private final Map<Integer, Command> pressedCommands = new HashMap<>();
    private final Map<Integer, Command> releasedCommands = new HashMap<>();

    public KeyboardInputs(Player player, AudioController audioController, 
        GameModel model, GameStateManager gameStateManager, MainMenu mainMenu, Leaderboard leaderboard,
              Consumer<GameStateManager.GameState> stateChangeAction) {
        this.player = player;
        this.audioController = audioController;
        this.model = model;
        this.gameStateManager = gameStateManager;
        this.mainMenu = mainMenu;
        this.leaderboard = leaderboard;
        this.stateChangeAction = stateChangeAction;
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
        Command goToMenu = new GoToMenuCommand(stateChangeAction); // game stays for menu interactions ONLY(for now)

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
               mainMenu != null &&
               mainMenu.isEditingName();
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
            mainMenu.handleNameKeyPressed(0, e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isEditingName()) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
                mainMenu.handleNameKeyPressed(code, '\0');
            }
            return;
        }

        int keyCode = e.getKeyCode();

        // LEFT/RIGHT navigation for leaderboarding...
        if (gameStateManager.getGameState() == GameStateManager.GameState.LEADERBOARD) {
            if (keyCode == KeyEvent.VK_LEFT) {
                leaderboard.previousLevel();
                return;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                leaderboard.nextLevel();
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
