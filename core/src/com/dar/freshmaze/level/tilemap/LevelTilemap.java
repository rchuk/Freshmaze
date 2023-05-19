package com.dar.freshmaze.level.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.dar.freshmaze.level.bitmap.LevelBitmap;

public class LevelTilemap implements Disposable {
    private Texture tiles;

    private final StaticTiledMapTile wallTile;
    private final StaticTiledMapTile floorTile;

    private final int tileSize;
    private TiledMap tilemap;

    public LevelTilemap(String tilesetPath, int tileSize) {
        this.tileSize = tileSize;
        tiles = new Texture(Gdx.files.internal(tilesetPath));
        final TextureRegion[][] splitTiles = TextureRegion.split(tiles, tileSize, tileSize);

        floorTile = new StaticTiledMapTile(splitTiles[0][0]);
        wallTile = new StaticTiledMapTile(splitTiles[0][1]);
    }

    public TiledMap getTilemap() {
        return tilemap;
    }

    public void generate(LevelBitmap bitmap) {
        tilemap = new TiledMap();

        final MapLayers layers = tilemap.getLayers();
        final TiledMapTileLayer layer = new TiledMapTileLayer(bitmap.getWidth(), bitmap.getHeight(), tileSize, tileSize / 2);

        for (int yi = 0; yi < bitmap.getWidth(); ++yi) {
            for (int xi = 0; xi < bitmap.getHeight(); ++xi) {
                final LevelBitmap.Cell bitmapCell = bitmap.getCell(xi, yi);
                final TiledMapTile tile = mapBitmapCellToTile(bitmapCell);
                if (tile == null)
                    continue;

                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                layer.setCell(xi, yi, cell);
            }
        }

        layers.add(layer);
    }

    // TODO: Create more tiles
    private TiledMapTile mapBitmapCellToTile(LevelBitmap.Cell bitmapCell) {
        switch (bitmapCell.getKind()) {
            case Wall:
                return wallTile;
            case Room:
            case Hall:
            case HallEntrance:
                return floorTile;
            default:
                return null;
        }
    }

    @Override
    public void dispose() {
        tiles.dispose();
    }
}
