package com.dar.freshmaze.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.level.tilemap.rooms.LevelRoom;

public class Dungeon implements Disposable {
    private final Level level;
    private final Bob bob;
    private final EnemyOld enemy;
    private LevelRoom currentRoom;

    private int levelIndex = 0;

    public Dungeon(Level level, Bob bob, EnemyOld enemy) {
        this.level = level;
        this.bob = bob;
        this.enemy = enemy;
        generateLevel();
    }

    public Level getLevel() {
        return level;
    }

    public Bob getBob() {
        return bob;
    }

    public void update(float dt) {
        updateRoom();
    }

    // TODO: Room should open when all enemies are killed
    private void updateRoom() {
        final LevelRoom newRoom = findContainingRoom();
        if (currentRoom == newRoom)
            return;

        if (newRoom != null) {
            if (newRoom instanceof BattleLevelRoom)
                newRoom.setIsOpen(false);
        }
    }

    private LevelRoom findContainingRoom() {
        // TODO: Fix so the player doesn't get stuck outside of the room.
        //  Might have something to do with the coordinates conversion.
        final Vector2 bobWorldPos = new Vector2(bob.getX(), bob.getY());
        final Vector2 bobCellPos = level.getTilemap().vecToCellPosVec(bobWorldPos);

        for (LevelRoom room : level.getRooms()) {
            if (room.getBounds().contains(bobCellPos))
                return room;
        }

        return null;
    }

    private void generateLevel() {
        final LevelRoom startRoom = level.generate(levelIndex);
        final Vector2 centerCell = startRoom.getBounds().getCenter(new Vector2());
        final Vector2 center = level.getTilemap().cellPosToVec(centerCell);

        bob.teleport(center);
        enemy.teleport(center);
    }

    @Override
    public void dispose() {
        level.dispose();
    }
}
