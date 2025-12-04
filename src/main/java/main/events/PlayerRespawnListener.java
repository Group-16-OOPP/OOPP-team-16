package main.events;

/** Listener for low-level player events coming from the Player model. */
public interface PlayerRespawnListener {

    /** Called when the player has just Respawned. */
    void onPlayerRespawn();
}
