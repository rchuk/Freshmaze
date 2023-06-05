package com.dar.freshmaze.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PhysActor extends Actor {
    private final World physWorld;

    private Body body;

    private boolean pendingDestroy = false;
    private boolean isDestroyed = false;

    public PhysActor(World physWorld, Body body) {
        this.physWorld = physWorld;
        this.body = body;

        body.setUserData(this);
    }

    public Body getBody() {
        return body;
    }

    public World getPhysWorld() {
         return physWorld;
    }

    public boolean isDestroyed() {
         return isDestroyed;
    }

    public void teleport(Vector2 pos) {
        body.setTransform(pos, 0.0f);
        updatePosition();
    }

    public void destroy() {
        pendingDestroy = true;
    }

    @Override
    public boolean remove() {
        destroy();

        return super.remove();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (pendingDestroy) {
            if (body != null)
                physWorld.destroyBody(body);
            remove();

            pendingDestroy = false;
            isDestroyed = true;

            return;
        }

        updatePosition();
    }

    private void updatePosition() {
        setPosition(getBody().getPosition().x, getBody().getPosition().y);
    }
}
