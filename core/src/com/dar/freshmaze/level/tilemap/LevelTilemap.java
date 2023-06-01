package com.dar.freshmaze.level.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.dar.freshmaze.level.bitmap.LevelBitmap;
import com.dar.freshmaze.level.tilemap.tiles.DynamicEntranceTile;
import com.dar.freshmaze.level.tilemap.tiles.DynamicTile;

import java.util.Objects;

public class LevelTilemap implements Disposable {
    private final Texture tiles;

    public final StaticTiledMapTile floorTile;
    public final StaticTiledMapTile wallTile;
    public final StaticTiledMapTile entranceOpenTile;
    public final StaticTiledMapTile entranceClosedTile;

    private final int tileSize;
    private TiledMap tilemap;
    private final Array<Body> physBodies = new Array<>();
    private final ObjectMap<CellPos, DynamicTile> dynamicTiles = new ObjectMap<>();

    private final World physWorld;

    public LevelTilemap(World physWorld, String tilesetPath, int tileSize) {
        this.physWorld = physWorld;
        this.tileSize = tileSize;
        tiles = new Texture(Gdx.files.internal(tilesetPath));
        final TextureRegion[][] splitTiles = TextureRegion.split(tiles, tileSize, tileSize);

        // TODO: Create tileset
        floorTile = new StaticTiledMapTile(splitTiles[0][0]);
        wallTile = new StaticTiledMapTile(splitTiles[0][1]);
        entranceOpenTile = new StaticTiledMapTile(splitTiles[0][2]);
        entranceClosedTile = new StaticTiledMapTile(splitTiles[0][3]);

        // wallTile.getProperties().put("is_walkable", Boolean.FALSE);
        // floorTile.getProperties().put("is_walkable", Boolean.TRUE);

        wallTile.getObjects().add(new RectangleMapObject());
        entranceClosedTile.getObjects().add(new RectangleMapObject());
    }

    public int getTileSize() {
        return tileSize;
    }

    public TiledMap getTilemap() {
        return tilemap;
    }

    // TODO: Remove this duplicated code
    public Vector2 cellPosToVec(Vector2 cellPos) {
        return new Vector2(
                cellPos.x * tileSize,
                cellPos.y * tileSize
        );
    }

    public Vector2 cellPosToVec(CellPos cellPos) {
        return new Vector2(
                cellPos.x * tileSize,
                cellPos.y * tileSize
        );
    }

    public CellPos vecToCellPos(Vector2 pos) {
        return new CellPos(
                MathUtils.floor(pos.x / tileSize),
                MathUtils.floor(pos.y / tileSize)
        );
    }

    public Vector2 vecToCellPosVec(Vector2 pos) {
        return new Vector2(
                MathUtils.floor(pos.x / tileSize),
                MathUtils.floor(pos.y / tileSize)
        );
    }
    //

    public World getPhysWorld() {
        return physWorld;
    }

    public Array<Body> getPhysBodies() {
        return physBodies;
    }

    public void generate(LevelBitmap bitmap) {
        tilemap = new TiledMap();
        physBodies.forEach(physWorld::destroyBody);
        physBodies.clear();
        dynamicTiles.forEach(entry -> {
            final Body physBody = entry.value.getPhysBody();
            if (physBody != null)
                physWorld.destroyBody(physBody);
        });
        dynamicTiles.clear();

        final MapLayers layers = tilemap.getLayers();
        layers.add(createLayer(bitmap.getWidth(), bitmap.getHeight()));
        layers.add(createLayer(bitmap.getWidth(), bitmap.getHeight()));

        for (int yi = 0; yi < bitmap.getHeight(); ++yi) {
            for (int xi = 0; xi < bitmap.getWidth(); ++xi) {
                final LevelBitmap.Cell bitmapCell = bitmap.getCell(xi, yi);

                mapBitmapCellToTile(new CellPos(xi, yi), bitmapCell);
            }
        }
    }

    public DynamicTile getDynamicTile(CellPos cellPos) {
        return dynamicTiles.get(cellPos);
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

    private TiledMapTileLayer createLayer(int width, int height) {
        final TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileSize, tileSize / 2);
        layer.setOffsetY(0.25f * tileSize);

        return layer;
    }

    // TODO: Create more tiles
    private void mapBitmapCellToTile(CellPos pos, LevelBitmap.Cell bitmapCell) {
        switch (bitmapCell.getKind()) {
            case Wall:
                placeStaticTile(pos, wallTile, Layer.Wall);
                break;

            case Room:
            case Hall:
                placeStaticTile(pos, floorTile, Layer.Floor);
                break;

            case HallEntrance:
                placeStaticTile(pos, floorTile, Layer.Floor);
                placeDynamicTile(new DynamicEntranceTile(this, pos, entranceOpenTile, entranceClosedTile), Layer.Wall);
                break;

            default:
                break;
        }
    }

    private void placeDynamicTile(DynamicTile dynamicTile, Layer layerIndex) {
        final CellPos pos = dynamicTile.getCellPos();
        placeTile(pos, dynamicTile.getDefaultTile(), layerIndex);

        dynamicTiles.put(pos, dynamicTile);
    }

    private void placeStaticTile(CellPos pos, TiledMapTile tile, Layer layerIndex) {
        placeTile(pos, tile, layerIndex);

        final Body physBody = createTilePhysBody(pos, tile);
        if (physBody != null)
            physBodies.add(physBody);
    }

    public void placeTile(CellPos pos, TiledMapTile tile, Layer layerIndex) {
        final TiledMapTileLayer layer = tilemap.getLayers().getByType(TiledMapTileLayer.class).get(layerIndex.getIndex());
        final TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);
        layer.setCell(pos.getX(), pos.getY(), cell);
    }

    public Body createTilePhysBody(CellPos pos, TiledMapTile tile) {
        final Vector2 worldPos = cellPosToVec(pos);
        final Array<RectangleMapObject> objects = tile.getObjects().getByType(RectangleMapObject.class);
        if (objects.isEmpty())
            return null;

        final Rectangle tileRect = objects.get(0).getRectangle();
        final Rectangle rect = new Rectangle(
                tileRect.x + worldPos.x,
                tileRect.y + worldPos.y,
                tileRect.width * tileSize,
                tileRect.height * tileSize
        );

        final PolygonShape shape = rectangleToPhysPolygon(rect);

        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        final Body body = physWorld.createBody(bd);
        body.createFixture(shape, 1);

        shape.dispose();

        return body;
    }

    @Override
    public void dispose() {
        tiles.dispose();
    }

    public static class CellPos {
        private final int x;
        private final int y;

        public CellPos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (obj == null || getClass() != obj.getClass())
                return false;

            final CellPos other = (CellPos)obj;

            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public enum Layer {
        Floor(0),
        Wall(1);

        private final int index;

        public int getIndex() {
            return index;
        }

        Layer(int index) {
            this.index = index;
        }
    }
}
