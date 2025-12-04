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

    //public static AudioController getInstance() {
    //    return this.AudioController;
    //}

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

    public void playMenuMusic() {
        //Platform.runLater(() -> changeState(menuPlayer));
    }

    public void playGameMusic() {
        //Platform.runLater(() -> changeState(gamePlayer));
    }

    public void playJump() {
        jumpSound.play();
    }

    public void playDead() {
        deadSound.play();
    }

    public void playNextLevel() {
        nextLevelSound.play();
    }

    public void playRespawn() {
        spawnSound.play();
    }

    public void playPlatformSound() {
        platformSound.play();
    }

    public void stopAll() {
        Platform.runLater(() -> {
            if (activePlayer != null){
                activePlayer.stop();
            }
            if (menuPlayer != null) {
                menuPlayer.stop();
            }
            if (gamePlayer != null) {
                gamePlayer.stop();
            }
            activePlayer = null;
        });
    }

    private void changeState(MediaPlayer next) {
        if (activePlayer == next) {
            return;
        }
        if (activePlayer != null) {
            activePlayer.stop();
        }
        activePlayer = next;
        activePlayer.play();
    }
}
