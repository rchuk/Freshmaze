package com.dar.freshmaze.level.tilemap.rooms;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.entities.Bob;
import com.dar.freshmaze.entities.EnemyOld;
import com.dar.freshmaze.entities.Entity;
import com.dar.freshmaze.level.EnemyGenerator;
import com.dar.freshmaze.level.tilemap.LevelTilemap;
import com.dar.freshmaze.level.tilemap.tiles.ChestTile;
import com.dar.freshmaze.level.tilemap.tiles.EntranceTile;
import com.dar.freshmaze.level.tilemap.tiles.DynamicTile;
import com.dar.freshmaze.level.tilemap.tiles.SpikesTile;

public class BattleLevelRoom extends LevelRoom {
    private final Array<Vector2> entrances = new Array<>();
    private Array<Vector2> spikes;
    private final Array<EnemyOld> enemies;
    private final Array<Entity> otherEntities;

    private boolean wasEntered = false;
    private boolean isCleared = false;
    
    private final static float startSpikeInterval = 3.0f;
    private final static float spikeActiveInterval = 1.5f;
    private float spikeInterval;
    private float spikeTimeLeft;
    private boolean spikesActive = false;

    public BattleLevelRoom(Rectangle bounds, EnemyGenerator enemyGenerator, float spikeInterval) {
        super(bounds);

        this.spikeInterval = spikeInterval;
        this.spikeTimeLeft = startSpikeInterval;

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

    public Array<Vector2> getSpikes() { return spikes; }

    public void setSpikes(Array<Vector2> newSpikes) {
        spikes = newSpikes;
    }

    @Override
    public void act(float dt) {
        if (wasEntered && !isCleared) {
            if (spikeTimeLeft < 0) {
                if (spikesActive) {
                    spikeTimeLeft = spikeInterval;
                    spikesActive = false;

                    setSpikesOpen(false);
                } else {
                    spikeTimeLeft = spikeActiveInterval;
                    spikesActive = true;

                    setSpikesOpen(true);
                }
            }

            spikeTimeLeft -= dt;
        }
    }

    @Override
    public void onDestroy() {
        enemies.forEach(EnemyOld::destroy);
        otherEntities.forEach(Entity::destroy);
    }

    @Override
    public void onPlayerEnter(Bob bob) {
        if (!isCleared) {
            wasEntered = true;

            setEntrancesState(EntranceTile.State.Closed);
        }
    }

    public void onEnemyDeath(EnemyOld enemy) {
        enemies.removeValue(enemy, true);

        if (enemies.isEmpty()) {
            isCleared = true;

            onCleared();
        }
    }

    private void onCleared() {
        setEntrancesState(EntranceTile.State.Cleared);

        final LevelTilemap tilemap = getLevel().getTilemap();
        final LevelTilemap.CellPos pos = tilemap.vecToCellPos(getBounds().getCenter(new Vector2()));
        tilemap.placeDynamicTile(new ChestTile(tilemap, pos, tilemap.chestClosedTile, tilemap.chestOpenTile));

        setSpikesOpen(false);
    }

    private void setEntrancesState(EntranceTile.State state) {
        entrances.forEach(pos -> dynamicTileApply(pos, EntranceTile.class, tile -> tile.setState(state)));
    }

    private void setSpikesOpen(boolean isOpen) {
        spikes.forEach(pos -> dynamicTileApply(pos, SpikesTile.class, tile -> tile.setOpen(isOpen)));
    }

    private <T extends DynamicTile> void dynamicTileApply(Vector2 pos, Class<T> type, DynamicTileAction<T> action) {
        final T tile = getDynamicTile(pos, type);
        if (tile == null)
            return;

        action.apply(tile);
    }

    @SuppressWarnings("unchecked")
    private <T extends DynamicTile> T getDynamicTile(Vector2 pos, Class<T> type) {
        final DynamicTile dynamicTile = getLevel().getTilemap().getDynamicTile(new LevelTilemap.CellPos((int)pos.x, (int)pos.y));
        if (dynamicTile == null)
            return null;

        if (type.isInstance(dynamicTile))
            return (T)dynamicTile;

        return null;
    }

    interface DynamicTileAction<T extends DynamicTile> {
        void apply(T tile);
    }
}
