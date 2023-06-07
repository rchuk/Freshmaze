package com.dar.freshmaze.level.graph;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class LevelNode {
    private final LevelNodeGenerationRules rules;
    private final Rectangle bounds;
    private LevelNode leftChild;
    private LevelNode rightChild;
    private Rectangle roomBounds;

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

    public LevelNode getLeftChild() {
        return leftChild;
    }

    public LevelNode getRightChild() {
        return rightChild;
    }

    public Rectangle getRoomBounds() {
        return roomBounds;
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

        final float width = MathUtils.random(rules.getMinRoomSize(), (int)bounds.width - 2 * rules.getRoomGap());
        final float height = MathUtils.random(rules.getMinRoomSize(), (int)bounds.height - 2 * rules.getRoomGap());

        final float x = MathUtils.random(rules.getRoomGap(), (int)(bounds.width - width - rules.getRoomGap()));
        final float y = MathUtils.random(rules.getRoomGap(), (int)(bounds.height - height - rules.getRoomGap()));

        roomBounds = new Rectangle(bounds.x + x, bounds.y + y, width, height);
    }

    private boolean isSplitVertical() {
        final float aspectRatio = bounds.getAspectRatio();

        return 1.0f / aspectRatio >= 1.25f ? true :
               aspectRatio >= 1.25f ? false :
               MathUtils.randomBoolean();
    }
}
