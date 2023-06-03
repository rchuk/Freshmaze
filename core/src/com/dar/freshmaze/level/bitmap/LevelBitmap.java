package com.dar.freshmaze.level.bitmap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dar.freshmaze.level.graph.LevelNodeGenerator;
import com.dar.freshmaze.level.tilemap.rooms.BattleLevelRoom;
import com.dar.freshmaze.level.tilemap.rooms.FinalLevelRoom;
import com.dar.freshmaze.util.RectangleUtil;

import java.util.ArrayList;
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

        // debugPrint();
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
            case Teleport:
                return 'T';
        };

        return '@';
    }

    private void placeRooms() {
        generator.getRooms().forEach(room -> {
            processRectangleMap(room.getBounds(), (kind, x, y) -> {
                switch (kind) {
                    case Empty:
                    case Hall:
                    case Wall:
                        return Cell.Kind.Room;
                    default:
                        return kind;
                }
            });

            if (room instanceof FinalLevelRoom) {
                final FinalLevelRoom finalRoom = (FinalLevelRoom)room;
                final Vector2 teleportPos = finalRoom.getTeleportPos();

                getCell((int)teleportPos.x, (int)teleportPos.y).setKind(Cell.Kind.Teleport);
            }
        });

        generator.getRooms().forEach(room ->
                processRectangleMap(RectangleUtil.expand(room.getBounds(), new Vector2(1, 1)), (kind, x, y) -> {
                    switch (kind) {
                        case Empty:
                            return Cell.Kind.Wall;
                        case Hall:
                            if (room instanceof BattleLevelRoom) {
                                final BattleLevelRoom battleRoom = (BattleLevelRoom)room;
                                battleRoom.addEntrance(new Vector2(x, y));

                                return Cell.Kind.HallEntrance;
                            } else {
                                return Cell.Kind.Hall;
                            }
                        default:
                            return kind;
                    }
                })
        );
    }

    private void placeHalls() {
        generator.getHalls().forEach(hall ->
                processRectangleMap(hall, (kind, x, y) -> {
                    switch (kind) {
                        case Empty:
                            return Cell.Kind.Hall;
                        default:
                            return kind;
                    }
                })
        );

        generator.getHalls().forEach(hall ->
                processRectangleMap(RectangleUtil.expand(hall, new Vector2(1, 1)), (kind, x, y) -> {
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
        processRectangle(rect, (cell, x, y) -> cell.setKind(cellKindMapper.processCell(cell.getKind(), x, y)));
    }

    private void processRectangle(Rectangle rect, CellProcessor cellProcessor) {
        for (int yi = 0; yi < rect.height; ++yi) {
            for (int xi = 0; xi < rect.width; ++xi) {
                final int x = (int)rect.x + xi;
                final int y = (int)rect.y + yi;
                final Cell cell = getCell(x, y);

                cellProcessor.processCell(cell, x, y);
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
        Cell.Kind processCell(Cell.Kind kind, int x, int y);
    }

    private interface CellProcessor {
        void processCell(Cell cell, int x, int y);
    }


    public static class Cell {
        public enum Kind {
            Empty,
            Room,
            Hall,
            Wall,
            HallEntrance,
            Teleport
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
