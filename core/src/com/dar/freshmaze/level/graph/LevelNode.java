package com.dar.freshmaze.level.graph;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;

public class LevelNode {
    private final LevelNodeGenerationRules rules;

    private final Rectangle bounds;
    private Rectangle roomBounds;
    private LevelNode leftChild;
    private LevelNode rightChild;

    public LevelNode(Rectangle bounds, LevelNodeGenerationRules rules) {
        this.bounds = bounds;
        this.rules = rules;
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getRoomBounds() {
        return roomBounds;
    }

    public LevelNode getLeftChild() {
        return leftChild;
    }

    public LevelNode getRightChild() {
        return rightChild;
    }

    public boolean split() {
        if (leftChild != null || rightChild != null)
            return false;

        if (bounds.width < rules.getMaxNodeSize() && bounds.height < rules.getMaxNodeSize() ) {
            if (MathUtils.random() < rules.getSplitChance())
                return false;
        }


        final boolean isSplitVert = isSplitVertical();

        final float maxSize = isSplitVert ? bounds.height : bounds.width;
        final float maxSplitSize = maxSize - rules.getMinNodeSize();
        if (maxSplitSize <= rules.getMinNodeSize())
            return false;

        // NOTE: Casting to int is used to conform to the grid
        final float splitSize = MathUtils.random(rules.getMinNodeSize(), (int)maxSplitSize);

        if (isSplitVert) {
            leftChild = new LevelNode(new Rectangle(bounds.x, bounds.y, bounds.width, splitSize), rules);
            rightChild = new LevelNode(new Rectangle(bounds.x, bounds.y + splitSize, bounds.width, bounds.height - splitSize), rules);
        } else {
            leftChild = new LevelNode(new Rectangle(bounds.x, bounds.y, splitSize, bounds.height), rules);
            rightChild = new LevelNode(new Rectangle(bounds.x + splitSize, bounds.y, bounds.width - splitSize, bounds.height), rules);
        }

        return true;
    }

    public void generateRoom() {
        if (!isLeaf())
            return;

        final float width = MathUtils.random(rules.getMinRoomSize(), (int)bounds.width - 2);
        final float height = MathUtils.random(rules.getMinRoomSize(), (int)bounds.height - 2);

        final float x = MathUtils.random(1, (int)bounds.width - width - 1);
        final float y = MathUtils.random(1, (int)bounds.height - height - 1);

        roomBounds = new Rectangle(bounds.x + x, bounds.y + y, width, height);
    }

    private boolean isSplitVertical() {
        final float aspectRatio = bounds.getAspectRatio();

        return 1.0f / aspectRatio >= 1.25f ? true :
               aspectRatio >= 1.25f ? false :
               MathUtils.randomBoolean();
    }
}