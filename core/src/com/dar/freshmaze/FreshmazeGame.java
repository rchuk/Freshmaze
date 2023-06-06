package com.dar.freshmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.screens.TestScreen;
import com.dar.freshmaze.util.TimeUtil;

public class FreshmazeGame extends Game {
	public final static float WIDTH = 640;
	public final static float HEIGHT = 480;

	public SpriteBatch batch;
	public ShapeRenderer shape;

	@Override
	public void create () {
		TimeUtil.init();

		batch = new SpriteBatch();
		shape = new ShapeRenderer();

		start();
	}

	public void start() {
		if (getScreen() != null)
			getScreen().dispose();

		final OrthographicCamera camera = new OrthographicCamera();
		final Viewport viewport = new FitViewport(WIDTH, HEIGHT, camera);

		setScreen(new TestScreen(this, camera, viewport));
	}

	@Override
	public void dispose () {
		batch.dispose();
		shape.dispose();
	}
}
