package com.dar.freshmaze.level.tilemap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.level.tilemap.rooms.LevelRoom;

public class SpikeGenerator {
    // TODO: Add more patterns?
    public Array<Vector2> generateSpikes(LevelRoom room) {
        final Rectangle bounds = room.getBounds();

        final Array<Vector2> spikes = new Array<>();

        final float random = MathUtils.random();

        if (random < 0.4f) {
            return spikes;
        } else if (random < 0.65f) {
            generateSpikesVertical(bounds, spikes);
        } else if (random < 0.9f) {
            generateSpikesHorizontal(bounds, spikes);
        } else {
            generateSpikesCheckerboard(bounds, spikes);
        }

        return spikes;
    }

    private void generateSpikesVertical(Rectangle rect, Array<Vector2> spikes) {
        for (int yi = (int)rect.y; yi < rect.y + rect.height; ++yi) {
            for (int xi = (int)rect.x; xi < rect.x + rect.width; ++xi) {
                if (yi % 3 == 0)
                    spikes.add(new Vector2(xi, yi));
            }
        }
    }

    private void generateSpikesHorizontal(Rectangle rect, Array<Vector2> spikes) {
        for (int yi = (int)rect.y; yi < rect.y + rect.height; ++yi) {
            for (int xi = (int)rect.x; xi < rect.x + rect.width; ++xi) {
                if (xi % 3 == 0)
                    spikes.add(new Vector2(xi, yi));
            }
        }
    }

    private void generateSpikesCheckerboard(Rectangle rect, Array<Vector2> spikes) {
        for (int yi = (int)rect.y; yi < rect.y + rect.height; ++yi) {
            for (int xi = (int)rect.x; xi < rect.x + rect.width; ++xi) {
                if ((xi + yi) % 2 == 0)
                    spikes.add(new Vector2(xi, yi));
            }
        }
    }
}
