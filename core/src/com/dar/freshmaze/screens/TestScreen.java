package com.dar.freshmaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.FreshmazeGame;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.graphics.DepthSortedStage;
import com.dar.freshmaze.indicator.RectIndicator;
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
    private final Stage uiStage;
    private boolean mainInput = true;
    private final Bob bob;

    public TestScreen(FreshmazeGame game, OrthographicCamera camera, Viewport viewport) {
        this.game = game;

        this.camera = camera;
        camera.zoom = CAMERA_ZOOM;
        this.viewport = viewport;

        physWorld = new World(Vector2.Zero, true);
        physWorld.setContactListener(new WorldContactListener());
        physDebugRenderer = new Box2DDebugRenderer();

        stage = new DepthSortedStage(viewport);
        final Level level = new Level(physWorld, stage);

        bob = new Bob(physWorld, level, Vector2.Zero);
        stage.addActor(bob);

        dungeon = new Dungeon(level, bob);

        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(bob);

        uiStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        createUI();
    }

    private void createUI() {
        final RectIndicator healthIndicator = new RectIndicator(new RectIndicator.FloatRangeBinder() {
            @Override
            public float getValue() {
                return dungeon.getBob().getHealth();
            }

            @Override
            public float getMaxValue() {
                return dungeon.getBob().getMaxHealth();
            }
        });
        healthIndicator.setIndicatorColor(Color.RED);
        healthIndicator.setBounds(0.0f, uiStage.getHeight() - 40.0f, 200.0f, 40.0f);

        final RectIndicator attackIndicator = new RectIndicator(new RectIndicator.FloatRangeBinder() {
            @Override
            public float getValue() {
                return 1.0f - Math.max(dungeon.getBob().getAttackTimeLeft(), 0.0f);
            }

            @Override
            public float getMaxValue() {
                return dungeon.getBob().getTimePerAttack();
            }
        });
        attackIndicator.setIndicatorColor(Color.TEAL);
        attackIndicator.setBounds(200.0f, uiStage.getHeight() - 40.0f, 200.0f, 40.0f);

        uiStage.addActor(healthIndicator);
        uiStage.addActor(attackIndicator);
    }

    private void generateLevel() {
        // level.generate();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(bob.getHealth() <= 0) {
            stage.setKeyboardFocus(null);
            gameover();
        }
        handleInput(delta);

        dungeon.update(delta);
        stage.act();
        uiStage.act();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(0.1f, 0.1f, 0.25f, 1.0f);

        if (!enableFreeCamera) {
            final Vector2 playerPos = new Vector2(dungeon.getBob().getX(), dungeon.getBob().getY());
            camera.position.set(IsometricUtil.cartToIso(playerPos), 0.0f);
            camera.zoom = CAMERA_ZOOM;
        }
        camera.update();

        final Level level = dungeon.getLevel();
        level.render(camera, delta, TILEMAP_FLOOR_LAYER, false);
        // level.debugRender(camera, delta, Level.DebugRender.ROOMS | Level.DebugRender.GRID);
        // debugRenderBobCell();
        level.render(camera, delta, TILEMAP_WALL_LAYER, true);

        debugRenderAxes();

        final Rectangle viewBounds = dungeon.getLevel().getTilemapRenderer().getViewBounds();
        stage.getBatch().getShader().bind();
        stage.getBatch().getShader().setUniform2fv("bounds_vert", new float[] { viewBounds.y, viewBounds.height }, 0, 2);
        stage.getViewport().apply();
        stage.draw();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        uiStage.getViewport().apply();
        uiStage.draw();

        // Draw physics shapes in cartesian coordinates system (top-down view)
        // physDebugRenderer.render(physWorld, camera.combined);

        // physDebugRenderer.render(physWorld, new Matrix4(camera.combined).mul(IsometricUtil.ISO_TRANSFORMATION_MATRIX));

        physWorld.step(1.0f / 60.0f, 6, 2);
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
        if(mainInput) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                generateLevel();

            if (Gdx.input.isKeyJustPressed(Input.Keys.P))
                enableFreeCamera = !enableFreeCamera;
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                System.out.println("touchDown 2");


            }

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
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
                Gdx.app.exit();
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

        uiStage.getViewport().update(width, height);
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
        uiStage.dispose();
    }
    private void gameover() {
        mainInput = false;
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        // Store the default libGDX font under the name "default".
        skin.add("default", new BitmapFont());

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        // Create a table that fills the screen. Everything else will go inside this table.
        TextureRegion image2 = new TextureRegion(new Texture(Gdx.files.internal("gameover.png")));
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, 0);
        uiStage.addActor(table);
        Image image = new Image(image2);
        image.setScaling(Scaling.fill);
        table.add(image).width(image2.getRegionWidth()).height(image2.getRegionHeight()).row();
        table.columnDefaults(1);
        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton button = new TextButton("Press q to quite", skin);
        table.add(button).fill().row();

//        final TextButton button2 = new TextButton("Press r to restart", skin);
//        table.add(button2).fill().row();

    }
    private void victory() {
        mainInput = false;
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        // Store the default libGDX font under the name "default".
        skin.add("default", new BitmapFont());

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        // Create a table that fills the screen. Everything else will go inside this table.
        TextureRegion image2 = new TextureRegion(new Texture(Gdx.files.internal("victory.png")));
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, 0);
        uiStage.addActor(table);
        Image image = new Image(image2);
        image.setScaling(Scaling.fill);
        table.add(image).width(image2.getRegionWidth()).height(image2.getRegionHeight()).row();
        table.columnDefaults(1);
        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton button = new TextButton("Press q to quite", skin);
        table.add(button).fill().row();

//        final TextButton button2 = new TextButton("Press r to restart", skin);
//        table.add(button2).fill().row();

    }

}
