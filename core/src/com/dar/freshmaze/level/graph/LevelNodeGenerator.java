package com.dar.freshmaze.level.graph;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dar.freshmaze.util.RectangleUtil;

import java.lang.reflect.Array;
import java.util.*;

public class LevelNodeGenerator {
    private Vector2 levelSize;
    private int hallThickness;
    private LevelNodeGenerationRules rules;

    private LevelNode root;
    private ArrayList<LevelNode> leaves;
    private LevelGraph graph;
    private ArrayList<Rectangle> halls;


    public void generate(Vector2 levelSize, int hallThickness, LevelNodeGenerationRules rules) {
        this.levelSize = levelSize;
        this.hallThickness = hallThickness;
        this.rules = rules;

        generateNodes();
        generateGraph();
        generateHalls();
    }

    public Vector2 getLevelSize() {
        return levelSize;
    }

    public LevelNode getRoot() {
        return root;
    }

    public List<LevelNode> getLeaves() {
        return Collections.unmodifiableList(leaves);
    }

    public LevelGraph getGraph() {
        return graph;
    }

    public List<Rectangle> getHalls() {
        return halls;
    }

    private void generateHalls() {
        halls = new ArrayList<>();

        final HashMap<LevelGraph.Edge, Vector2> intersectionPoints = new HashMap<>();

        for (Map.Entry<LevelNode, HashMap<LevelNode, LevelGraph.Edge>> entry : graph.entrySet()) {
            final LevelNode firstNode = entry.getKey();

            for (Map.Entry<LevelNode, LevelGraph.Edge> connection : entry.getValue().entrySet()) {
                final LevelNode secondNode = connection.getKey();
                final LevelGraph.Edge edge = connection.getValue();

                if (!intersectionPoints.containsKey(edge))
                    intersectionPoints.put(edge, getRandomEdgeIntersectionPoint(edge));

                final Vector2 intersectionPoint = intersectionPoints.get(edge);

                halls.addAll(joinRoomsWithHalls(firstNode, secondNode, intersectionPoint));
            }
        }
    }

    private ArrayList<Rectangle> joinRoomsWithHalls(LevelNode firstNode, LevelNode secondNode, Vector2 intersectionPoint) {
        final Rectangle room = firstNode.getRoomBounds();

        return joinPointsWithHalls(
                new Vector2(room.x + (int)(0.5f * room.width), room.y + (int)(0.5f * room.height)),
                intersectionPoint,
                hallThickness
        );
    }

    static ArrayList<Rectangle> joinPointsWithHalls(Vector2 start, Vector2 end, int thickness) {
        final Vector2 diff = end.sub(start);

        final ArrayList<Rectangle> rects = new ArrayList<>();

        // TODO: Fix disjointed angles
        if (MathUtils.randomBoolean()) {
            rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y - 0.5f * thickness, diff.x, thickness)));
            rects.add(RectangleUtil.normalize(new Rectangle(start.x + diff.x - 0.5f * thickness, start.y, thickness, diff.y)));
            //rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y, diff.x, thickness)));
            //rects.add(RectangleUtil.normalize(new Rectangle(start.x + diff.x, start.y, thickness, diff.y)));
        } else {
            rects.add(RectangleUtil.normalize(new Rectangle(start.x - 0.5f * thickness, start.y, thickness, diff.y)));
            rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y + diff.y - 0.5f * thickness, diff.x, thickness)));
            //rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y, thickness, diff.y)));
            //rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y + diff.y, diff.x, thickness)));
        }

        return rects;
    }

    Vector2 getRandomEdgeIntersectionPoint(LevelGraph.Edge edge) {
        final Rectangle rect = edge.getIntersection();

        // TODO: Create some utility function like getIntCenter
        return new Vector2(rect.x + (int)(0.5f * rect.width), rect.y + (int)(0.5f * rect.height));

        // TODO: Implement some better mechanism or leave it as is
        /*
        if (edge.isVertical())
            return new Vector2(rect.x + (int)(0.5f * rect.width), rect.y + MathUtils.random(0, (int)rect.height));
        else
            return new Vector2(rect.x + MathUtils.random(0, rect.width), rect.y + (int)(0.5f * rect.width));
        */
    }

    private void generateGraph() {
        graph = new LevelGraph();
        graph.generate(leaves);
    }

    private void generateNodes() {
        leaves = new ArrayList<>();
        root = new LevelNode(new Rectangle(0.0f, 0.0f, levelSize.x, levelSize.y), rules);

        generateNodeRecursive(root);
    }

    private void generateNodeRecursive(LevelNode node) {
        if (!node.split()) {
            leaves.add(node);
            node.generateRoom();

            return;
        }

        generateNodeRecursive(node.getLeftChild());
        generateNodeRecursive(node.getRightChild());
    }
}
