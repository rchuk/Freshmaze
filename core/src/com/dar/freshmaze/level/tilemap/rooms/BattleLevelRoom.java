package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.level.EnemyGenerator;

public class BattleLevelRoom extends LevelRoom {
    private final Array<EnemyOld> enemies;
    private boolean isCleared = false;

    public BattleLevelRoom(Rectangle bounds, EnemyGenerator enemyGenerator) {
        super(bounds);

        enemies = enemyGenerator.generate(this);
    }

    @Override
    public void onPlayerEnter(Bob bob) {
        if (!isCleared)
            setIsOpen(false);
    }

    public void onEnemyDeath(EnemyOld enemy) {
        enemies.removeValue(enemy, true);

        if (enemies.isEmpty()) {
            setIsOpen(true);

            isCleared = true;
        }
    }
}
