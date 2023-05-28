package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.dar.freshmaze.util.IsometricUtil;

public class Enemy extends Actor {
    Texture texture = new Texture(Gdx.files.internal("still.png"));
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("still.png")));
    public static final float deltaPx = 2;
    public static final float deltaPy = 2;
    public static final float deltaS = 0.00001f;
    private static final int boxSize = 50;
    private int boxIndex = 0;
    private boolean boxForward = true;
    //    TextureRegion region;
    public boolean movingRight = false;
    public boolean movingLeft = false;
    public boolean movingUp = false;
    public boolean movingDown = false;
    TextureRegion region;

    public Enemy() {
        super();
        region = new TextureRegion(texture);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);
    }

    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.setTransformMatrix(IsometricUtil.ISO_TRANSFORMATION_MATRIX); // Not needed if the sprites is already drawn as isometric
        sprite.draw(batch);
        batch.setColor(getColor());
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        MoveByAction mba = new MoveByAction();
        if (!boxForward)
            mba.setAmount(-deltaPx, -deltaPy);
        if (boxForward)
            mba.setAmount(deltaPx, deltaPy);
        if (++boxIndex == boxSize) {
            boxForward = !boxForward;
            boxIndex = 0;
        }
        mba.setDuration(deltaS);
        addAction(mba);

    }

}
