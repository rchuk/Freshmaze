package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class SpikesTile extends DynamicTile {
    private final TiledMapTile openTile;

    private boolean isOpen = false;

    public SpikesTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile openTile) {
        super(tilemap, pos, null, LevelTilemap.Layer.FloorOverlay);

        this.openTile = openTile;
    }

    public void onTouch(Bob bob) {
        bob.damage(3);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean newIsOpen) {
        if (isOpen == newIsOpen)
            return;

        isOpen = newIsOpen;

        final TiledMapTile tile = isOpen ? openTile : null;
        final Body body = getTilemap().createTilePhysBodySensor(getCellPos(), tile);

        getTilemap().placeTile(getCellPos(), tile, LevelTilemap.Layer.FloorOverlay);
        setPhysBody(body);
    }
}
