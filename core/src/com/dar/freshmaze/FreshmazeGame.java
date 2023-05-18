package com.dar.freshmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dar.freshmaze.screens.TestScreen;

public class FreshmazeGame extends Game {
	public final static float WIDTH = 640;
	public final static float HEIGHT = 480;

	public SpriteBatch batch;


	@Override
	public void create () {
		batch = new SpriteBatch();

		setScreen(new TestScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
