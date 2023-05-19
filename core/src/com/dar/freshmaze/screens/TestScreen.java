package com.dar.freshmaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dar.freshmaze.FreshmazeGame;
import com.dar.freshmaze.level.graph.LevelGraph;
import com.dar.freshmaze.level.graph.LevelNode;
import com.dar.freshmaze.level.graph.LevelNodeGenerationRules;
import com.dar.freshmaze.level.graph.LevelNodeGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestScreen implements Screen {
    private static final float cameraSpeed = 75.0f;

    private final FreshmazeGame game;

    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final LevelNodeGenerator levelNodeGenerator;
    private List<Color> leafColors;

    public TestScreen(FreshmazeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.zoom = 0.2f;
        viewport = new FitViewport(FreshmazeGame.WIDTH, FreshmazeGame.HEIGHT, camera);

        levelNodeGenerator = new LevelNodeGenerator();
        generateLevelNodes();
    }

    private void generateLevelNodes() {
        levelNodeGenerator.generate(
                new Vector2(64, 64),
                2, //1
                new LevelNodeGenerationRules(10, 16, 40, 0.75f)
        );

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
        game.shape.setProjectionMatrix(camera.combined);

        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.BLACK);

        game.shape.rect(0.0f, 0.0f, 100.0f, 100.0f);

        game.shape.end();

        debugRenderLevelNodes();
        debugRenderLevelGraph();
        debugRenderHalls();
    }

    private void debugRenderLevelNodes() {
        int index = 0;

        for (LevelNode leaf : levelNodeGenerator.getLeaves()) {
            game.shape.setProjectionMatrix(camera.combined);

            game.shape.begin(ShapeRenderer.ShapeType.Filled);
            game.shape.setColor(leafColors.get(index++));
            game.shape.rect(leaf.getBounds().x, leaf.getBounds().y, leaf.getBounds().width, leaf.getBounds().height);

            game.shape.setColor(Color.WHITE);
            game.shape.rect(leaf.getRoomBounds().x, leaf.getRoomBounds().y, leaf.getRoomBounds().width, leaf.getRoomBounds().height);
            game.shape.end();
        }
    }

    private void debugRenderLevelGraph() {
        for (Map.Entry<LevelNode, HashMap<LevelNode, LevelGraph.Edge>> entry : levelNodeGenerator.getGraph().entrySet()) {
            final LevelNode firstNode = entry.getKey();

            for (Map.Entry<LevelNode, LevelGraph.Edge> connection : entry.getValue().entrySet()) {
                final LevelNode secondNode = connection.getKey();

                game.shape.begin(ShapeRenderer.ShapeType.Filled);
                game.shape.setColor(Color.BLACK);
                game.shape.line(firstNode.getRoomBounds().getCenter(new Vector2()), secondNode.getRoomBounds().getCenter(new Vector2()));
                game.shape.end();
            }
        }
    }

    private void debugRenderHalls() {
        for (Rectangle hall : levelNodeGenerator.getHalls()) {
            game.shape.begin(ShapeRenderer.ShapeType.Filled);
            game.shape.setColor(Color.ORANGE);
            game.shape.rect(hall.x, hall.y, hall.width, hall.height);
            game.shape.end();
        }
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            generateLevelNodes();


        final float offsetX = Gdx.input.isKeyPressed(Input.Keys.A) ? -1.0f :
                              Gdx.input.isKeyPressed(Input.Keys.D) ? 1.0f :
                              0.0f;

        final float offsetY = Gdx.input.isKeyPressed(Input.Keys.S) ? -1.0f :
                              Gdx.input.isKeyPressed(Input.Keys.W) ? 1.0f :
                              0.0f;

        camera.translate(offsetX * cameraSpeed * dt, offsetY * cameraSpeed * dt);
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

    }
}
