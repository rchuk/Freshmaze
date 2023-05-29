package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.dar.freshmaze.Closet;
import com.dar.freshmaze.util.IsometricUtil;

public class Bob extends Actor {
    Texture texture = new Texture(Gdx.files.internal("still.png"));
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("still.png")));
    public static final float deltaPx = 128;
    public static final float deltaPy = 128;
    public static final float deltaS = 0.00001f;
    //    TextureRegion region;
    public boolean movingRight = false;
    public boolean movingLeft = false;
    public boolean movingUp = false;
    public boolean movingDown = false;
    TextureRegion region;
    private Body body;
    private  World physWorld;

    public Bob() {
        super();
        region = new TextureRegion(texture);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);
        this.physWorld = Closet.getWorld();;
        final CircleShape circle = new CircleShape();
        circle.setPosition(IsometricUtil.isoToCart(new Vector2(sprite.getX() + 128, sprite.getY())));
        circle.setRadius(64);
        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.position.set(Vector2.Zero);
        bd.linearDamping = 0.5f;
        bd.angularDamping = 0.5f;
        body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.setUserData(this);
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
        body.setLinearVelocity(IsometricUtil.isoToCart(new Vector2(dx, dy).scl(10000)));
        MoveToAction mta = new MoveToAction();
        mta.setPosition(body.getPosition().x, body.getPosition().y);
        mta.setDuration(0);
        addAction(mta);
//        MoveByAction mba = new MoveByAction();
//        mba.setAmount(dx, dy);
//        mba.setDuration(deltaS);
//        addAction(mba);



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