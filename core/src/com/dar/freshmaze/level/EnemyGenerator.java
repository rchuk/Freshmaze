package com.dar.freshmaze.level;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.Enemy;
import com.dar.freshmaze.entities.EnemyOld;

public class EnemyGenerator {
    private final World physWorld;
    private final Stage stage;

    public EnemyGenerator(World physWorld, Stage stage) {
        this.physWorld = physWorld;
        this.stage = stage;
    }

    public Array<EnemyOld> generate(LevelRoom room) {
        final Array<EnemyOld> enemies = new Array<>();

        enemies.add(new EnemyOld(physWorld, room.getBounds())); // TODO: Add ability to spawn enemies at arbitrary points

        enemies.forEach(stage::addActor);

        return enemies;
    }
}
