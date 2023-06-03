package com.dar.freshmaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.FreshmazeGame;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.indicator.Indicator;
import com.dar.freshmaze.level.Dungeon;
import com.dar.freshmaze.level.Level;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.util.IsometricUtil;
import com.dar.freshmaze.world.WorldContactListener;

public class TestScreen implements Screen {
    private static final float CAMERA_ZOOM = 3.0f / 128.0f;

    private static final float CAMERA_SPEED = 5.0f;
    private static final float CAMERA_ZOOM_SPEED = 0.25f;

    private final FreshmazeGame game;

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final World physWorld;
    private final Box2DDebugRenderer physDebugRenderer;
    private boolean enableFreeCamera = false;

    private final static int[] TILEMAP_FLOOR_LAYER = new int[] { LevelTilemap.Layer.Floor.getIndex() };
    private final static int[] TILEMAP_WALL_LAYER = new int[] { LevelTilemap.Layer.Wall.getIndex() };


    private final Dungeon dungeon;
    private final Stage stage;
    private Indicator indicator;

    public TestScreen(FreshmazeGame game, OrthographicCamera camera, Viewport viewport) {
        this.game = game;

        this.camera = camera;
        camera.zoom = CAMERA_ZOOM;
        this.viewport = viewport;

        physWorld = new World(Vector2.Zero, true); //TODO: Change graphics scale to 1 unit = 1 meter
        physWorld.setContactListener(new WorldContactListener());
        physDebugRenderer = new Box2DDebugRenderer();

        stage = new Stage(viewport);
        final Level level = new Level(physWorld, stage);

        final Bob bob = new Bob(physWorld, level, Vector2.Zero);
        stage.addActor(bob);

        dungeon = new Dungeon(level, bob);

        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(bob);
        indicator = new Indicator(level);
    }

    private void generateLevel() {
        // level.generate();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        dungeon.update(delta);
        stage.act();

        ScreenUtils.clear(0.1f, 0.1f, 0.25f, 1.0f);

        //implement camera follow
        if (!enableFreeCamera) {
            final Vector2 playerPos = new Vector2(dungeon.getBob().getX(), dungeon.getBob().getY());
            camera.position.set(IsometricUtil.cartToIso(playerPos), 0.0f);
            camera.zoom = CAMERA_ZOOM;
        }
        camera.update();

        final Level level = dungeon.getLevel();
        level.render(camera, delta, TILEMAP_FLOOR_LAYER);
        level.debugRender(camera, delta, Level.DebugRender.ROOMS | Level.DebugRender.GRID);
        debugRenderBobCell();
        level.render(camera, delta, TILEMAP_WALL_LAYER);

        debugRenderAxes();

        stage.draw();

        // Draw physics shapes in cartesian coordinates system (top-down view)
        // physDebugRenderer.render(physWorld, camera.combined);

        physDebugRenderer.render(physWorld, new Matrix4(camera.combined).mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX));

        physWorld.step(1.0f / 60.0f, 6, 2);
        indicator.begin();
        indicator.end();
    }

    private void debugRenderBobCell() {
        final Vector2 bobPos = new Vector2(dungeon.getBob().getX() + dungeon.getBob().getWidth() / 2, dungeon.getBob().getY() + dungeon.getBob().getHeight() / 2);
        final LevelTilemap tilemap = dungeon.getLevel().getTilemap();
        final Vector2 cellPos = tilemap.vecToCellPosVec(bobPos);
        final Vector2 worldPos = tilemap.cellPosToVec(cellPos);

        game.shape.setTransformMatrix(IsometricUtil.ISO_TRANSFORMATION_MATRIX);
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.YELLOW);
        game.shape.rect(worldPos.x, worldPos.y, tilemap.getTileSize(), tilemap.getTileSize());
        game.shape.end();
    }

    private void debugRenderAxes() {
        final Vector2 xAxis = IsometricUtil.cartToIso(Vector2.X);
        final Vector2 yAxis = IsometricUtil.cartToIso(Vector2.Y);

        game.shape.setTransformMatrix(new Matrix4().idt());
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        game.shape.setColor(Color.RED);
        game.shape.rectLine(Vector2.Zero, xAxis.scl(128.0f), 0.1f);
        game.shape.setColor(Color.GREEN);
        game.shape.rectLine(Vector2.Zero, yAxis.scl(128.0f),  0.1f);

        game.shape.setColor(Color.PURPLE);
        game.shape.rectLine(Vector2.Zero, new Vector2(Vector2.X).scl(128.0f), 0.1f);
        game.shape.setColor(Color.YELLOW);
        game.shape.rectLine(Vector2.Zero, new Vector2(Vector2.Y).scl(128.0f), 0.1f);

        game.shape.end();
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            generateLevel();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P))
            enableFreeCamera = !enableFreeCamera;

        if (enableFreeCamera) {
            if (Gdx.input.isKeyPressed(Input.Keys.MINUS))
                camera.zoom += CAMERA_ZOOM_SPEED * dt;
            else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS))
                camera.zoom -= CAMERA_ZOOM_SPEED * dt;

            camera.zoom = Math.max(0.01f, camera.zoom);

            final float cameraSpeedMult = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 2.0f : 1.0f;

            final Vector2 cameraMovementVec = getInputMovementVec(Input.Keys.J, Input.Keys.L, Input.Keys.K, Input.Keys.I, CAMERA_SPEED);
            camera.translate(cameraMovementVec.x * cameraSpeedMult * dt, cameraMovementVec.y * cameraSpeedMult * dt);
        }
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
        dungeon.dispose();
    }
}
