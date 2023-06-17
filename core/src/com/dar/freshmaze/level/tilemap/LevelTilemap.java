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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.dar.freshmaze.level.Dungeon;
import com.dar.freshmaze.level.bitmap.LevelBitmap;
import com.dar.freshmaze.level.tilemap.tiles.EntranceTile;
import com.dar.freshmaze.level.tilemap.tiles.SpikesTile;
import com.dar.freshmaze.level.tilemap.tiles.TeleportTile;
import com.dar.freshmaze.level.tilemap.tiles.DynamicTile;

import java.util.Objects;

/**
 * TileMap for levels.
 */
public class LevelTilemap implements Disposable {
    private final Texture tiles;

    public final StaticTiledMapTile floorTile;
    public final StaticTiledMapTile wallTile;
    public final StaticTiledMapTile entranceOpenTile;
    public final StaticTiledMapTile entranceClearedTile;
    public final StaticTiledMapTile entranceClosedTile;
    public final StaticTiledMapTile teleportMonolithTile;
    public final StaticTiledMapTile chestClosedTile;
    public final StaticTiledMapTile chestOpenTile;
    public final StaticTiledMapTile spikesTile;

    private final float tileSize;
    private final int textureTileSize;
    private TiledMap tilemap;
    private final Array<Body> physBodies = new Array<>();
    private final ObjectMap<CellPos, DynamicTile> dynamicTiles = new ObjectMap<>();

    private final World physWorld;
    private Dungeon dungeon;

    /**
     *
     * @param physWorld
     * @param tilesetPath
     * @param tileSize
     * @param textureTileSize
     */
    public LevelTilemap(World physWorld, String tilesetPath, float tileSize, int textureTileSize) {
        this.physWorld = physWorld;
        this.tileSize = tileSize;
        this.textureTileSize = textureTileSize;
        tiles = new Texture(Gdx.files.internal(tilesetPath));
        final TextureRegion[][] splitTiles = TextureRegion.split(tiles, textureTileSize, textureTileSize);

        floorTile = new StaticTiledMapTile(splitTiles[0][0]);
        wallTile = new StaticTiledMapTile(splitTiles[0][1]);
        entranceOpenTile = new StaticTiledMapTile(splitTiles[0][2]);
        entranceClearedTile = new StaticTiledMapTile(splitTiles[0][3]);
        entranceClosedTile = new StaticTiledMapTile(splitTiles[0][4]);
        teleportMonolithTile = new StaticTiledMapTile(splitTiles[0][5]);
        chestClosedTile = new StaticTiledMapTile(splitTiles[0][6]);
        chestOpenTile = new StaticTiledMapTile(splitTiles[0][7]);
        spikesTile = new StaticTiledMapTile(splitTiles[1][0]);

        wallTile.getObjects().add(new RectangleMapObject());
        entranceClosedTile.getObjects().add(new RectangleMapObject());
        teleportMonolithTile.getObjects().add(new RectangleMapObject());
        chestClosedTile.getObjects().add(new RectangleMapObject());
        chestOpenTile.getObjects().add(new RectangleMapObject());
        spikesTile.getObjects().add(new RectangleMapObject());
    }

    public float getTileSize() {
        return tileSize;
    }

    public int getTextureTileSize() {
        return textureTileSize;
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

    public void setDungeon(Dungeon newDungeon) {
        dungeon = newDungeon;
    }
    public Dungeon getDungeon() { return dungeon; }

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
        for (int i = 0; i < 3; ++i)
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
        final TiledMapTileLayer layer = new TiledMapTileLayer(width, height, textureTileSize, textureTileSize / 2);
        layer.setOffsetY(0.25f * textureTileSize);

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
                placeDynamicTile(new EntranceTile(this, pos, entranceOpenTile, entranceClosedTile, entranceClearedTile));
                break;

            case Teleport:
                placeStaticTile(pos, floorTile, Layer.Floor);
                placeDynamicTile(new TeleportTile(this, pos, teleportMonolithTile, dungeon));
                break;

            case Spikes:
                placeStaticTile(pos, floorTile, Layer.Floor);
                placeDynamicTile(new SpikesTile(this, pos, spikesTile));
                break;

            default:
                break;
        }
    }

    public void placeDynamicTile(DynamicTile dynamicTile) {
        final CellPos pos = dynamicTile.getCellPos();
        placeTile(pos, dynamicTile.getDefaultTile(), dynamicTile.getDefaultLayer());

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
        return createTilePhysBodyImpl(pos, tile, false);
    }

    public Body createTilePhysBodySensor(CellPos pos, TiledMapTile tile) {
        return createTilePhysBodyImpl(pos, tile, true);
    }

    private Body createTilePhysBodyImpl(CellPos pos, TiledMapTile tile, boolean isSensor) {
        if (tile == null)
            return null;

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

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = isSensor;
        body.createFixture(fdef);

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
        FloorOverlay(1),
        Wall(2);

        private final int index;

        public int getIndex() {
            return index;
        }

        Layer(int index) {
            this.index = index;
        }
    }
}
