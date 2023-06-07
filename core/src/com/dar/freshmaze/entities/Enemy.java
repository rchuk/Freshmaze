package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.g2d.*;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Enemy extends Entity {
    public final float movementSpeed;
    public final float deltaPx;
    public final float deltaPy;
    private final int boxSize;
    private int boxIndex = 0;
    private boolean boxForward = true;

    private final BattleLevelRoom room;

    public Enemy(World physWorld, BattleLevelRoom room, Vector2 spawnPos) {
        super(physWorld, createSprite(), createBody(physWorld), new Vector2(0.5f, -0.5f), SpriteKind.Isometric, spawnPos);

        this.room = room;

        deltaPx = MathUtils.random(2.0f);
        deltaPy = MathUtils.random(2.0f);
        boxSize = MathUtils.random(300) + 50;
        movementSpeed = (MathUtils.random() + 0.5f) * 2;
    }

    public void kill() {
        room.onEnemyDeath(this);

        destroy();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isDestroyed())
            return;

//        if (++boxIndex == boxSize ) {
//            boxForward = !boxForward;
//            boxIndex = 0;
//        }
        if(getBody().getPosition().x - deltaPx * movementSpeed < room.getBounds().x || getBody().getPosition().y - deltaPy * movementSpeed < room.getBounds().y) {
            boxForward = true;
//            boxIndex = 0;
        } else if(getBody().getPosition().x + deltaPx * movementSpeed > room.getBounds().x + room.getBounds().width || getBody().getPosition().y + deltaPy * movementSpeed > room.getBounds().y + room.getBounds().height) {
            boxForward = false;
//            boxIndex = 0;
        }

        if (!boxForward) {
            getBody().setLinearVelocity(new Vector2(-deltaPx, -deltaPy).scl(movementSpeed));
        } else {
            getBody().setLinearVelocity(new Vector2(deltaPx, deltaPy).scl(movementSpeed));
        }
    }

    private static Sprite createSprite() {
        final Sprite sprite = new Sprite(new Texture(Gdx.files.internal("enemy.png")));
        sprite.setSize(1.0f, 1.0f);

        return sprite;
    }

    private static Body createBody(World physWorld) {
        final CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0.5f, 0.5f));
        circle.setRadius(0.5f);

        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.linearDamping = MathUtils.random();
        bd.angularDamping = MathUtils.random();

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;

        final Body body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.createFixture(fdef);

        return body;
    }
}
