package com.dar.freshmaze.level.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dar.freshmaze.level.bitmap.LevelBitmap;

public class LevelTilemap implements Disposable {
    private final Texture tiles;

    private final StaticTiledMapTile wallTile;
    private final StaticTiledMapTile floorTile;

    private final int tileSize;
    private TiledMap tilemap;

    public LevelTilemap(String tilesetPath, int tileSize) {
        this.tileSize = tileSize;
        tiles = new Texture(Gdx.files.internal(tilesetPath));
        final TextureRegion[][] splitTiles = TextureRegion.split(tiles, tileSize, tileSize);

        // TODO: Create tileset
        floorTile = new StaticTiledMapTile(splitTiles[0][0]);
        wallTile = new StaticTiledMapTile(splitTiles[0][1]);

        // wallTile.getProperties().put("is_walkable", Boolean.FALSE);
        // floorTile.getProperties().put("is_walkable", Boolean.TRUE);

        wallTile.getObjects().add(new RectangleMapObject());
    }

    public int getTileSize() {
        return tileSize;
    }

    public TiledMap getTilemap() {
        return tilemap;
    }

    public void generate(LevelBitmap bitmap) {
        tilemap = new TiledMap();

        final MapLayers layers = tilemap.getLayers();
        final TiledMapTileLayer layer = new TiledMapTileLayer(bitmap.getWidth(), bitmap.getHeight(), tileSize, tileSize / 2);
        layer.setOffsetY(0.25f * tileSize);

        for (int yi = 0; yi < bitmap.getWidth(); ++yi) {
            for (int xi = 0; xi < bitmap.getHeight(); ++xi) {
                final LevelBitmap.Cell bitmapCell = bitmap.getCell(xi, yi);
                final TiledMapTile tile = mapBitmapCellToTile(bitmapCell);
                if (tile == null)
                    continue;

                final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                layer.setCell(xi, yi, cell);

                final int finalXi = xi;
                final int finalYi = yi;
                tile.getObjects().getByType(RectangleMapObject.class).forEach(obj -> {
                    final Rectangle rect = obj.getRectangle();

                    layer.getObjects().add(new RectangleMapObject(
                            rect.x + finalXi * tileSize,
                            rect.y + finalYi * tileSize,
                            rect.width * tileSize,
                            rect.height * tileSize
                    ));
                });
            }
        }

        layers.add(layer);
    }

    public Array<Body> createPhysObjects(World physWorld) {
        final Array<Body> bodies = new Array<Body>();

        final TiledMapTileLayer layer = tilemap.getLayers().getByType(TiledMapTileLayer.class).get(0); //

        layer.getObjects().getByType(RectangleMapObject.class).forEach(obj -> {
            final Rectangle rect = obj.getRectangle();
            final PolygonShape shape = rectangleToPhysPolygon(rect);

            final BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            final Body body = physWorld.createBody(bd);
            body.createFixture(shape, 1);

            bodies.add(body);

            shape.dispose();
        });

        return bodies;
    }

    private static PolygonShape rectangleToPhysPolygon(Rectangle rect) {
        final PolygonShape polygon = new PolygonShape();
        polygon.setAsBox(
                rect.width * 0.5f,
                rect.height * 0.5f,
                rect.getCenter(new Vector2()),
                0.0f
        );

        return polygon;
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
