package com.dar.freshmaze.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dar.freshmaze.FreshmazeGame;

public class TestScreen implements Screen {
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
        ScreenUtils.clear(0.1f, 0.1f, 0.25f, 1.0f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // TODO: Draw something
        game.batch.end();
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
