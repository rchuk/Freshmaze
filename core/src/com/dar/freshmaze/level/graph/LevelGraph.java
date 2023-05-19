package com.dar.freshmaze.level.graph;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dar.freshmaze.common.Graph;
import com.dar.freshmaze.util.RectangleUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LevelGraph {
    private static final float EXPAND_DELTA = 1.0f;
    private static final Vector2 EXPAND_HORIZONTAL = new Vector2(EXPAND_DELTA, 0.0f);
    private static final Vector2 EXPAND_VERTICAL = new Vector2(0.0f, EXPAND_DELTA);

    private Graph<LevelNode, Edge> graph;

    public Set<Map.Entry<LevelNode, HashMap<LevelNode, Edge>>> entrySet() {
        return graph.entrySet();
    }

    public void generate(List<LevelNode> leaves) {
        graph = new Graph<>();

        for (LevelNode firstNode : leaves) {
            for (LevelNode secondNode : leaves) {
                if (firstNode.equals(secondNode))
                    continue;

                final Edge edge = findIntersection(firstNode, secondNode);
                if (edge == null)
                    continue;

                graph.add(firstNode, secondNode, edge);
            }
        }

        // TODO: Create random breaks
    }

    private Edge findIntersection(LevelNode firstNode, LevelNode secondNode) {
        final Rectangle intersection = new Rectangle();

        final boolean isVertical;
        if (intersectExpandedNodes(firstNode, secondNode, EXPAND_VERTICAL, intersection)) {
            isVertical = true;
        } else if (intersectExpandedNodes(firstNode, secondNode, EXPAND_HORIZONTAL, intersection)) {
            isVertical = false;
        } else {
            return null;
        }

        return new Edge(intersection, isVertical);
    }

    private static boolean intersectExpandedNodes(LevelNode firstNode, LevelNode secondNode, Vector2 delta, Rectangle intersection) {
        return Intersector.intersectRectangles(
                RectangleUtil.expand(firstNode.getBounds(), delta),
                RectangleUtil.expand(secondNode.getBounds(), delta),
                intersection
        );
    }

    public static class Edge {
        private final Rectangle intersection;
        private final boolean isVertical;

        public Edge(Rectangle intersection, boolean isVertical) {
            this.intersection = intersection;
            this.isVertical = isVertical;
        }

        public Rectangle getIntersection() {
            return intersection;
        }

        public boolean isVertical() {
            return isVertical;
        }
    }
}
