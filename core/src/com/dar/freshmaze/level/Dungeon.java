package com.dar.freshmaze.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.tilemap.rooms.LevelRoom;
import com.dar.freshmaze.util.RectangleUtil;

public class Dungeon implements Disposable {
    private final Level level;
    private final Bob bob;
    private LevelRoom currentRoom;

    private int levelIndex = 0;
    private boolean pendingTransition = false;

    public Dungeon(Level level, Bob bob) {
        this.level = level;
        this.bob = bob;

        level.getTilemap().setDungeon(this);

        generateLevel();
    }

    public Level getLevel() {
        return level;
    }

    public Bob getBob() {
        return bob;
    }

    public boolean isPendingTransition() {
        return pendingTransition;
    }

    public void moveToNextLevel() {
        levelIndex++;

        if (!isMaxLevel())
            pendingTransition = true;
    }

    public void update(float dt) {
        if (pendingTransition) {
            generateLevel();

            pendingTransition = false;
        }

        updateRoom();
        level.update(dt);
    }
    
    private void updateRoom() {
        final LevelRoom newRoom = findContainingRoom();
        if (currentRoom == newRoom)
            return;

        if (currentRoom != null)
            currentRoom.onPlayerExit(bob);

        currentRoom = newRoom;

        if (currentRoom != null)
            currentRoom.onPlayerEnter(bob);
    }

    private LevelRoom findContainingRoom() {
        final Vector2 bobWorldPos = new Vector2(bob.getX() + 0.5f * bob.getWidth(), bob.getY() + 0.5f * bob.getHeight());
        final Vector2 bobCellPos = level.getTilemap().vecToCellPosVec(bobWorldPos);

        for (LevelRoom room : level.getRooms()) {
            if (RectangleUtil.containsExclusive(room.getBounds(), bobCellPos))
                return room;
        }

        return null;
    }

    private void generateLevel() {
        final LevelRoom startRoom = level.generate(levelIndex);
        final Vector2 centerCell = startRoom.getBounds().getCenter(new Vector2());
        final Vector2 center = level.getTilemap().cellPosToVec(centerCell);

        bob.teleport(center);
    }

    public boolean isMaxLevel() {
        return getLevelIndex() == level.getMaxLevel();
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    @Override
    public void dispose() {
        level.dispose();
    }
}
