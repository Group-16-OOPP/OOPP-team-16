package main.events;

/** Listener for low-level player events coming from the Player model. */
public interface PlayerDeathListener {

    /** Called when the player has just died. */
    void onPlayerDeath();
}
