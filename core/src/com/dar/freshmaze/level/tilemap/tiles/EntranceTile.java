package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class EntranceTile extends DynamicTile {
    private final TiledMapTile openTile;
    private final TiledMapTile closedTile;
    private final TiledMapTile clearedTile;

    private State state = State.Closed;

    public EntranceTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile openTile, TiledMapTile closedTile, TiledMapTile clearedTile) {
        super(tilemap, pos, openTile, LevelTilemap.Layer.FloorOverlay);

        this.openTile = openTile;
        this.closedTile = closedTile;
        this.clearedTile = clearedTile;

        setPhysBody(tilemap.createTilePhysBody(getCellPos(), getDefaultTile()));
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        state = newState;

        final LevelTilemap tilemap = getTilemap();

        switch (state) {
            case Open:
                tilemap.placeTile(getCellPos(), openTile, LevelTilemap.Layer.FloorOverlay);
                tilemap.placeTile(getCellPos(), null, LevelTilemap.Layer.Wall);
                setPhysBody(null);
                break;

            case Cleared:
                tilemap.placeTile(getCellPos(), clearedTile, LevelTilemap.Layer.FloorOverlay);
                tilemap.placeTile(getCellPos(), null, LevelTilemap.Layer.Wall);
                setPhysBody(null);
                break;

            case Closed:
                tilemap.placeTile(getCellPos(), null, LevelTilemap.Layer.FloorOverlay);
                tilemap.placeTile(getCellPos(), closedTile, LevelTilemap.Layer.Wall);
                setPhysBody(tilemap.createTilePhysBody(getCellPos(), closedTile));
                break;
        }
    }

    public enum State {
        Open,
        Closed,
        Cleared
    }
}