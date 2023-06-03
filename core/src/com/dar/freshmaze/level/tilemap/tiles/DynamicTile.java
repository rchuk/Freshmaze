package com.dar.freshmaze.level.tilemap.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;
import com.dar.freshmaze.level.tilemap.LevelTilemap;

public abstract class DynamicTile {
    private final LevelTilemap tilemap;
    private final LevelTilemap.CellPos cellPos;
    private final TiledMapTile defaultTile;

    private Body physBody;

    public DynamicTile(LevelTilemap tilemap, LevelTilemap.CellPos cellPos, TiledMapTile defaultTile) {
        this.tilemap = tilemap;
        this.cellPos = cellPos;
        this.defaultTile = defaultTile;
    }

    public LevelTilemap getTilemap() {
        return tilemap;
    }

    public Body getPhysBody() {
        return physBody;
    }

    public LevelTilemap.CellPos getCellPos() {
         return cellPos;
    }

    public TiledMapTile getDefaultTile() {
        return defaultTile;
    }

    protected void setPhysBody(Body newPhysBody) {
        if (physBody != null)
            tilemap.getPhysWorld().destroyBody(physBody);

        physBody = newPhysBody;
        if (physBody != null)
            physBody.setUserData(this);
    }
}
