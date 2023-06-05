package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.g2d.*;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.dar.freshmaze.util.IsometricUtil;

public class HealthBonus extends Entity {
    private final BattleLevelRoom room;


    public HealthBonus(World physWorld, BattleLevelRoom room, Vector2 spawnPos) {
        super(physWorld, createSprite(), createBody(physWorld), Vector2.Zero, SpriteKind.Transform, spawnPos);

        this.room = room;
    }

    private static Sprite createSprite() {
        final Sprite sprite = new Sprite(new Texture(Gdx.files.internal("heart.png")));
        sprite.setSize(1.0f, 1.0f);

        return sprite;
    }

    private static Body createBody(World physWorld) {
        final CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0.5f, 0.5f));
        circle.setRadius(0.5f);

        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.fixedRotation = true;
        bd.gravityScale = 0;

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;

        final Body body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.createFixture(fdef);

        return body;
    }
}
