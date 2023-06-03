package com.dar.freshmaze.level;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.level.tilemap.rooms.LevelRoom;

public class EnemyGenerator {
    private final World physWorld;
    private final Stage stage;

    public EnemyGenerator(World physWorld, Stage stage) {
        this.physWorld = physWorld;
        this.stage = stage;
    }

    public Array<EnemyOld> generate(BattleLevelRoom room) {
        final Array<EnemyOld> enemies = new Array<>();

        enemies.add(new EnemyOld(physWorld, room)); // TODO: Add ability to spawn enemies at arbitrary points

        enemies.forEach(stage::addActor);

        return enemies;
    }
}
