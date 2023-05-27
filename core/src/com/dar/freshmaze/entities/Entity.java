package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.g2d.*;

public class Entity extends Actor {
    Texture texture = new Texture(Gdx.files.internal("still.png"));
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("still.png")));
    float actorX = 0, actorY = 0;
    TextureRegion region;


    public Entity() {
        super();
        region = new TextureRegion(texture);
        setBounds(region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float alpha) {//Draw it
        batch.draw(texture, actorX, actorY);
    }

    @Override
    public void act(float delta) {//Update it
    }
    public void setX(float x) {
        actorX = x;
    }
}
