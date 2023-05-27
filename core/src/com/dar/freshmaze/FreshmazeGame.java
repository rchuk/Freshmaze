package com.dar.freshmaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dar.freshmaze.entities.Entity;
import com.dar.freshmaze.screens.TestScreen;

public class FreshmazeGame extends Game {
	public final static float WIDTH = 640;
	public final static float HEIGHT = 480;

	public SpriteBatch batch;
	public ShapeRenderer shape;

	private Stage stage;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		stage = new Stage(new ScreenViewport());
		Actor actor = new Entity();
		stage.addActor(actor);
		Gdx.input.setInputProcessor(stage);
		stage.setKeyboardFocus(actor);
		setScreen(new TestScreen(this));
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
