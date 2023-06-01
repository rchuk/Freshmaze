package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class DynamicEntranceTile extends DynamicTile {
    private final TiledMapTile openTile;
    private final TiledMapTile closedTile;

    private boolean isOpen = true;

    public DynamicEntranceTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile openTile, TiledMapTile closedTile) {
        super(tilemap, pos, openTile);

        this.openTile = openTile;
        this.closedTile = closedTile;

        physBody = tilemap.createTilePhysBody(getCellPos(), getDefaultTile());
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean newIsOpen) {
        isOpen = newIsOpen;

        final LevelTilemap tilemap = getTilemap();

        if (physBody != null)
            tilemap.getPhysWorld().destroyBody(physBody);

        final TiledMapTile tile = isOpen ? openTile : closedTile;

        tilemap.placeTile(getCellPos(), tile, LevelTilemap.Layer.Wall);
        physBody = tilemap.createTilePhysBody(getCellPos(), tile);
    }
}