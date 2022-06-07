package ru.hse.edu.ershestakova.jigsawgame.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * An instance of the Tetrominoe class represents a tetrominoe block of several rectangular tiles.
 */
public class Tetrominoe {
    /**
     * Represents different tetraminoe types (shapes)
     */
    public enum TetrominoeType {
        ASYMMETRIC_ANGLE(0, 7, 2, 3),
        Z(8, 11, 2, 3),
        LONG_ANGLE(12, 15, 3, 3),
        T(16, 19, 3, 3),
        LINE(20, 21, 1, 3),
        SQUARE(22, 22, 1, 1),
        SHORT_ANGLE(23, 26, 2, 2),
        SHORT_T(27, 30, 3, 2);

        // Defines the bounds of generation this exact type of tetraminoe
        int startIndex, endIndex;
        // Defines the width and height of a tetraminoe in rectangular tiles
        int width;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        int height;

        TetrominoeType(int startIndex, int endIndex, int width, int height) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.width = width;
            this.height = height;
        }
    }

    // Variables defining the common traits of all tetraminoes
    private static final int TETROMINOE_TYPES_NUM = 8;
    private static final int TETROMINOE_NUM = 31;
    private static Rectangle[][] rectanglesByTetrominoeType;
    private static double rectangleSize;
    private static Color color;
    private static final Random rand = new Random();

    // Variables defining specific traits of a tetraminoe
    TetrominoeType type;
    private final Rectangle[] rectangles;
    private final double width;
    private final double height;

    /**
     * @return Returns a new randomly generated tetrominoe
     */
    public static Tetrominoe randomTetrominoe() {
        return new Tetrominoe(rand.nextInt(TETROMINOE_NUM));
    }

    /**
     * Creates a tetrominoe from a number, choosing a type by the type bounds,
     * and then mirrors or turns it accordingly.
     *
     * @param index the number being converted to a tetrominoe
     * @see TetrominoeType
     */
    public Tetrominoe(int index) {
        int iter = 0;
        TetrominoeType current = TetrominoeType.values()[iter];
        while (index > current.endIndex) {
            current = TetrominoeType.values()[++iter];
        }
        Tetrominoe tetrominoe = new Tetrominoe(current, rectanglesByTetrominoeType[current.ordinal()]);
        if (current.ordinal() < 2 && index - current.startIndex >=
                (TetrominoeType.values()[iter + 1].startIndex - current.startIndex) / 2) {
            tetrominoe = tetrominoe.mirrored();
        } else {
            for (int i = 0; i < index - current.startIndex; ++i) {
                tetrominoe = tetrominoe.turnRight();
            }
        }
        this.type = tetrominoe.type;
        this.height = tetrominoe.height;
        this.width = tetrominoe.width;
        this.rectangles = tetrominoe.rectangles;
    }

    /**
     * Basically copies an existing tetraminoe or a typical one
     *
     * @param type       Tetraminoe type
     * @param rectangles The array of rectangular tiles
     */
    public Tetrominoe(TetrominoeType type, Rectangle[] rectangles) {
        this.type = type;
        this.width = type.width * rectangleSize;
        this.height = type.height * rectangleSize;
        this.rectangles = new Rectangle[rectangles.length];
        int iter = 0;
        for (Rectangle rect :
                rectangles) {
            this.rectangles[iter++] = new Rectangle(rect.getX(), rect.getY(), rectangleSize - 2, rectangleSize - 2);
        }
    }

    /**
     * Creates a tetrominoe from its projection on a 3x3 board
     *
     * @param type       Tetrominoe type
     * @param rectangles the boolean array representation of a 3x3 board
     * @param count      The number of tiles
     */
    public Tetrominoe(TetrominoeType type, boolean[][] rectangles, int count) {
        this.type = type;
        this.rectangles = getRectanglesFromBool(rectangles, count);
        this.width = getWidthFromBool(rectangles, true);
        this.height = getWidthFromBool(rectangles, false);
    }

    /**
     * Gets the coordinates of the first tile in translation to the node containing the tetrominoe
     *
     * @return An (x,y) pair of translated coordinates
     */
    public double[] getFirstBlockTranslation() {
        return new double[]{rectangles[0].getX(), rectangles[0].getY()};
    }

    /**
     * Draws a tetraminoe
     *
     * @param gc graphicsContext on which a tetraminoe will be drawn
     */
    public void draw(GraphicsContext gc) {
        for (Rectangle rectangle :
                rectangles) {
            gc.setFill(color);
            gc.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        }
    }

    /**
     * Gets a tetraminoe projection on a 3x3 board, represented by a boolean array
     *
     * @return A boolean array projection of a tetraminoe
     */
    public boolean[][] getModel() {
        boolean[][] blocks = makeSmallGrid();
        for (Rectangle rectangle :
                rectangles) {
            blocks[(int) (rectangle.getY() / rectangleSize)][(int) (rectangle.getX() / rectangleSize)] = true;
        }
        return blocks;
    }

    /**
     * Gets the coordinates of the first tile in coordinates of the tetraminoe projection on a 3x3 board,
     * which is represented by a boolean array
     *
     * @return An (row,col) pair of translated coordinates
     */
    public int[] getFirstTileModelCoords() {
        return new int[]{(int) (rectangles[0].getY() / rectangleSize), (int) (rectangles[0].getX() / rectangleSize)};
    }

    /**
     * Creates a new tetrominoe from an existing one, spinning it by 90 degrees on the right
     * @return A new turned tetrominoe
     */
    public Tetrominoe turnRight() {
        boolean[][] blocks = makeSmallGrid();
        for (Rectangle rectangle :
                rectangles) {
            blocks[2 - (int) (rectangle.getX() / rectangleSize)][(int) (rectangle.getY() / rectangleSize)] = true;
        }
        return new Tetrominoe(type, restoreBlocks(blocks), rectangles.length);
    }

    /**
     * Creates a new tetrominoe from an existing one, mirroring it vertically
     * @return A new mirrored tetrominoe
     */
    public Tetrominoe mirrored() {
        boolean[][] blocks = makeSmallGrid();
        for (Rectangle rectangle :
                rectangles) {
            blocks[(int) (rectangle.getY() / rectangleSize)][2 - (int) (rectangle.getX() / rectangleSize)] = true;
        }
        return new Tetrominoe(type, restoreBlocks(blocks), rectangles.length);
    }

    /**
     * Gets width or height of a tetrominoe from its 3x3 projection
     * @param blocks Boolean 3x3 array representation of a tetrominoe
     * @param isWidth Declares should width or height be gotten
     * @return Width or height in the container node coordinates
     */
    private static double getWidthFromBool(boolean[][] blocks, boolean isWidth) {
        int width = 0;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (blocks[i][j]) {
                    if (isWidth && j >= width) {
                        ++width;
                    } else if (!isWidth && i >= width) {
                        ++width;
                    }
                }
            }
        }
        return width * rectangleSize;
    }

    /**
     * Gets width of the tetraminoe
     * @return Width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets height of the tetraminoe
     * @return Height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets tetrominoe tiles array from its 3x3 projection
     * @param blocks Boolean 3x3 array representation of a tetrominoe
     * @param count The number of tiles in the tetraminoe;
     * @return Tetrominoe rectangular tiles array
     */
    private static Rectangle[] getRectanglesFromBool(boolean[][] blocks, int count) {
        Rectangle[] rectangles = new Rectangle[count];
        int iter = 0;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (blocks[i][j]) {
                    rectangles[iter++] = new Rectangle(j * rectangleSize, i * rectangleSize,
                            rectangleSize - 2, rectangleSize - 2);
                }
            }
        }
        return rectangles;
    }

    /**
     * Crates a 3x3 grid template
     * @return Full false 3x3 boolean array
     */
    private boolean[][] makeSmallGrid() {
        boolean[][] blocks = new boolean[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                blocks[i][j] = false;
            }
        }
        return blocks;
    }

    /**
     * Moves tetraminoe tiles as up and left as possible on its 3x3 projection
     * @param blocks Boolean 3x3 array representation of a tetrominoe
     * @return Boolean 3x3 array representation of a tetrominoe with up and left moved tiles
     */
    private boolean[][] restoreBlocks(boolean[][] blocks) {
        int highestRow = 2;
        int highestCol = 2;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (blocks[i][j]) {
                    if (i < highestRow) {
                        highestRow = i;
                    }
                    if (j < highestCol) {
                        highestCol = j;
                    }
                }
            }
        }
        if (highestRow != 0) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if (blocks[i][j]) {
                        blocks[i - highestRow][j] = true;
                        blocks[i][j] = false;
                    }
                }
            }
        }
        if (highestCol != 0) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if (blocks[i][j]) {
                        blocks[i][j - highestCol] = true;
                        blocks[i][j] = false;
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Creates arrays of tiles specific for standard tetaminoe types
     * @param cellSize The size of one tetraminoe tile
     * @param paint The color of tetraminoe tiles
     */
    public static void init(double cellSize, Color paint) {
        rectangleSize = cellSize;
        double size = rectangleSize - 2;
        color = paint;
        rectanglesByTetrominoeType = new Rectangle[TETROMINOE_TYPES_NUM][];
        rectanglesByTetrominoeType[TetrominoeType.ASYMMETRIC_ANGLE.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(rectangleSize, 0, size, size),
                        new Rectangle(0, rectangleSize, size, size),
                        new Rectangle(0, rectangleSize * 2, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.Z.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(rectangleSize, rectangleSize, size, size),
                        new Rectangle(0, rectangleSize, size, size),
                        new Rectangle(rectangleSize, rectangleSize * 2, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.LONG_ANGLE.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(0, rectangleSize, size, size),
                        new Rectangle(0, rectangleSize * 2, size, size),
                        new Rectangle(rectangleSize, 0, size, size),
                        new Rectangle(rectangleSize * 2, 0, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.T.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(rectangleSize, 0, size, size),
                        new Rectangle(rectangleSize * 2, 0, size, size),
                        new Rectangle(rectangleSize, rectangleSize, size, size),
                        new Rectangle(rectangleSize, rectangleSize * 2, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.LINE.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(rectangleSize, 0, size, size),
                        new Rectangle(rectangleSize * 2, 0, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.SQUARE.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.SHORT_ANGLE.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(0, rectangleSize, size, size),
                        new Rectangle(rectangleSize, 0, size, size)};
        rectanglesByTetrominoeType[TetrominoeType.SHORT_T.ordinal()] =
                new Rectangle[]{new Rectangle(0, 0, size, size),
                        new Rectangle(rectangleSize, rectangleSize, size, size),
                        new Rectangle(0, rectangleSize, size, size),
                        new Rectangle(0, rectangleSize * 2, size, size)};
        for (int i = 0; i < TETROMINOE_TYPES_NUM; ++i) {
            for (Rectangle rect :
                    rectanglesByTetrominoeType[i]) {
                rect.setFill(color);
            }
        }
    }
}
