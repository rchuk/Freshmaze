package com.dar.freshmaze.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.common.CommonHelper;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.entities.HealthBonus;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import java.util.Random;

public class EnemyGenerator {
    private final World physWorld;
    private final Stage stage;

    public EnemyGenerator(World physWorld, Stage stage) {
        this.physWorld = physWorld;
        this.stage = stage;
    }

    public Array<EnemyOld> generate(BattleLevelRoom room) {
        final Array<EnemyOld> enemies = new Array<>();
        Random rand = new Random();
        if(rand.nextBoolean())
            enemies.add(createEnemy(room));
        if(rand.nextBoolean())
            enemies.add(createEnemy(room));
        if(rand.nextBoolean() && rand.nextBoolean())
            enemies.add(createEnemy(room));
        if(rand.nextInt(3) >= 0)
            stage.addActor(new HealthBonus(physWorld, room));

        enemies.add(createEnemy(room));
        enemies.forEach(stage::addActor);

        return enemies;
    }

    private EnemyOld createEnemy(BattleLevelRoom room) {
        return new EnemyOld(physWorld, room, getSpawnPos(room));
    }

    private static Vector2 getSpawnPos(BattleLevelRoom room) {
        return new Vector2((int)CommonHelper.randomPoint(room.getBounds()).x, (int)CommonHelper.randomPoint(room.getBounds()).y);
    }
}
