package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.dar.freshmaze.util.IsometricUtil;

public class Bob extends Actor {
    Texture texture = new Texture(Gdx.files.internal("still.png"));
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("still.png")));
    public static final float deltaPx = 5;
    public static final float deltaPy = 5;
    public static final float deltaS = 0.00001f;
    //    TextureRegion region;
    public boolean movingRight = false;
    public boolean movingLeft = false;
    public boolean movingUp = false;
    public boolean movingDown = false;
    TextureRegion region;

    public Bob() {
        super();
        region = new TextureRegion(texture);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);
        addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.RIGHT)
                    movingRight = true;
                if (keycode == Input.Keys.LEFT)
                    movingLeft = true;
                if (keycode == Input.Keys.UP)
                    movingUp = true;
                if (keycode == Input.Keys.DOWN)
                    movingDown = true;

                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.RIGHT)
                    movingRight = false;
                if (keycode == Input.Keys.LEFT)
                    movingLeft = false;
                if (keycode == Input.Keys.UP)
                    movingUp = false;
                if (keycode == Input.Keys.DOWN)
                    movingDown = false;
                return true;

            }
        });
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
        float dx = 0;
        float dy = 0;
        if (movingRight)
            dx = deltaPx;
        if (movingLeft)
            dx = -deltaPx;
        if (movingUp )
            dy = deltaPy;
        if (movingDown)
            dy = -deltaPy;
        MoveByAction mba = new MoveByAction();
        mba.setAmount(dx, dy);
        mba.setDuration(deltaS);
        addAction(mba);
//        SequenceAction sequenceAction = new SequenceAction();
//        ColorAction ca = new ColorAction();
//        ca.setEndColor(Color.RED);
//        ca.setDuration(0.5f);
//        ColorAction ca1 = new ColorAction();
//        ca1.setEndColor(Color.CLEAR);
//        ca1.setDuration(0.5f);
//        sequenceAction.addAction(ca);
//        sequenceAction.addAction(ca1);
//        addAction(sequenceAction);

    }

}
