package com.dar.freshmaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.FreshmazeGame;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.level.Dungeon;
import com.dar.freshmaze.level.Level;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.util.IsometricUtil;
import com.dar.freshmaze.world.WorldContactListener;

public class TestScreen implements Screen {
    private static final float cameraSpeed = 512.0f;

    private final FreshmazeGame game;

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final World physWorld;
    private final Box2DDebugRenderer physDebugRenderer;

    private final static int[] TILEMAP_FLOOR_LAYER = new int[] { LevelTilemap.Layer.Floor.getIndex() };
    private final static int[] TILEMAP_WALL_LAYER = new int[] { LevelTilemap.Layer.Wall.getIndex() };


    private final Dungeon dungeon;
    private final Stage stage;

    public TestScreen(FreshmazeGame game, OrthographicCamera camera, Viewport viewport) {
        this.game = game;

        this.camera = camera;
        camera.zoom = 10.0f;
        this.viewport = viewport;

        physWorld = new World(Vector2.Zero, true); //TODO: Change graphics scale to 1 unit = 1 meter
        physWorld.setContactListener(new WorldContactListener());
        physDebugRenderer = new Box2DDebugRenderer();

        stage = new Stage(viewport);
        final Bob bob = new Bob(physWorld, new Rectangle());
        stage.addActor(bob);

        final Level level = new Level(physWorld, stage);
        dungeon = new Dungeon(level, bob);

        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(bob);
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

        camera.update();

        final Level level = dungeon.getLevel();
        level.render(camera, delta, TILEMAP_FLOOR_LAYER);
        level.debugRender(camera, delta, Level.DebugRender.ROOMS | Level.DebugRender.GRID);
        level.render(camera, delta, TILEMAP_WALL_LAYER);

        debugRenderAxes();

        stage.draw();

        // Draw physics shapes in cartesian coordinates system (top-down view)
        // physDebugRenderer.render(physWorld, camera.combined);

        physDebugRenderer.render(physWorld, new Matrix4(camera.combined).mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX));

        physWorld.step(1 / 60f, 6, 2);
    }

    private void debugRenderAxes() {
        final Vector2 xAxis = IsometricUtil.cartToIso(Vector2.X);
        final Vector2 yAxis = IsometricUtil.cartToIso(Vector2.Y);

        game.shape.setTransformMatrix(new Matrix4().idt());
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);

        game.shape.setColor(Color.RED);
        game.shape.rectLine(Vector2.Zero, xAxis.scl(64.0f * 128.0f), 0.1f);
        game.shape.setColor(Color.GREEN);
        game.shape.rectLine(Vector2.Zero, yAxis.scl(64.0f * 128.0f),  0.1f);

        game.shape.setColor(Color.PURPLE);
        game.shape.rectLine(Vector2.Zero, new Vector2(Vector2.X).scl(64.0f * 128.0f), 0.1f);
        game.shape.setColor(Color.YELLOW);
        game.shape.rectLine(Vector2.Zero, new Vector2(Vector2.Y).scl(64.0f * 128.0f), 0.1f);

        game.shape.end();
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            generateLevel();

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS))
            camera.zoom += 5.0 * dt;
        else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS))
            camera.zoom -= 5.0 * dt;

        final float cameraSpeedMult = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 2.0f : 1.0f;

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
        dungeon.dispose();
    }
}
