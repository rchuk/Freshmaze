package com.dar.freshmaze.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.dar.freshmaze.level.bitmap.LevelBitmap;
import com.dar.freshmaze.level.graph.*;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.level.tilemap.SortedIsometricTiledMapRenderer;
import com.dar.freshmaze.level.tilemap.SpikeGenerator;
import com.dar.freshmaze.level.tilemap.rooms.LevelRoom;
import com.dar.freshmaze.util.IsometricUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Level implements Disposable {
    private final LevelNodeGenerator nodeGenerator;
    private final EnemyGenerator enemyGenerator;
    private final SpikeGenerator spikeGenerator;
    private final LevelTilemap tilemap;
    private SortedIsometricTiledMapRenderer tilemapRenderer;

    private final ShapeRenderer shape;
    private List<Color> debugLeafColors;
    private Matrix4 debugRenderMatrix;
//    private Dungeon dungeon;
    public Level(World physWorld, Stage stage) {
        nodeGenerator = new LevelNodeGenerator();
        tilemap = new LevelTilemap(physWorld, "level/tiles/tiles.png", 1.0f, 128);
//        this.dungeon =dungeon;
        enemyGenerator = new EnemyGenerator(physWorld, stage);
        spikeGenerator = new SpikeGenerator();

        shape = new ShapeRenderer();
    }

    public LevelRoom generate(int levelIndex) {
        enemyGenerator.setDungeon(getTilemap().getDungeon());

        generateNodes(levelIndex);

        final LevelBitmap levelBitmap = new LevelBitmap();
        levelBitmap.generate(nodeGenerator);

        tilemap.generate(levelBitmap);
        tilemapRenderer = new SortedIsometricTiledMapRenderer(tilemap.getTilemap(), tilemap.getTileSize() / tilemap.getTextureTileSize());
        debugLeafColors = Stream
                .generate(() -> new Color(MathUtils.random(0, 0xFFFFFF)))
                .limit(nodeGenerator.getRooms().size())
                .collect(Collectors.toList());
        debugRenderMatrix = new Matrix4()
                .idt()
                .scl(tilemap.getTileSize(), tilemap.getTileSize(), 1.0f)
                .mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX);

        nodeGenerator.getRooms().forEach(room -> room.setLevel(this));

        return nodeGenerator.getSpawnRoom();
    }

    private void generateNodes(int levelIndex) {
        // TODO: Make levels progressively harder

        nodeGenerator.generate(
                new Vector2(64, 64),
                2,
                new LevelNodeGenerationRules(10, 16, 40, 0.75f, 2),
                enemyGenerator,
                spikeGenerator
        );
    }

    public List<LevelRoom> getRooms() {
        return Collections.unmodifiableList(nodeGenerator.getRooms());
    }

    public LevelTilemap getTilemap() {
        return tilemap;
    }

    public SortedIsometricTiledMapRenderer getTilemapRenderer() {
        return tilemapRenderer;
    }

    public void update(float dt) {
        nodeGenerator.getRooms().forEach(room -> room.act(dt));
    }

    public void render(OrthographicCamera camera, float dt, int[] layers, boolean writeDepth) {
        if (tilemapRenderer == null)
            return;

        tilemapRenderer.setWriteDepth(writeDepth);
        tilemapRenderer.setView(camera);
        tilemapRenderer.render(layers);
    }

    public void debugRender(Camera camera, float dt, int kind) {
        shape.setTransformMatrix(debugRenderMatrix);
        shape.setProjectionMatrix(camera.combined);

        if ((kind & DebugRender.LEAVES) == DebugRender.LEAVES)
            debugRenderLeaves();
        if ((kind & DebugRender.HALLS) == DebugRender.HALLS)
            debugRenderHalls();
        if ((kind & DebugRender.GRAPH) == DebugRender.GRAPH)
            debugRenderGraph();
        if ((kind & DebugRender.ROOMS) == DebugRender.ROOMS)
            debugRenderRooms();
        if ((kind & DebugRender.GRID) == DebugRender.GRID)
            debugRenderGrid();
    }

    private void debugRenderGrid() {
        final Vector2 levelSize = nodeGenerator.getLevelSize();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);

        for (int xi = 0; xi <= levelSize.x; ++xi)
            shape.rectLine(xi, 0.0f, xi, levelSize.y, 0.1f);

        for (int yi = 0; yi <= levelSize.y; ++yi)
            shape.rectLine(0.0f, yi, levelSize.x, yi, 0.1f);

        shape.end();
    }

    private void debugRenderRooms() {
        int index = 0;

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (LevelRoom room : nodeGenerator.getRooms()) {
            shape.setColor(debugLeafColors.get(index++).cpy().mul(1.0f, 1.0f, 1.0f, 0.1f));
            shape.rect(room.getBounds().x, room.getBounds().y, room.getBounds().width, room.getBounds().height);
        }
        shape.end();
    }

    private void debugRenderLeaves() {
        int index = 0;

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (LevelNode leaf : nodeGenerator.getLeaves()) {
            shape.setColor(debugLeafColors.get(index++));
            shape.rect(leaf.getBounds().x, leaf.getBounds().y, leaf.getBounds().width, leaf.getBounds().height);
        }
        shape.end();
    }

    private void debugRenderGraph() {
        shape.begin(ShapeRenderer.ShapeType.Filled);

        for (Map.Entry<LevelNode, HashMap<LevelNode, LevelGraph.Edge>> entry : nodeGenerator.getGraph().entrySet()) {
            final LevelNode firstNode = entry.getKey();

            for (Map.Entry<LevelNode, LevelGraph.Edge> connection : entry.getValue().entrySet()) {
                final LevelNode secondNode = connection.getKey();

                shape.setColor(Color.BLACK);
                shape.line(firstNode.getRoomBounds().getCenter(new Vector2()), secondNode.getRoomBounds().getCenter(new Vector2()));
            }
        }

        shape.end();
    }

    private void debugRenderHalls() {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (Rectangle hall : nodeGenerator.getHalls()) {
            shape.setColor(Color.ORANGE);
            shape.rect(hall.x, hall.y, hall.width, hall.height);
        }
        shape.end();
    }

    public static class DebugRender {
        public static final int NONE = 0;
        public static final int GRAPH = 1 << 0;
        public static final int LEAVES = 1 << 1;
        public static final int HALLS = 1 << 2;
        public static final int ROOMS = 1 << 3;
        public static final int GRID = 1 << 4;
    }
    @Override
    public void dispose() {
        if (tilemapRenderer != null)
            tilemapRenderer.dispose();
    }
}
