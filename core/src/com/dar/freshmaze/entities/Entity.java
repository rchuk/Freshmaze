package com.dar.freshmaze.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dar.freshmaze.util.IsometricUtil;

public class Entity extends Actor {
    private final World physWorld;

    private Body body;
    private Sprite sprite;
    private Vector2 spriteOffset;

    private boolean pendingDestroy = false;

    public Entity(World physWorld, Sprite sprite, Body body, Vector2 spriteOffset, Vector2 spawnPos) {
        super();

        this.physWorld = physWorld;
        this.sprite = sprite;
        this.body = body;
        this.spriteOffset = spriteOffset;

        body.setUserData(this);

        setBounds(spawnPos.x, spawnPos.y, sprite.getWidth(), sprite.getHeight());
        teleport(spawnPos);
    }

    public Body getBody() {
        return body;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void destroy() {
        pendingDestroy = true;
    }

    public void teleport(Vector2 pos) {
        body.setTransform(pos, 0.0f);
        updatePosition();
    }

    @Override
    public boolean remove() {
        destroy();

        return super.remove();
    }

    @Override
    protected void positionChanged() {
        final Vector2 isoPos = IsometricUtil.cartToIso(new Vector2(getX() + spriteOffset.x, getY() + spriteOffset.y));
        sprite.setPosition(isoPos.x, isoPos.y);

        super.positionChanged();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        final Vector2 isoPos = IsometricUtil.cartToIso(new Vector2(getX() + getWidth() / 2, getY() + getHeight() / 2));
        batch.getShader().setUniformf("height", isoPos.y);

        batch.setTransformMatrix(new Matrix4().idt());
        sprite.draw(batch);

        batch.flush();
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

        updatePosition();
    }

    private void updatePosition() {
        setPosition(body.getPosition().x, body.getPosition().y);
    }
}
