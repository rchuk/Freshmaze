package com.dar.freshmaze.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.Enemy;
import com.dar.freshmaze.entities.Entity;
import com.dar.freshmaze.entities.HealthBonus;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.util.RectangleUtil;

public class EnemyGenerator {
    private final World physWorld;
    private final Stage stage;
    private Dungeon dungeon;

    public EnemyGenerator(World physWorld, Stage stage) {
        this.physWorld = physWorld;
        this.stage = stage;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Result generate(BattleLevelRoom room) {
        final Array<Enemy> enemies = new Array<>();
        final Array<Entity> otherEntities = new Array<>();

        if(MathUtils.randomBoolean())
            enemies.add(createEnemy(room));
        if(MathUtils.randomBoolean() && dungeon.getLevelIndex() >= 1)
            enemies.add(createEnemy(room));
        if(MathUtils.randomBoolean() && MathUtils.randomBoolean() && dungeon.getLevelIndex() >= 2)
            enemies.add(createEnemy(room));
        if(MathUtils.random(3) >= dungeon.getLevelIndex())
            otherEntities.add(createHealthBouns(room));

        enemies.add(createEnemy(room));
        enemies.forEach(stage::addActor);
        otherEntities.forEach(stage::addActor);

        return new Result(enemies, otherEntities);
    }

    private HealthBonus createHealthBouns(BattleLevelRoom room) {
        return new HealthBonus(physWorld, room, getSpawnPos(RectangleUtil.shrink(room.getBounds(), new Vector2(1.0f, 1.0f))));
    }

    private Enemy createEnemy(BattleLevelRoom room) {
        return new Enemy(physWorld, room, getSpawnPos(room.getBounds()));
    }

    private static Vector2 getSpawnPos(Rectangle rect) {
        return RectangleUtil.getRandomPoint(rect);
    }

    public static class Result {
        public final Array<Enemy> enemies;
        public final Array<Entity> otherEntities;

        public Result(Array<Enemy> enemies, Array<Entity> otherEntities) {
            this.enemies = enemies;
            this.otherEntities = otherEntities;
        }
    }
}
