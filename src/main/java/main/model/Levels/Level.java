package main.model.Levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import main.model.entities.DeathSprite;
import main.model.entities.Entity;
import main.model.entities.MovingPlatform;
import main.model.entities.SpawnPlatform;
import main.model.entities.Spike;
import main.model.entities.TriggerPlatform;
import main.model.entities.TriggerSpike;
import main.controller.Game;

public class Level {
    private int[][] lvlData;
    private int[][] lvlObstacleData;
    private int[][] lvlObjData;
    private float spawnX;
    private float spawnY;
    private int deathScore;
    private List<MovingPlatform> movingPlatforms;
    private List<TriggerPlatform> triggerPlatforms;
    private List<Spike> spikes;
    private List<TriggerSpike> triggerSpikes;
    private SpawnPlatform spawnPlatform;

    // Store which tile positions are trigger platforms (so we don't draw them as tiles)
    private List<int[]> triggerPlatformPositions;

    // Store death sprites that fall to the ground
    private List<DeathSprite> deathSprites;

    public Level(int[][] lvlData, int[][] lvlObstacleData, int[][] lvlObjData, float spawnX, float spawnY) {
        this.lvlData = lvlData;
        this.lvlObstacleData = lvlObstacleData;
        this.lvlObjData = lvlObjData;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.movingPlatforms = new ArrayList<>();
        this.triggerPlatforms = new ArrayList<>();
        this.spikes = new ArrayList<>();
        this.triggerSpikes = new ArrayList<>();
        this.triggerPlatformPositions = new ArrayList<>();
        this.deathSprites = new ArrayList<>();
    }

    /**
     * Scans level data for specific tile IDs and creates TriggerPlatforms from them.
     *
     * @param tileId        The tile ID to look for in the level data
     * @param targetOffsetX How far the platform moves in X when triggered
     * @param targetOffsetY How far the platform moves in Y when triggered
     * @param speed         Movement speed
     * @param sprites       Array of sprites indexed by tile ID
     * @param shouldReturn  Whether platform returns to start after reaching target
     * @param solid         Whether player can stand on this platform
     */
    public void createTriggerPlatformsFromTile(int tileId, int spriteId, float targetOffsetX,
                                               float targetOffsetY, float speed, BufferedImage[] sprites,
                                               boolean shouldReturn, boolean solid) {
        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    float posX = x * Game.TILES_SIZE;
                    float posY = y * Game.TILES_SIZE;

                    TriggerPlatform platform = new TriggerPlatform(
                            posX, posY,
                            posX + targetOffsetX, posY + targetOffsetY,
                            Game.TILES_SIZE, Game.TILES_SIZE,
                            speed,
                            sprites[spriteId]
                            , shouldReturn);
                    platform.setSolid(solid);
                    triggerPlatforms.add(platform);

                    // Mark this position so we don't draw the tile
                    triggerPlatformPositions.add(new int[]{x, y});

                    // Replace tile with transparent/empty tile in level data
                    lvlData[y][x] = 80; // 80 = transparent tile
                }
            }
        }
    }

    public boolean isTriggerPlatformPosition(int x, int y) {
        for (int[] pos : triggerPlatformPositions) {
            if (pos[0] == x && pos[1] == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a single TriggerPlatform from all tiles with the given ID - they all move together.
     */
    public void createGroupedTriggerPlatformFromTile(
            int tileId, float targetOffsetX, float targetOffsetY, float speed,
            BufferedImage[] sprites, boolean shouldReturn, boolean solid, boolean shouldLoop) {

        // Find all tiles with this ID
        List<int[]> positions = new ArrayList<>();
        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    positions.add(new int[]{x, y});
                }
            }
        }

        if (positions.isEmpty()) {
            return;
        }

        // Find bounding box of all tiles
        int minX = positions.get(0)[0];
        int maxX = positions.get(0)[0];
        int minY = positions.get(0)[1];
        int maxY = positions.get(0)[1];

        for (int[] pos : positions) {
            minX = Math.min(minX, pos[0]);
            maxX = Math.max(maxX, pos[0]);
            minY = Math.min(minY, pos[1]);
            maxY = Math.max(maxY, pos[1]);
        }

        // Use top-left of bounding box as main position
        float posX = minX * Game.TILES_SIZE;
        float posY = minY * Game.TILES_SIZE;
        int width = (maxX - minX + 1) * Game.TILES_SIZE;
        int height = (maxY - minY + 1) * Game.TILES_SIZE;

        // Get sprite from lvlData at first tile position
        int[] first = positions.get(0);
        int firstSpriteId = lvlData[first[1]][first[0]];

        TriggerPlatform platform = new TriggerPlatform(
                posX, posY,
                posX + targetOffsetX, posY + targetOffsetY,
                width, height,
                speed,
                sprites[firstSpriteId],
                shouldReturn
        );

        platform.setSolid(solid);
        platform.setLoop(shouldLoop);

        // Set first tile position relative to bounding box
        platform.setFirstTileOffset((
                first[0] - minX) * Game.TILES_SIZE, (first[1] - minY)
                * Game.TILES_SIZE);

        // Enlarge hitbox with offset (1.5x for smaller trigger area)
        platform.setHitboxSize(
                (int) (width * 1.5), (int) (height * 1.5),
                (int) (posX - width * 0.25), (int) (posY - height * 0.25)
        );

        // Add remaining tiles as additional tiles (relative to bounding box top-left), using lvlData for sprite
        for (int i = 1; i < positions.size(); i++) {
            int[] pos = positions.get(i);
            float relX = (pos[0] - minX) * Game.TILES_SIZE;
            float relY = (pos[1] - minY) * Game.TILES_SIZE;
            int tileSpriteId = lvlData[pos[1]][pos[0]];
            platform.addTile(relX, relY, sprites[tileSpriteId]);
        }

        triggerPlatforms.add(platform);

        // Mark all positions and replace tiles
        for (int[] pos : positions) {
            triggerPlatformPositions.add(pos);
            lvlData[pos[1]][pos[0]] = 80;
        }
    }

    /**
     * Check if player is standing on any solid platform
     */
    public boolean isOnSolidPlatform(java.awt.geom.Rectangle2D.Float playerHitbox) {
        for (TriggerPlatform platform : triggerPlatforms) {
            if (platform.isSolid()) {
                java.awt.geom.Rectangle2D.Float platHitbox = platform.getSpriteHitbox();
                // Check if player's bottom is on platform's top
                float playerBottom = playerHitbox.y + playerHitbox.height;
                float platformTop = platHitbox.y;

                boolean verticallyAligned = playerBottom >= platformTop && playerBottom <= platformTop + 5;
                boolean horizontallyOverlapping =
                        playerHitbox.x + playerHitbox.width > platHitbox.x &&
                                playerHitbox.x < platHitbox.x + platHitbox.width;

                if (verticallyAligned && horizontallyOverlapping) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the Y position of the platform the player is landing on (only when falling through it)
     */
    public float getSolidPlatformY(java.awt.geom.Rectangle2D.Float playerHitbox, float airSpeed) {
        for (TriggerPlatform platform : triggerPlatforms) {
            if (platform.isSolid()) {
                java.awt.geom.Rectangle2D.Float platHitbox = platform.getSpriteHitbox();
                float playerBottom = playerHitbox.y + playerHitbox.height;
                float platformTop = platHitbox.y;

                // Only land if player bottom crossed platform top this frame
                boolean crossedPlatform = playerBottom >= platformTop && playerBottom <= platformTop + airSpeed + 5;
                boolean horizontallyOverlapping =
                        playerHitbox.x + playerHitbox.width > platHitbox.x &&
                                playerHitbox.x < platHitbox.x + platHitbox.width;

                if (crossedPlatform && horizontallyOverlapping) {
                    return platformTop - playerHitbox.height;
                }
            }
        }
        return -1;
    }

    public void addMovingPlatform(MovingPlatform platform) {
        movingPlatforms.add(platform);
    }

    public void addTriggerPlatform(TriggerPlatform platform) {
        triggerPlatforms.add(platform);
    }

    public void setAudioControllerForPlatforms(audio.controller.AudioController audioController) {
        for (TriggerPlatform platform : triggerPlatforms) {
            platform.setAudioController(audioController);
        }
    }

    public void updatePlatforms(Entity player) {
        for (MovingPlatform platform : movingPlatforms) {
            platform.update();
        }
        for (TriggerPlatform platform : triggerPlatforms) {
            // Check if player touches the platform
            if (!platform.isTriggered() && platform.checkPlayerCollision(player)) {
                platform.trigger();
            }

            float oldX = platform.getHitbox().x;
            float oldY = platform.getHitbox().y;

            platform.update();

            float dx = platform.getHitbox().x - oldX;
            float dy = platform.getHitbox().y - oldY;

            if ((dx != 0 || dy != 0) && platform.isSolid()) {
                java.awt.geom.Rectangle2D.Float platHitbox = platform.getSpriteHitbox();
                // Reconstruct previous position
                float prevPlatX = platHitbox.x - dx;
                float prevPlatY = platHitbox.y - dy;

                java.awt.geom.Rectangle2D.Float playerHitbox = player.getHitbox();
                float playerBottom = playerHitbox.y + playerHitbox.height;

                // Check if player was standing on the platform before it moved
                boolean verticallyAligned = playerBottom >= prevPlatY && playerBottom <= prevPlatY + 5;
                boolean horizontallyOverlapping =
                        playerHitbox.x + playerHitbox.width > prevPlatX &&
                                playerHitbox.x < prevPlatX + platHitbox.width;

                if (verticallyAligned && horizontallyOverlapping) {
                    // Check if player can move here before applying the movement
                    if (utilities.HelpMethods.canMoveHere(
                            player.getHitbox().x + dx,
                            player.getHitbox().y + dy,
                            player.getHitbox().width,
                            player.getHitbox().height,
                            lvlData)) {

                        player.getHitbox().x += dx;
                        player.getHitbox().y += dy;
                    }
                }
            }
        }
    }

    public void drawPlatforms(Graphics g) {
        for (MovingPlatform platform : movingPlatforms) {
            platform.render(g);
        }
        for (TriggerPlatform platform : triggerPlatforms) {
            platform.render(g);
        }
    }

    public List<MovingPlatform> getMovingPlatforms() {
        return movingPlatforms;
    }

    public List<TriggerPlatform> getTriggerPlatforms() {
        return triggerPlatforms;
    }

    public void resetPlatforms() {
        for (TriggerPlatform platform : triggerPlatforms) {
            platform.reset();
        }
        resetTriggerSpikes();
    }

    public void recordDeathPosition(float x, float y, BufferedImage deathSprite) {
        if (deathSprite == null) {
            return;
        }

        // Find the ground Y position - use player's Y if on ground, otherwise find ground below
        float groundY = utilities.HelpMethods.findGroundY(x, y, Game.TILES_SIZE, lvlData);

        // Only place death sprite if there's valid ground (not -1)
        if (groundY >= 0) {
            DeathSprite sprite = new DeathSprite(x, groundY, deathSprite);
            deathSprites.add(sprite);
        }
    }

    public void clearDeathPositions() {
        deathSprites.clear();
    }

    public void drawDeathSprites(Graphics g) {
        for (DeathSprite sprite : deathSprites) {
            sprite.render(g);
        }
    }

    public void createSpikesFromTile(int tileId, int spriteId, BufferedImage[] sprites) {
        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    float posX = x * Game.TILES_SIZE;
                    float posY = y * Game.TILES_SIZE;

                    Spike spike = new Spike(posX, posY, Game.TILES_SIZE, Game.TILES_SIZE, sprites[spriteId]);
                    spikes.add(spike);
                }
            }
        }
    }

    public boolean checkSpikeCollision(Entity player) {
        for (Spike spike : spikes) {
            if (spike.checkPlayerCollision(player)) {
                return true;
            }
        }
        return false;
    }

    public void drawSpikes(Graphics g) {
        for (Spike spike : spikes) {
            spike.render(g);
        }
    }

    public void createTriggerSpikesFromTile(int tileId, int spriteId, float targetOffsetX,
                                            float targetOffsetY, float speed,
                                            float triggerDistance, BufferedImage[] sprites,
                                            boolean shouldReturn, int id,
                                            int collisionWidth, int collisionHeight) {

        for (int y = 0; y < lvlObstacleData.length; y++) {
            for (int x = 0; x < lvlObstacleData[y].length; x++) {
                if (lvlObstacleData[y][x] == tileId) {
                    float posX = x * Game.TILES_SIZE;
                    float posY = y * Game.TILES_SIZE;

                    // Use provided collision size or default to full width, half height
                    int cWidth = (collisionWidth > 0) ? collisionWidth : Game.TILES_SIZE;
                    int cHeight = (collisionHeight > 0) ? collisionHeight : Game.TILES_SIZE / 2;

                    TriggerSpike spike = new TriggerSpike(
                            posX, posY,
                            posX + targetOffsetX, posY + targetOffsetY,
                            Game.TILES_SIZE, Game.TILES_SIZE,
                            speed, triggerDistance,
                            sprites[spriteId],
                            shouldReturn,
                            id,
                            cWidth, cHeight
                    );
                    triggerSpikes.add(spike);
                }
            }
        }
    }

    public void updateTriggerSpikes(Entity player) {
        for (TriggerSpike spike : triggerSpikes) {
            if (!spike.isTriggered() && spike.checkTriggerDistance(player)) {
                spike.trigger();
                // If this spike has a group ID, trigger all others with the same ID
                if (spike.getId() != -1) {
                    for (TriggerSpike otherSpike : triggerSpikes) {
                        if (otherSpike.getId() == spike.getId()) {
                            otherSpike.trigger();
                        }
                    }
                }
            }
            spike.update();
        }
    }

    public void drawTriggerSpikes(Graphics g) {
        for (TriggerSpike spike : triggerSpikes) {
            spike.render(g);
        }
    }

    public boolean checkTriggerSpikeCollision(Entity player) {
        for (TriggerSpike spike : triggerSpikes) {
            if (spike.checkPlayerCollision(player)) {
                return true;
            }
        }
        return false;
    }

    public void resetTriggerSpikes() {
        for (TriggerSpike spike : triggerSpikes) {
            spike.reset();
        }
    }

    public void setSpawnPlatform(SpawnPlatform platform) {
        this.spawnPlatform = platform;
    }

    public void updateSpawnPlatform() {
        if (spawnPlatform != null) {
            spawnPlatform.update();
        }
    }

    public void drawSpawnPlatform(Graphics g) {
        if (spawnPlatform != null) {
            spawnPlatform.render(g);
        }
    }

    public void triggerSpawnPlatform() {
        if (spawnPlatform != null) {
            spawnPlatform.triggerSpawn();
        }
    }

    public boolean isSpawnPlatformAnimating() {
        return spawnPlatform != null && spawnPlatform.isAnimating();
    }

    public boolean hasSpawnPlatformReachedBottom() {
        return spawnPlatform == null || spawnPlatform.hasReachedBottom();
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    public int getObjectSpriteIndex(int x, int y) {
        return lvlObjData[y][x];
    }

    public int[][] getLevelData() {
        return lvlData;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public void updateDeathScore(int death) {
        if (death == 0) {
            this.deathScore = death;
        } else if (death < getDeathCount()) {
            this.deathScore = death;
        } else {

        }
        this.deathScore += 1;
    }

    public int getDeathCount() {
        return this.deathScore;
    }
}
