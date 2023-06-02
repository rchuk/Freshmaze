package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.level.EnemyGenerator;

public class BattleLevelRoom extends LevelRoom {
    private final Array<EnemyOld> enemies;

    public BattleLevelRoom(Rectangle bounds, EnemyGenerator enemyGenerator) {
        super(bounds);

        enemies = enemyGenerator.generate(this);
    }
}
