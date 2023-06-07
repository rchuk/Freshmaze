package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
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

        final float random = MathUtils.random();
        if (random < 0.2) {
            player.heal(10);
        } else if (random < 0.8) {
            player.increaseAttackSpeed(0.15f);
        }

        isOpen = true;

        getTilemap().placeTile(getCellPos(), openTile, LevelTilemap.Layer.Wall);
        setPhysBody(getTilemap().createTilePhysBody(getCellPos(), openTile));
    }
}
