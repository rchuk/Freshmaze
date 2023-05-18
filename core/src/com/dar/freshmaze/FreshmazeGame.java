package com.dar.freshmaze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FreshmazeGame extends ApplicationAdapter {
	private final static float WIDTH = 640;
	private final static float HEIGHT = 480;

	private OrthographicCamera camera;
	private FitViewport viewport;


	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new FitViewport(WIDTH, HEIGHT, camera);
	}

	@Override
	public void render () {
		camera.update();

		ScreenUtils.clear(0.1f, 0.1f, 0.25f, 1.0f);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose () {

	}
}
