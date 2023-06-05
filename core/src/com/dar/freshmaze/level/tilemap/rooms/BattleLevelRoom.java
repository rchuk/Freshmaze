package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.entities.Entity;
import com.dar.freshmaze.level.EnemyGenerator;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.level.tilemap.tiles.DynamicEntranceTile;
import com.dar.freshmaze.level.tilemap.tiles.DynamicTile;

public class BattleLevelRoom extends LevelRoom {
    private final Array<Vector2> entrances = new Array<>();
    private final Array<EnemyOld> enemies;
    private final Array<Entity> otherEntities;
    private boolean isCleared = false;

    public BattleLevelRoom(Rectangle bounds, EnemyGenerator enemyGenerator) {
        super(bounds);

        final EnemyGenerator.Result result = enemyGenerator.generate(this);
        enemies = result.enemies;
        otherEntities = result.otherEntities;
    }

    public Array<Vector2> getEntrances() {
        return entrances;
    }

    public void addEntrance(Vector2 entrance) {
        entrances.add(entrance);
    }

    @Override
    public void onDestroy() {
        enemies.forEach(EnemyOld::destroy);
        otherEntities.forEach(Entity::destroy);
    }

    @Override
    public void onPlayerEnter(Bob bob) {
        if (!isCleared)
            setEntrancesState(DynamicEntranceTile.State.Closed);
    }

    public void onEnemyDeath(EnemyOld enemy) {
        enemies.removeValue(enemy, true);

        if (enemies.isEmpty()) {
            setEntrancesState(DynamicEntranceTile.State.Cleared);

            isCleared = true;
        }
    }

    private void setEntrancesState(DynamicEntranceTile.State state) {
        entrances.forEach(entrance -> {
            final DynamicTile dynamicTile = getLevel().getTilemap().getDynamicTile(new LevelTilemap.CellPos((int)entrance.x, (int)entrance.y));
            if (dynamicTile == null)
                return;

            if (dynamicTile instanceof DynamicEntranceTile) {
                final DynamicEntranceTile entranceTile = (DynamicEntranceTile)dynamicTile;

                entranceTile.setState(state);
            }
        });
    }
}
