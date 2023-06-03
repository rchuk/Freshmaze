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
import com.dar.freshmaze.common.CommonHelper;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.util.IsometricUtil;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.Random;

public class EnemyOld extends Actor {
    Texture texture = new Texture(Gdx.files.internal("still.png"));
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("still.png")));
    public final float movementSpeed;
    public final float deltaPx;
    public final float deltaPy;
    public static final float deltaS = 0.00001f;
    private final int boxSize;
    private int boxIndex = 0;
    private boolean boxForward = true;
    //    TextureRegion region;
    public boolean movingRight = false;
    public boolean movingLeft = false;
    public boolean movingUp = false;
    public boolean movingDown = false;
    TextureRegion region;
    private Body body;
    private final World physWorld;

    private final BattleLevelRoom room;

    private boolean pendingDestroy = false;

    public EnemyOld(World physWorld, BattleLevelRoom room) {
        super();
        this.room = room;
        region = new TextureRegion(texture);
        Random rand = new Random();
        // sprite.setPosition(r.getX() + r.getWidth() * 0.5f,  r.getY() + r.getHeight() * 0.5f);
        sprite.setPosition((int) CommonHelper.randomPoint(room.getBounds()).x, (int) CommonHelper.randomPoint(room.getBounds()).y);
        sprite.setSize(1.0f, 1.0f);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);
        this.physWorld = physWorld;
        final CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0.5f, 0.5f));
        circle.setRadius(0.5f);
        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.position.set(new Vector2(sprite.getX(), sprite.getY()));
        bd.linearDamping = rand.nextFloat();
        bd.angularDamping = rand.nextFloat();
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;
        body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.setUserData(this);
        body.createFixture(fdef);
        deltaPx = rand.nextFloat() * 2;
        deltaPy = rand.nextFloat() * 2;
        boxSize = rand.nextInt(500);
        movementSpeed = (rand.nextFloat() + 0.5f) * 2;
    }

    public void destroy() {
        pendingDestroy = true;
    }

    public void kill() {
        room.onEnemyDeath(this);

        destroy();
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
    public void teleport(Vector2 pos) {
        setPosition(body.getPosition().x, body.getPosition().y);
        body.setTransform(pos, 0.0f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (pendingDestroy) {
            physWorld.destroyBody(body);
            remove();

            pendingDestroy = false;

            return;
        }

        if (!boxForward) {
            body.setLinearVelocity(IsometricUtil.isoToCart(new Vector2(-deltaPx, -deltaPy).scl(movementSpeed)));

        }
        if (boxForward) {
            body.setLinearVelocity(IsometricUtil.isoToCart(new Vector2(deltaPx, deltaPy).scl(movementSpeed)));
        }
        if (++boxIndex == boxSize) {
            boxForward = !boxForward;
            boxIndex = 0;
        }
        // MoveToAction mta = new MoveToAction();
        // mta.setPosition(body.getPosition().x, body.getPosition().y);
        // mta.setDuration(0);
        // addAction(mta);
        setPosition(body.getPosition().x, body.getPosition().y);
    }
}
