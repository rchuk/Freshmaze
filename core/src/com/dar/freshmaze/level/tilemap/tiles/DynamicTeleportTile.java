package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.Dungeon;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class DynamicTeleportTile extends DynamicInteractableTile {
    private final Dungeon dungeon;

    public DynamicTeleportTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile tile, Dungeon dungeon) {
        super(tilemap, pos, tile);

        this.dungeon = dungeon;
    }

    @Override
    public void interact(Bob player) {
        dungeon.moveToNextLevel();
    }
}
