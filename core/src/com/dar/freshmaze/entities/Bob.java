package com.dar.freshmaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.level.Level;
import com.dar.freshmaze.level.tilemap.tiles.DynamicInteractableTile;
import com.dar.freshmaze.level.tilemap.tiles.SpikesTile;
import com.dar.freshmaze.util.IsometricUtil;

public class Bob extends Entity {
    final Texture attackTexture = new Texture(Gdx.files.internal("iso_circle_marker.png"));
    public static final float MOVEMENT_SPEED = 4.0f;
    public static final float deltaPx = 1.0f;
    public static final float deltaPy = 1.0f;
    public boolean movingRight = false;
    public boolean movingLeft = false;
    public boolean movingUp = false;
    public boolean movingDown = false;

    private boolean isAttacking = false;
    private boolean isInteracting = false;
    private final static float multiplier = 1.6f;
    private Level level;
    private int health;

    // Configurable
    private final static float attackSpeed = 1.0f;
    private final static float attackDisplayMult = 0.1f;

    private final static float timePerAttack = 1.0f / attackSpeed;
    private float attackTimeLeft;

    private final Array<Object> closeObjects = new Array<>();

    public Bob(World physWorld, Level level, Vector2 spawnPos) {
        super(physWorld, createSprite(), createBody(physWorld), new Vector2(0.5f, -0.5f), SpriteKind.Isometric, spawnPos);

        this.level = level;
        health = getMaxHealth();

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
                if (keycode == Input.Keys.B)
                    isAttacking = true;
                if (keycode == Input.Keys.E)
                    isInteracting = true;

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
                if (keycode == Input.Keys.B)
                    isAttacking = false;
                if (keycode == Input.Keys.E)
                    isInteracting = false;

                return true;

            }
        });
    }

    public float getAttackTimeLeft() {
        return attackTimeLeft;
    }

    public float getTimePerAttack() {
        return timePerAttack;
    }

    public void damage(int damage) {
        setHealth(getHealth() - damage);

        onDamage();
    }

    @Override
    public void draw(Batch batch, float alpha) {
        // TODO: Add shader if there's some time left
        if (attackTimeLeft >= (1.0f - attackDisplayMult) * timePerAttack) {
            final Vector2 pos = IsometricUtil.cartToIso(new Vector2(getX() - 0.5f, getY() - 0.5f));

            setShaderSortHeight(batch, 1.0f);
            batch.setColor(Color.RED);
            batch.draw(attackTexture, pos.x, pos.y - 0.5f, 2.0f, 2.0f);
            batch.flush();
        }

        getSprite().setColor(getColor());

        super.draw(batch, alpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isDestroyed())
            return;

        attackTimeLeft -= delta;

        processActions();

        float dx = 0;
        float dy = 0;
        if (movingRight)
            dx = deltaPx;
        if (movingLeft)
            dx = -deltaPx;
        if (movingUp)
            dy = deltaPy;
        if (movingDown)
            dy = -deltaPy;

        getBody().setLinearVelocity(IsometricUtil.isoToCart(new Vector2(dx, dy).nor().scl(MOVEMENT_SPEED)));
    }

    public void addObjectInRadius(Object userData) {
        if (userData == null)
            return;

        if (!processContact(userData))
            closeObjects.add(userData);
    }

    public void removeObjectInRadius(Object userData) {
        if (userData == null)
            return;

        closeObjects.removeValue(userData, true);
    }

    private void processActions() {
        boolean attacked = false;
        if (isAttacking) {
            if (attackTimeLeft <= 0) {
                attacked = true;
                attackTimeLeft = timePerAttack;

                //ColorAction ca = new ColorAction();
                //ca.setEndColor(Color.RED);
                //ca.setDuration(0.4f);
            }
        }

        for (Object obj : closeObjects) {
            if (obj instanceof EnemyOld) {
                if (attacked)
                    ((EnemyOld) obj).kill();
            } else if (obj instanceof DynamicInteractableTile) {
                if (isInteracting)
                    ((DynamicInteractableTile) obj).interact(this);
            } else if(obj instanceof HealthBonus) {
                setHealth(Math.min(getHealth() + 10, 100));
                ((HealthBonus) obj).remove();
                // ((HealthBonus) obj).teleport(new Vector2(1000, 1000));
            }
        }
    }

    private boolean processContact(Object obj) {
        if (obj instanceof EnemyOld) {
            damage(7);
        } else if (obj instanceof SpikesTile) {
            ((SpikesTile)obj).onTouch(this);

            return true;
        }

        return false;
    }

    private void onDamage() {
        ColorAction ca = new ColorAction();
        ca.setEndColor(Color.RED);
        ca.setDuration(0.4f);
        ColorAction ca1 = new ColorAction();
        ca1.setEndColor(Color.WHITE);
        ca1.setDuration(0.4f);
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(ca);
        sequenceAction.addAction(ca1);
        addAction(sequenceAction);

        addAction(ca);
    }

    private static Sprite createSprite() {
        final Sprite sprite = new Sprite(new Texture(Gdx.files.internal("player.png")));
        sprite.setSize(1.0f, 1.0f);

        return sprite;
    }

    private static Body createBody(World physWorld) {
        final CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0.5f, 0.5f));
        circle.setRadius(0.3f);

        final BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        bd.linearDamping = 1f;
        bd.angularDamping = 1f;

        final CircleShape circleSensor = new CircleShape();
        circleSensor.setPosition(new Vector2(0.5f, 0.5f));
        circleSensor.setRadius(0.5f * multiplier);

        final FixtureDef fdef = new FixtureDef();
        fdef.shape = circleSensor;
        fdef.isSensor = true;

        final Body body = physWorld.createBody(bd);
        body.createFixture(circle, 1);
        body.createFixture(fdef);

        return body;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return 100;
    }
}
