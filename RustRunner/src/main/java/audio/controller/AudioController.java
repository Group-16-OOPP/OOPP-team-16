package audio.controller;

import java.net.URL;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioController {
    //has to be static to be called on by keyboardinputs later.
    //add more down here if needed (sound effects).

    private static AudioClip deadSound;
    private static AudioClip jumpSound;
    private static AudioClip nextLevelSound;
    private static AudioClip platformSound;
    private static AudioClip spawnSound;
    private static AudioController audioController = null;

    private static boolean javaFXStarted = false;

    private MediaPlayer menuPlayer;
    private MediaPlayer gamePlayer;
    private MediaPlayer LeaderboardPlayer;
    private MediaPlayer activePlayer;


    //has to be static to be called on by keyboardinputs later.
    //add more down here if needed (sound effects).
    //To use this and add sounds, load the resources via the loadResouces method,
    //if it's a music state use use loadMedia() method, if it's a sound effect,
    //use the loadSoundClip() method.

    public AudioController() {
        startJavaFX();
        loadResources();
    }

    public static synchronized AudioController getInstance() {
        if (audioController == null) {
            audioController = new AudioController();
        }
        return audioController;
    }

    private void startJavaFX() {
        if (javaFXStarted) {
            return;
        }

        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // Already initialized
        }
        javaFXStarted = true;
    }

    private void loadResources() {
        menuPlayer = loadMedia("/audio/resources/John Bartmann - Hardcore Orchestral Dubstep.mp3");
        menuPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        gamePlayer = loadMedia("/audio/resources/BlackTrendMusic - The Dubstep.mp3");
        gamePlayer.setCycleCount(MediaPlayer.INDEFINITE);

        jumpSound = loadSoundClip("/audio/resources/jump.mp3");
        deadSound = loadSoundClip("/audio/resources/Dead.mp3");
        spawnSound = loadSoundClip("/audio/resources/Respawn.mp3");
        nextLevelSound = loadSoundClip("/audio/resources/NextLevel.mp3");
        platformSound = loadSoundClip("/audio/resources/Platform.mp3");
    }

    private MediaPlayer loadMedia(String name) {
        URL menu = AudioController.class.getResource(name);
        return new MediaPlayer(new Media(menu.toString()));
    }

    private AudioClip loadSoundClip(String path) {
        URL jumpPath = AudioController.class.getResource(path);
        return new AudioClip(jumpPath.toString());
    }

    /**
     * Starts playing menu music, stopping any other active background track first.
     */
    public void playMenuMusic() {
        changeState(menuPlayer);
    }

    /**
     * Starts playing game music, stopping any other active background track first.
     */
    public void playGameMusic() {
        changeState(gamePlayer);
    }

    public void playJump() {
        if (jumpSound != null) {
            jumpSound.play();
        }
    }

    public void playDead() {
        if (deadSound != null) {
            deadSound.play();
        }
    }

    public void playNextLevel() {
        if (nextLevelSound != null) {
            nextLevelSound.play();
        }
    }

    public void playRespawn() {
        if (spawnSound != null) {
            spawnSound.play();
        }
    }

    public void playPlatformSound() {
        if (platformSound != null) {
            platformSound.play();
        }
    }

    /**
     * Stops all background music players, if they are currently playing,
     * and clears the activePlayer reference.
     */
    public void stopAll() {
        if (activePlayer != null) {
            activePlayer.stop();
            activePlayer = null;
        }
        if (menuPlayer != null) {
            menuPlayer.stop();
        }
        if (gamePlayer != null) {
            gamePlayer.stop();
        }
    }

    /**
     * Internal helper to switch the active background track.
     * Stops the current active player (if any) and starts the next one in loop mode.
     */
    private void changeState(MediaPlayer next) {
        if (next == null) {
            return;
        }
        if (activePlayer == next) {
            // Already playing this track; avoid restarting/overlapping
            return;
        }
        if (activePlayer != null) {
            activePlayer.stop();
        }
        activePlayer = next;
        activePlayer.setCycleCount(MediaPlayer.INDEFINITE);
        activePlayer.play();
    }
}
