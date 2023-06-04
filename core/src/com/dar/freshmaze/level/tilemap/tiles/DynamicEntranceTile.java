package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public class DynamicEntranceTile extends DynamicTile {
    private final TiledMapTile openTile;
    private final TiledMapTile closedTile;
    private final TiledMapTile clearedTile;

    private State state = State.Closed;

    public DynamicEntranceTile(LevelTilemap tilemap, LevelTilemap.CellPos pos, TiledMapTile openTile, TiledMapTile closedTile, TiledMapTile clearedTile) {
        super(tilemap, pos, openTile);

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

        final TiledMapTile tile = getTile();

        // TODO: Implement something more complex, with more layers for correct depth sorting
        tilemap.placeTile(getCellPos(), tile, LevelTilemap.Layer.Wall);
        setPhysBody(tilemap.createTilePhysBody(getCellPos(), tile));
    }

    private TiledMapTile getTile() {
        switch (state) {
            case Open:
                return openTile;
            case Closed:
                return closedTile;
            case Cleared:
                return clearedTile;
        }

        return null;
    }

    public enum State {
        Open,
        Closed,
        Cleared
    }
}