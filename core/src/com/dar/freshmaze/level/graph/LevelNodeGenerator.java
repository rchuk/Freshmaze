package com.dar.freshmaze.level.graph;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.level.EnemyGenerator;
import com.dar.freshmaze.level.tilemap.SpikeGenerator;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.level.tilemap.rooms.FinalLevelRoom;
import com.dar.freshmaze.level.tilemap.rooms.LevelRoom;
import com.dar.freshmaze.level.tilemap.rooms.SpawnLevelRoom;
import com.dar.freshmaze.util.RectangleUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LevelNodeGenerator {
    private Vector2 levelSize;
    private int hallThickness;
    private LevelNodeGenerationRules rules;

    private LevelNode root;
    private ArrayList<LevelNode> leaves;
    private ArrayList<LevelRoom> rooms;
    private LevelGraph graph;
    private ArrayList<Rectangle> halls;

    private LevelRoom spawnRoom;
    private LevelRoom finalRoom;


    public void generate(Vector2 levelSize, int hallThickness, LevelNodeGenerationRules rules, EnemyGenerator enemyGenerator, SpikeGenerator spikeGenerator) {
        if (rooms != null)
            rooms.forEach(LevelRoom::onDestroy);

        this.levelSize = levelSize;
        this.hallThickness = hallThickness;
        this.rules = rules;

        generateNodes();
        generateGraph();
        generateHalls();
        generateRooms(enemyGenerator, spikeGenerator);
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

    public List<LevelRoom> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public LevelGraph getGraph() {
        return graph;
    }

    public List<Rectangle> getHalls() {
        return halls;
    }

    public LevelRoom getSpawnRoom() {
        return spawnRoom;
    }

    public LevelRoom getFinalRoom() {
        return finalRoom;
    }

    private void generateRooms(EnemyGenerator enemyGenerator, SpikeGenerator spikeGenerator) {
        final ArrayList<Integer> indices = IntStream.range(0, leaves.size())
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(indices);

        if (indices.size() < 2)
            throw new RuntimeException("Can't generate dungeon with less than two rooms");

        spawnRoom = createSpawnRoom(leaves.get(indices.get(0)));
        finalRoom = createFinalRoom(leaves.get(indices.get(1)));

        rooms.add(spawnRoom);
        rooms.add(finalRoom);

        for (int i = 2; i < indices.size(); ++i)
            rooms.add(createBattleRoom(leaves.get(indices.get(i)), enemyGenerator, spikeGenerator));
    }

    private SpawnLevelRoom createSpawnRoom(LevelNode node) {
        return new SpawnLevelRoom(node.getRoomBounds());
    }

    private FinalLevelRoom createFinalRoom(LevelNode node) {
        final Rectangle bounds = node.getRoomBounds();
        final Vector2 teleportPos = new Vector2(bounds.x + (int)(0.5f * bounds.width), bounds.y + (int)(0.5f * bounds.height));;

        return new FinalLevelRoom(bounds, teleportPos);
    }

    private BattleLevelRoom createBattleRoom(LevelNode node, EnemyGenerator enemyGenerator, SpikeGenerator spikeGenerator) {
        final float spikeInterval = MathUtils.random(1.0f, 4.0f);

        final BattleLevelRoom room = new BattleLevelRoom(node.getRoomBounds(), enemyGenerator, spikeInterval);

        room.setSpikes(spikeGenerator.generateSpikes(room));

        return room;
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

        if (MathUtils.randomBoolean()) {
            rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y - 0.5f * thickness, diff.x, thickness)));
            rects.add(RectangleUtil.normalize(new Rectangle(start.x + diff.x - 0.5f * thickness, start.y, thickness, diff.y)));
        } else {
            rects.add(RectangleUtil.normalize(new Rectangle(start.x - 0.5f * thickness, start.y, thickness, diff.y)));
            rects.add(RectangleUtil.normalize(new Rectangle(start.x, start.y + diff.y - 0.5f * thickness, diff.x, thickness)));
        }

        return rects;
    }

    Vector2 getRandomEdgeIntersectionPoint(LevelGraph.Edge edge) {
        final Rectangle rect = edge.getIntersection();

        // TODO: Create some utility function like getIntCenter
        return new Vector2(rect.x + (int)(0.5f * rect.width), rect.y + (int)(0.5f * rect.height));
    }

    private void generateGraph() {
        graph = new LevelGraph();
        graph.generate(leaves);
    }

    private void generateNodes() {
        leaves = new ArrayList<>();
        rooms = new ArrayList<>();
        root = new LevelNode(new Rectangle(0.0f, 0.0f, levelSize.x, levelSize.y), rules);
        generateNodeRecursive(root);
    }

    private void generateNodeRecursive(LevelNode node) {
        if (!node.split()) {
            node.generateRoom();
            leaves.add(node);

            return;
        }

        generateNodeRecursive(node.getLeftChild());
        generateNodeRecursive(node.getRightChild());
    }
}
