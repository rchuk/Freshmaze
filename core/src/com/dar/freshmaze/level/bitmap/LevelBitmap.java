package com.dar.freshmaze.level.bitmap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.dar.freshmaze.level.graph.LevelNode;
import com.dar.freshmaze.level.graph.LevelNodeGenerator;
import com.dar.freshmaze.util.RectangleUtil;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelBitmap {
    private int width;
    private int height;

    private ArrayList<Cell> cells;

    private LevelNodeGenerator generator;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void generate(LevelNodeGenerator generator) {
        final Vector2 levelSize = generator.getLevelSize();

        this.generator = generator;

        width = (int)levelSize.x;
        height = (int)levelSize.y;

        cells = Stream
                .generate(() -> new Cell(Cell.Kind.Empty))
                .limit((long)width * height)
                .collect(Collectors.toCollection(ArrayList::new));

        placeHalls();
        placeRooms();

        //debugPrint();
    }

    // TODO: Remove
    private void debugPrint() {
        System.out.println("Width: " + width + " height: " + height);
        for (int yi = height - 1; yi >= 0; --yi) {
            StringBuilder line = new StringBuilder();
            for (int xi = 0; xi < height; ++xi) {
                line.append(getDebugCellChar(getCell(xi, yi)));
            }
            System.out.println(line);
        }
    }

    // TODO: Remove when it's no longer needed
    private char getDebugCellChar(Cell cell) {
        switch (cell.kind) {
            case HallEntrance:
                return 'E';
            case Hall:
                return 'H';
            case Wall:
                return 'W';
            case Empty:
                return '_';
            case Room:
                return 'R';
        };

        return '@';
    }

    private void placeRooms() {
        generator.getLeaves().forEach(leaf ->
                processRectangleMap(leaf.getRoomBounds(), kind -> {
                    switch (kind) {
                        case Empty:
                        case Hall:
                        case Wall:
                            return Cell.Kind.Room;
                        default:
                            return kind;
                    }
                })
        );

        generator.getLeaves().forEach(leaf ->
                processRectangleMap(RectangleUtil.expand(leaf.getRoomBounds(), new Vector2(1, 1)), kind -> {
                    switch (kind) {
                        case Empty:
                            return Cell.Kind.Wall;
                        case Hall:
                            return Cell.Kind.HallEntrance;
                        default:
                            return kind;
                    }
                })
        );
    }

    private void placeHalls() {
        generator.getHalls().forEach(hall ->
                processRectangleMap(hall, kind -> {
                    switch (kind) {
                        case Empty:
                            return Cell.Kind.Hall;
                        default:
                            return kind;
                    }
                })
        );

        generator.getHalls().forEach(hall ->
                processRectangleMap(RectangleUtil.expand(hall, new Vector2(1, 1)), kind -> {
                    switch (kind) {
                        case Empty:
                            return Cell.Kind.Wall;
                        default:
                            return kind;
                    }
                })
        );
    }

    private void processRectangleMap(Rectangle rect, CellKindMapper cellKindMapper) {
        processRectangle(rect, cell -> cell.setKind(cellKindMapper.processCell(cell.getKind())));
    }

    private void processRectangle(Rectangle rect, CellProcessor cellProcessor) {
        for (int yi = 0; yi < rect.height; ++yi) {
            for (int xi = 0; xi < rect.width; ++xi) {
                final Cell cell = getCell((int)rect.x + xi, (int)rect.y + yi);

                cellProcessor.processCell(cell);
            }
        }
    }

    public Cell getCell(int xi, int yi) {
        return getCell(xi + yi * width);
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    private interface CellKindMapper {
        Cell.Kind processCell(Cell.Kind kind);
    }

    private interface CellProcessor {
        void processCell(Cell cell);
    }


    public static class Cell {
        public enum Kind {
            Empty,
            Room,
            Hall,
            Wall,
            HallEntrance,
        }

        private Kind kind;

        public Cell(Kind kind) {
            this.kind = kind;
        }

        public Kind getKind() {
            return kind;
        }

        public void setKind(Kind newKind) {
            kind = newKind;
        }
    }
}
