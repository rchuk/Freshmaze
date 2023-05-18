package com.dar.freshmaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dar.freshmaze.FreshmazeGame;

public class TestScreen implements Screen {
    private static final float cameraSpeed = 75.0f;

    private final FreshmazeGame game;

    private final OrthographicCamera camera;
    private final FitViewport viewport;

    public TestScreen(FreshmazeGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(FreshmazeGame.WIDTH, FreshmazeGame.HEIGHT, camera);
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
    }

    private void handleInput(float dt) {
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
