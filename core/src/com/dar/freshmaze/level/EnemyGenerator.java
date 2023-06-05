package com.dar.freshmaze.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.common.CommonHelper;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.entities.Entity;
import com.dar.freshmaze.entities.HealthBonus;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.util.RectangleUtil;

import java.util.Random;

public class EnemyGenerator {
    private final World physWorld;
    private final Stage stage;

    public EnemyGenerator(World physWorld, Stage stage) {
        this.physWorld = physWorld;
        this.stage = stage;
    }

    public Result generate(BattleLevelRoom room) {
        final Array<EnemyOld> enemies = new Array<>();
        final Array<Entity> otherEntities = new Array<>();

        if(MathUtils.randomBoolean())
            enemies.add(createEnemy(room));
        if(MathUtils.randomBoolean())
            enemies.add(createEnemy(room));
        if(MathUtils.randomBoolean() && MathUtils.randomBoolean())
            enemies.add(createEnemy(room));
        if(MathUtils.random(3) >= 0)
            otherEntities.add(createHealthBouns(room));

        enemies.add(createEnemy(room));
        enemies.forEach(stage::addActor);
        otherEntities.forEach(stage::addActor);

        return new Result(enemies, otherEntities);
    }

    private HealthBonus createHealthBouns(BattleLevelRoom room) {
        return new HealthBonus(physWorld, room, getSpawnPos(RectangleUtil.shrink(room.getBounds(), new Vector2(1.0f, 1.0f))));
    }

    private EnemyOld createEnemy(BattleLevelRoom room) {
        return new EnemyOld(physWorld, room, getSpawnPos(room.getBounds()));
    }

    private static Vector2 getSpawnPos(Rectangle rect) {
        return new Vector2((int)CommonHelper.randomPoint(rect).x, (int)CommonHelper.randomPoint(rect).y);
    }

    public class Result {
        public final Array<EnemyOld> enemies;
        public final Array<Entity> otherEntities;

        public Result(Array<EnemyOld> enemies, Array<Entity> otherEntities) {
            this.enemies = enemies;
            this.otherEntities = otherEntities;
        }
    }
}
