package com.dar.freshmaze.level.graph;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelNodeGenerator {
    private Vector2 levelSize;
    private LevelNodeGenerationRules rules;

    private LevelNode root;
    private ArrayList<LevelNode> leaves;
    private LevelGraph graph;


    public void generate(Vector2 levelSize, LevelNodeGenerationRules rules) {
        this.levelSize = levelSize;
        this.rules = rules;

        generateNodes();
        generateGraph();

        // TODO
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
