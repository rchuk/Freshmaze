package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.Dungeon;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class DynamicChestTile extends DynamicInteractableTile {
    private final TiledMapTile closedTile;
    private final TiledMapTile openTile;

    private boolean isOpen = false;

    public DynamicChestTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile closedTile, TiledMapTile openTile) {
        super(tilemap, pos, closedTile);

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
    }
}
