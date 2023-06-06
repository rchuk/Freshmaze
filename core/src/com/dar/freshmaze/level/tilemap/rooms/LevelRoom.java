package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.Level;

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


    public void act(float dt) {}
    public void onDestroy() {}

    public void onPlayerEnter(Bob bob) {}
    public void onPlayerExit(Bob bob) {}
}
