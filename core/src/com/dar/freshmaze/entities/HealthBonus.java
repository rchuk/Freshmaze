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
import com.dar.freshmaze.util.RectangleUtil;

import java.util.Random;

public class HealthBonus extends Actor {
    Texture texture = new Texture(Gdx.files.internal("heart.png"));
    Sprite sprite = new Sprite(new Texture(Gdx.files.internal("heart.png")));
    TextureRegion region;
    private Body body;
    private final World physWorld;

    private final BattleLevelRoom room;

    private boolean pendingDestroy = false;

    public HealthBonus(World physWorld, BattleLevelRoom room) {
        super();
        this.room = room;
        final Rectangle r = RectangleUtil.shrink(room.getBounds(), new Vector2(1.0f, 1.0f));
        region = new TextureRegion(texture);
        Random rand = new Random();
        // sprite.setPosition(r.getX() + r.getWidth() * 0.5f,  r.getY() + r.getHeight() * 0.5f);
        sprite.setPosition((int) CommonHelper.randomPoint(r).x, (int) CommonHelper.randomPoint(r).y);
        sprite.setSize(1.0f, 1.0f);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        setTouchable(Touchable.enabled);
        this.physWorld = physWorld;
        final CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0.5f, 0.5f));
        circle.setRadius(0.5f);
        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.fixedRotation = true;
        bd.position.set(new Vector2(sprite.getX(), sprite.getY()));
        bd.gravityScale = 0;
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;
        body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.setUserData(this);
        body.createFixture(fdef);
    }

    public void destroy() {
        pendingDestroy = true;
    }

    public void kill() {
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
    }
    @Override
    public boolean remove() {
        physWorld.destroyBody(body);
        return super.remove();
    }
}
