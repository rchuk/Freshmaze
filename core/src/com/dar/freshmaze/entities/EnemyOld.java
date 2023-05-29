package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
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

public class EnemyOld extends Actor {
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
    private Body body;
    private World physWorld;

    public EnemyOld(Rectangle r) {
        super();
        region = new TextureRegion(texture);
        sprite.setPosition(r.getX() * 128 + r.getWidth() * 64,  r.getY() * 128 + r.getHeight() * 64);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);
        this.physWorld = Closet.getWorld();;
        final CircleShape circle = new CircleShape();
        circle.setPosition(IsometricUtil.isoToCart(new Vector2( 128, 0)));
        circle.setRadius(64);
        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.position.set(new Vector2(sprite.getX(), sprite.getY()));
        bd.linearDamping = 0.5f;
        bd.angularDamping = 0.5f;
        body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.setUserData(this);

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
        if (!boxForward) {
            body.setLinearVelocity(IsometricUtil.isoToCart(new Vector2(-deltaPx, -deltaPy).scl(10000)));

        }
        if (boxForward) {
            body.setLinearVelocity(IsometricUtil.isoToCart(new Vector2(deltaPx, deltaPy).scl(10000)));
        }
        if (++boxIndex == boxSize) {
            boxForward = !boxForward;
            boxIndex = 0;
        }
        MoveToAction mta = new MoveToAction();
        mta.setPosition(body.getPosition().x, body.getPosition().y);
        mta.setDuration(0);
        addAction(mta);

    }
   }
