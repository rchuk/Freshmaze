package com.dar.freshmaze.screens;

import com.badlogic.gdx.Game;
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
import com.badlogic.gdx.utils.Align;
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
import com.dar.freshmaze.ui.ScreenTransition;
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

    private final static int[] TILEMAP_FLOOR_LAYER = new int[] { LevelTilemap.Layer.Floor.getIndex(), LevelTilemap.Layer.FloorOverlay.getIndex() };
    private final static int[] TILEMAP_WALL_LAYER = new int[] { LevelTilemap.Layer.Wall.getIndex() };

    private final Skin skin = createSkin();

    private final Dungeon dungeon;
    private final DepthSortedStage stage;
    private final Stage uiStage;
    private boolean mainInput = true;
    private boolean hasGameBegun = false;
    private final Bob bob;
    private boolean shouldRestart = false;


    private ScreenTransition startGameTransitionScreen;

    public TestScreen(FreshmazeGame game, OrthographicCamera camera, Viewport viewport, boolean skipMenu) {
        this.game = game;

        this.camera = camera;
        camera.zoom = CAMERA_ZOOM;
        this.viewport = viewport;
        hasGameBegun = skipMenu;

        physWorld = new World(Vector2.Zero, true);
        physWorld.setContactListener(new WorldContactListener());
        physDebugRenderer = new Box2DDebugRenderer();

        stage = new DepthSortedStage(viewport);
        final Level level = new Level(physWorld, stage);

        bob = new Bob(physWorld, level, Vector2.Zero);
        stage.addActor(bob);

        dungeon = new Dungeon(level, bob);

        uiStage = new Stage(new FitViewport(game.WIDTH, game.HEIGHT));
        if (!hasGameBegun)
            addGameStartUI();
        else
            startGame();
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

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (shouldRestart) {
            game.start(true);

            return;
        }

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

        // debugRenderAxes();

        final Rectangle viewBounds = dungeon.getLevel().getTilemapRenderer().getViewBounds();
        stage.setVerticalViewBounds(new Vector2(viewBounds.y, viewBounds.height));
        stage.shaderSetVerticalViewBounds();
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

    private void startGame() {
        hasGameBegun = true;

        uiStage.clear();

        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(bob);

        createUI();
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
        if (!hasGameBegun) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                startGameTransitionScreen.setIsFrozen(false);

            return;
        }

        if(mainInput) {
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
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R))
                shouldRestart = true;
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

    private void addGameStartUI() {
        Gdx.input.setInputProcessor(stage);

        startGameTransitionScreen = new ScreenTransition(1.0f, 3.0f, true, this::startGame);
        startGameTransitionScreen.setIsFrozen(true);
        uiStage.addActor(startGameTransitionScreen);

        // Create a table that fills the screen. Everything else will go inside this table.
        TextureRegion logoTexture = new TextureRegion(new Texture(Gdx.files.internal("freshmaze_logo.png")));
        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, 0);
        uiStage.addActor(table);
        Image logoImage = new Image(logoTexture);
        logoImage.setScaling(Scaling.fillX);
        table.add(logoImage).top().row();

        TextureRegion infoTexture = new TextureRegion(new Texture(Gdx.files.internal("start_info.png")));
        Image infoImage = new Image(infoTexture);
        infoImage.setScaling(Scaling.fillX);
        table.add(infoImage).row();
    }

    private void gameover() {
        mainInput = false;
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, 0);
        uiStage.addActor(table);
        TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("gameover.png")));
        Image image = new Image(texture);
        image.setScaling(Scaling.fillX);
        table.add(image).row();
        table.columnDefaults(1);

        final TextButton button = new TextButton("Press Q to quit", skin);
        table.add(button).fill().row();

        final TextButton button2 = new TextButton("Press R to restart", skin);
        table.add(button2).fill().row();

        uiStage.addActor(new ScreenTransition(1.0f, 3.0f, false));
    }
    private void victory() {
        mainInput = false;
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, 0);
        uiStage.addActor(table);

        TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("victory.png")));
        Image image = new Image(texture);
        image.setScaling(Scaling.fillX);
        table.add(image).row();
        table.columnDefaults(1);

        final TextButton button = new TextButton("Press q to quite", skin);
        table.add(button).fill().row();

        final TextButton button2 = new TextButton("Press r to restart", skin);
        table.add(button2).fill().row();

        uiStage.addActor(new ScreenTransition(1.0f, 3.0f, false));
    }

    private static Skin createSkin() {
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

        return skin;
    }
}
