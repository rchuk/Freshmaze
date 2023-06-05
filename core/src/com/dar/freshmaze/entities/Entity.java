package com.dar.freshmaze.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.dar.freshmaze.util.IsometricUtil;

public class Entity extends PhysActor {
    private Sprite sprite;
    private Vector2 spriteOffset;
    private SpriteKind spriteKind;
    private Matrix4 renderMatrix;


    public Entity(World physWorld, Sprite sprite, Body body, Vector2 spriteOffset, SpriteKind spriteKind, Vector2 spawnPos) {
        super(physWorld, body);

        this.sprite = sprite;
        this.spriteOffset = spriteOffset;
        this.spriteKind = spriteKind;
        this.renderMatrix = spriteKind.getRenderMatrix();

        setBounds(spawnPos.x, spawnPos.y, sprite.getWidth(), sprite.getHeight());
        teleport(spawnPos);
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    protected void positionChanged() {
        final Vector2 cartPos = new Vector2(getX() + spriteOffset.x, getY() + spriteOffset.y);
        final Vector2 pos = spriteKind == SpriteKind.Isometric ? IsometricUtil.cartToIso(cartPos) : cartPos;
        sprite.setPosition(pos.x, pos.y);

        super.positionChanged();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        setShaderSortHeight(batch, 0.0f);

        batch.setTransformMatrix(renderMatrix);
        sprite.draw(batch);

        batch.flush();
    }

    protected void setShaderSortHeight(Batch batch, float offset) {
        final Vector2 isoPos = IsometricUtil.cartToIso(new Vector2(getX() + getWidth() / 2, getY() + getHeight() / 2));
        batch.getShader().setUniformf("height", isoPos.y + offset);
    }

    protected enum SpriteKind {
        Isometric,
        Transform;

        public Matrix4 getRenderMatrix() {
            switch (this) {
                case Isometric:
                    return new Matrix4().idt();
                case Transform:
                    return IsometricUtil.ISO_TRANSFORMATION_MATRIX;
                default:
                    throw new RuntimeException("Unhandled case in switch");
            }
        }
    }
}
