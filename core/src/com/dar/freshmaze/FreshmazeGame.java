package com.dar.freshmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.screens.GameScreen;
import com.dar.freshmaze.util.TimeUtil;

public class FreshmazeGame extends Game {
	public final static float WIDTH = 1280;
	public final static float HEIGHT = 720;

	public SpriteBatch batch;
	public ShapeRenderer shape;

	@Override
	public void create () {
		TimeUtil.init();

		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound.mp3"));
        sound.setLooping(sound.play(1.0f), true);

		start(false);
	}

	public void start(boolean isRestart) {
		if (getScreen() != null)
			getScreen().dispose();

		final OrthographicCamera camera = new OrthographicCamera();
		final Viewport viewport = new FitViewport(WIDTH, HEIGHT, camera);

		setScreen(new GameScreen(this, camera, viewport, isRestart));
	}

	@Override
	public void dispose () {
		batch.dispose();
		shape.dispose();
	}
}
