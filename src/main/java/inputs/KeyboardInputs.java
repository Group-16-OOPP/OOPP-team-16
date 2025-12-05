package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import inputs.commands.Command;
import inputs.commands.GoToMenuCommand;
import inputs.commands.JumpPressCommand;
import inputs.commands.JumpReleaseCommand;
import inputs.commands.MoveLeftPressCommand;
import inputs.commands.MoveLeftReleaseCommand;
import inputs.commands.MoveRightPressCommand;
import inputs.commands.MoveRightReleaseCommand;
import inputs.commands.TogglePauseCommand;
import main.Game;

public class KeyboardInputs implements KeyListener {

    private final Game game;
    private boolean keyDown = false;

    private final Map<Integer, Command> pressedCommands = new HashMap<>();
    private final Map<Integer, Command> releasedCommands = new HashMap<>();

    public KeyboardInputs(Game game) {
        this.game = game;
        initCommands();
    }

    private void initCommands() {
        // movement
        Command moveLeftPress = new MoveLeftPressCommand(game);
        Command moveLeftRelease = new MoveLeftReleaseCommand(game);
        Command moveRightPress = new MoveRightPressCommand(game);
        Command moveRightRelease = new MoveRightReleaseCommand(game);

        // jump
        Command jumpPress = new JumpPressCommand(game);
        Command jumpRelease = new JumpReleaseCommand(game);

        // control
        Command togglePause = new TogglePauseCommand(game);
        Command goToMenu = new GoToMenuCommand(game);

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
        return game.getGameState() == Game.GameState.MENU &&
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
        if (game.getGameState() == Game.GameState.LEADERBOARD) {
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
            game.getAudioController().playJump();
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
