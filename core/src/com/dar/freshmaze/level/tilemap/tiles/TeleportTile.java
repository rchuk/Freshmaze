package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.Dungeon;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class TeleportTile extends DynamicInteractableTile {
    private final Dungeon dungeon;

    private boolean wasActived = false;

    public TeleportTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile tile, Dungeon dungeon) {
        super(tilemap, pos, tile, LevelTilemap.Layer.Wall);

        this.dungeon = dungeon;
    }

    @Override
    public void interact(Bob player) {
        if (wasActived)
            return;

        wasActived = true;

        dungeon.moveToNextLevel();
    }
}
