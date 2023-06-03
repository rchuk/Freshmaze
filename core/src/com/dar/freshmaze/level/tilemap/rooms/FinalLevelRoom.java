package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FinalLevelRoom extends LevelRoom {
    private final Vector2 teleportPos;

    public FinalLevelRoom(Rectangle bounds, Vector2 teleportPos) {
        super(bounds);

        this.teleportPos = teleportPos;
    }

    public final Vector2 getTeleportPos() {
        return teleportPos;
    }
}
