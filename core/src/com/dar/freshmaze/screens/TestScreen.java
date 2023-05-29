package com.dar.freshmaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.FreshmazeGame;
import com.dar.freshmaze.level.bitmap.LevelBitmap;
import com.dar.freshmaze.level.graph.LevelGraph;
import com.dar.freshmaze.level.graph.LevelNode;
import com.dar.freshmaze.level.graph.LevelNodeGenerationRules;
import com.dar.freshmaze.level.graph.LevelNodeGenerator;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.util.IsometricUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestScreen implements Screen {
    private static final float cameraSpeed = 512.0f;

    private final FreshmazeGame game;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private IsometricTiledMapRenderer tilemapRenderer;

    private final World physWorld;
    private final Box2DDebugRenderer physDebugRenderer;

    private final LevelNodeGenerator levelNodeGenerator;
    private List<Color> leafColors;
    private final LevelTilemap levelTilemap;

    private Body playerBody;
    private Texture isoCircleMarkerTexture;

    private Matrix4 debugLevelMatrix;

    public TestScreen(FreshmazeGame game, OrthographicCamera camera, Viewport viewport) {
        this.game = game;

        this.camera = camera;
        camera.zoom = 10.0f;
        this.viewport = viewport;

        physWorld = new World(Vector2.Zero, true); //TODO: Change graphics scale to 1 unit = 1 meter
        physDebugRenderer = new Box2DDebugRenderer();

        levelNodeGenerator = new LevelNodeGenerator();
        levelTilemap = new LevelTilemap(physWorld, "level/tiles/tiles.png", 128);
        debugLevelMatrix = new Matrix4().idt().scl(levelTilemap.getTileSize(), levelTilemap.getTileSize(), 1.0f).mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX);

        isoCircleMarkerTexture = new Texture(Gdx.files.internal("iso_circle_marker.png"));
        final CircleShape circle = new CircleShape();
        circle.setPosition(Vector2.Zero);
        circle.setRadius(46.0f);

        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.position.set(Vector2.Zero);
        playerBody = physWorld.createBody(bd);
        playerBody.createFixture(circle, 1);

        circle.dispose();

        generateLevelNodes();
    }

    private void generateLevelNodes() {
        levelNodeGenerator.generate(
                new Vector2(64, 64),
                2,
                new LevelNodeGenerationRules(10, 16, 40, 0.75f, 2)
        );

        final LevelBitmap levelBitmap = new LevelBitmap();
        levelBitmap.generate(levelNodeGenerator);

        levelTilemap.generate(levelBitmap);
        tilemapRenderer = new IsometricTiledMapRenderer(levelTilemap.getTilemap(), game.batch);

        leafColors = Stream
                .generate(() -> new Color(MathUtils.random(0, 0xFFFFFF)))
                .limit(levelNodeGenerator.getLeaves().size())
                .collect(Collectors.toList());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        handleInput(delta);

        ScreenUtils.clear(0.1f, 0.1f, 0.25f, 1.0f);

        camera.update();

        tilemapRenderer.setView(camera);
        tilemapRenderer.render();

        debugRenderLevelRooms();
        debugRenderLevelGrid();

        debugRenderAxes();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        final Vector2 isoMarkerPos = IsometricUtil.cartToIso(playerBody.getPosition());
        // TODO: Don't hardcode the values
        game.batch.draw(isoCircleMarkerTexture, isoMarkerPos.x - 64.0f, isoMarkerPos.y - 32.0f, 128.0f, 128.0f);

        game.batch.end();

        /*
        debugRenderLevelNodes();
        debugRenderLevelGraph();
        debugRenderHalls();

        debugRenderLevelGrid();
        */

        // Draw physics shapes in cartesian coordinates system (top-down view)
        // physDebugRenderer.render(physWorld, camera.combined);

        physDebugRenderer.render(physWorld, new Matrix4(camera.combined).mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX));

        physWorld.step(1/60f, 6, 2);
    }

    private void debugRenderAxes() {
        final Vector2 xAxis = IsometricUtil.cartToIso(Vector2.X);
        final Vector2 yAxis = IsometricUtil.cartToIso(Vector2.Y);

        game.shape.setTransformMatrix(new Matrix4().idt());
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        game.shape.setColor(Color.RED);
        game.shape.line(Vector2.Zero, xAxis.scl(64.0f * 128.0f));
        game.shape.setColor(Color.GREEN);
        game.shape.line(Vector2.Zero, yAxis.scl(64.0f * 128.0f));

        game.shape.setColor(Color.PURPLE);
        game.shape.line(Vector2.Zero, new Vector2(Vector2.X).scl(64.0f * 128.0f));
        game.shape.setColor(Color.YELLOW);
        game.shape.line(Vector2.Zero, new Vector2(Vector2.Y).scl(64.0f * 128.0f));

        game.shape.end();
    }
    private void debugRenderLevelGrid() {
        final Vector2 levelSize = levelNodeGenerator.getLevelSize();

        game.shape.setTransformMatrix(debugLevelMatrix);
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.BLACK);

        for (int xi = 0; xi <= levelSize.x; ++xi)
            game.shape.rectLine(xi, 0.0f, xi, levelSize.y, 0.1f);

        for (int yi = 0; yi <= levelSize.y; ++yi)
            game.shape.rectLine(0.0f, yi, levelSize.x, yi, 0.1f);

        game.shape.end();
    }

    private void debugRenderLevelRooms() {
        int index = 0;

        game.shape.setTransformMatrix(debugLevelMatrix);
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        for (LevelNode leaf : levelNodeGenerator.getLeaves()) {
            game.shape.setColor(leafColors.get(index++).cpy().mul(1.0f, 1.0f, 1.0f, 0.25f));
            game.shape.rect(leaf.getRoomBounds().x, leaf.getRoomBounds().y, leaf.getRoomBounds().width, leaf.getRoomBounds().height);
        }
        game.shape.end();
    }

    private void debugRenderLevelNodes() {
        int index = 0;

        game.shape.setTransformMatrix(debugLevelMatrix);
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        for (LevelNode leaf : levelNodeGenerator.getLeaves()) {
            game.shape.setColor(leafColors.get(index++));
            game.shape.rect(leaf.getBounds().x, leaf.getBounds().y, leaf.getBounds().width, leaf.getBounds().height);

            game.shape.setColor(Color.WHITE);
            game.shape.rect(leaf.getRoomBounds().x, leaf.getRoomBounds().y, leaf.getRoomBounds().width, leaf.getRoomBounds().height);
        }
        game.shape.end();
    }

    private void debugRenderLevelGraph() {
        game.shape.setTransformMatrix(debugLevelMatrix);
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        for (Map.Entry<LevelNode, HashMap<LevelNode, LevelGraph.Edge>> entry : levelNodeGenerator.getGraph().entrySet()) {
            final LevelNode firstNode = entry.getKey();

            for (Map.Entry<LevelNode, LevelGraph.Edge> connection : entry.getValue().entrySet()) {
                final LevelNode secondNode = connection.getKey();

                game.shape.setColor(Color.BLACK);
                game.shape.line(firstNode.getRoomBounds().getCenter(new Vector2()), secondNode.getRoomBounds().getCenter(new Vector2()));
            }
        }

        game.shape.end();
    }

    private void debugRenderHalls() {
        game.shape.setTransformMatrix(debugLevelMatrix);
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        for (Rectangle hall : levelNodeGenerator.getHalls()) {
            game.shape.setColor(Color.ORANGE);
            game.shape.rect(hall.x, hall.y, hall.width, hall.height);
        }
        game.shape.end();
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            generateLevelNodes();

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS))
            camera.zoom += 5.0 * dt;
        else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS))
            camera.zoom -= 5.0 * dt;


        final float cameraSpeedMult = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 2.0f : 1.0f;

        final float playerSpeed = 1000.0f;
        final Vector2 movementVec = IsometricUtil.isoToCart(getInputMovementVec(Input.Keys.A, Input.Keys.D, Input.Keys.S, Input.Keys.W, playerSpeed));
        playerBody.setLinearVelocity(movementVec);

        final Vector2 cameraMovementVec = getInputMovementVec(Input.Keys.J, Input.Keys.L, Input.Keys.K, Input.Keys.I, cameraSpeed);
        camera.translate(cameraMovementVec.x * cameraSpeedMult * dt, cameraMovementVec.y * cameraSpeedMult * dt);
    }

    private Vector2 getInputMovementVec(int leftKey, int rightKey, int downKey, int upKey, float speed) {
        final float valueX = getInputAxisValue(leftKey, rightKey);
        final float valueY = getInputAxisValue(downKey, upKey);

        return new Vector2(valueX, valueY)
                .nor()
                .scl(speed);
    }

    private float getInputAxisValue(int keyNegative, int keyPositive) {
        return Gdx.input.isKeyPressed(keyNegative) ? -1.0f :
               Gdx.input.isKeyPressed(keyPositive) ? 1.0f :
               0.0f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        tilemapRenderer.dispose();
        isoCircleMarkerTexture.dispose();
    }
}
