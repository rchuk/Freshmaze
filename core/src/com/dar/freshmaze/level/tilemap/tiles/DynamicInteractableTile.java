package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public abstract class DynamicInteractableTile extends DynamicTile {
    public DynamicInteractableTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile defaultTile, LevelTilemap.Layer defaultLayer) {
        super(tilemap, pos, defaultTile, defaultLayer);

        setPhysBody(tilemap.createTilePhysBody(getCellPos(), defaultTile));
    }

    public abstract void interact(Bob player);
}
