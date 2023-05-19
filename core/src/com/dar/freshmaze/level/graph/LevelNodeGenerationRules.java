package com.dar.freshmaze.level.graph;

public class LevelNodeGenerationRules {
    private final int minRoomSize;

    private final int minNodeSize;
    private final int maxNodeSize;
    private final float splitChance;

    public LevelNodeGenerationRules(int minRoomSize, int minNodeSize, int maxNodeSize, float splitChance) {
        this.minRoomSize = minRoomSize;
        this.minNodeSize = minNodeSize;
        this.maxNodeSize = maxNodeSize;
        this.splitChance = splitChance;
    }

    public int getMinRoomSize() {
        return minRoomSize;
    }

    public int getMinNodeSize() {
        return minNodeSize;
    }

    public int getMaxNodeSize() {
        return maxNodeSize;
    }

    public float getSplitChance() {
        return splitChance;
    }
}
