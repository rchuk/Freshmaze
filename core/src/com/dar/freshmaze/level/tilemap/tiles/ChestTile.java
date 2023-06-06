package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class ChestTile extends DynamicInteractableTile {
    private final TiledMapTile closedTile;
    private final TiledMapTile openTile;

    private boolean isOpen = false;

    public ChestTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile closedTile, TiledMapTile openTile) {
        super(tilemap, pos, closedTile, LevelTilemap.Layer.Wall);

        this.closedTile = closedTile;
        this.openTile = openTile;
    }

    @Override
    public void interact(Bob player) {
        if (isOpen)
            return;

        // TODO: Add some bonuses to the player

        isOpen = true;

        getTilemap().placeTile(getCellPos(), openTile, LevelTilemap.Layer.Wall);
        setPhysBody(getTilemap().createTilePhysBody(getCellPos(), openTile));
    }
}
