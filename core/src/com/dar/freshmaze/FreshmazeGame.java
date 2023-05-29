package com.dar.freshmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.screens.TestScreen;

public class FreshmazeGame extends Game {
	public final static float WIDTH = 640;
	public final static float HEIGHT = 480;

	public SpriteBatch batch;
	public ShapeRenderer shape;

	private Stage stage;
    public Bob actor;
	@Override
	public void create () {
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		OrthographicCamera camera = new OrthographicCamera();
		Viewport       viewport = new FitViewport(WIDTH, HEIGHT, camera);
		setScreen(new TestScreen(this, camera, viewport));
		stage = new Stage(viewport);
		actor = new Bob();
		stage.addActor(actor);
		for(Actor a : Closet.getActors())
		    stage.addActor(a);
		Gdx.input.setInputProcessor(stage);
		stage.setKeyboardFocus(actor);
	}

	@Override
	public void render() {
		super.render();
		float delta = Gdx.graphics.getDeltaTime();
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();

	}

	@Override
	public void dispose () {
		batch.dispose();
		shape.dispose();
		//stage.dispose();
	}
}
