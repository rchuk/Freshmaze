package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.Level;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.level.tilemap.tiles.DynamicEntranceTile;
import com.dar.freshmaze.level.tilemap.tiles.DynamicTile;

public class LevelRoom {
    private Level level;
    private final Rectangle bounds;


    public LevelRoom(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level newLevel) {
        level = newLevel;
    }

    public Rectangle getBounds() {
        return bounds;
    }


    public void onDestroy() {}

    public void onPlayerEnter(Bob bob) {}
    public void onPlayerExit(Bob bob) {}
}
