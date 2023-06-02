package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.level.Level;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.level.tilemap.tiles.DynamicEntranceTile;
import com.dar.freshmaze.level.tilemap.tiles.DynamicTile;

public class LevelRoom {
    private Level level;
    private final Rectangle bounds;
    private final Array<Vector2> entrances = new Array<>();

    private boolean isOpen = true;

    public LevelRoom(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setLevel(Level newLevel) {
        level = newLevel;
    }

    public void setIsOpen(boolean newIsOpen) {
        isOpen = newIsOpen;

        // TODO: Send some event
        entrances.forEach(entrance -> {
            final DynamicTile dynamicTile = level.getTilemap().getDynamicTile(new LevelTilemap.CellPos((int)entrance.x, (int)entrance.y));
            if (dynamicTile == null)
                return;

            if (dynamicTile instanceof DynamicEntranceTile) {
                final DynamicEntranceTile entranceTile = (DynamicEntranceTile)dynamicTile;

                entranceTile.setIsOpen(isOpen);
            }
        });
    }

    public boolean isOpen() {
        return isOpen;
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public Array<Vector2> getEntrances() {
        return entrances;
    }

    public void addEntrance(Vector2 entrance) {
        entrances.add(entrance);
    }
}
