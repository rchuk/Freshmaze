package com.dar.freshmaze.entities;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dar.freshmaze.Closet;

public class Enemy {
    private final World physWorld;
    private Body body;

    public Enemy() {
        final CircleShape circle = new CircleShape();
        circle.setPosition(Vector2.Zero);
        circle.setRadius(46.0f);
        this.physWorld = Closet.getWorld();;
        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.position.set(Vector2.Zero);
        bd.linearDamping = 0.5f;
        bd.angularDamping = 0.5f;
        body = physWorld.createBody(bd);
        body.createFixture(circle, 1);

        circle.dispose();
    }
}
