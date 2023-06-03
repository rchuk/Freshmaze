package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public abstract class DynamicInteractableTile extends DynamicTile {
    public DynamicInteractableTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile tile) {
        super(tilemap, pos, tile);

        setPhysBody(tilemap.createTilePhysBody(getCellPos(), tile));
    }

    public abstract void interact(Bob player);
}
