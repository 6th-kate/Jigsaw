package ru.hse.edu.ershestakova.jigsawgame.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * An instance of the Cell class represents a cell of the grid, which is the playing board.
 */
public class Cell extends Rectangle {
    private boolean isFilled;
    private final Canvas grid;
    private final Color fillColor;

    public Cell(Canvas grid, double x, double y, double size, Color fillColor) {
        super(x, y, size, size);
        this.grid = grid;
        isFilled = false;
        this.fillColor = fillColor;
    }

    /**
     * Draws an empty cell on the grid by its coordinates with the default color.
     */
    public void draw() {
        GraphicsContext gc = grid.getGraphicsContext2D();
        gc.setFill(fillColor);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Draws a cell on the grid by its coordinates.
     * @param color The cell fill color.
     */
    public void draw(Color color) {
        GraphicsContext gc = grid.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * @return Returns if the cell already has a tetrominoe tile in it.
     */
    public boolean isFilled() {
        return isFilled;
    }

    /**
     * Defines if the cell already has a tetrominoe tile in it.
     * @param filled new state
     */
    public void setFilled(boolean filled) {
        isFilled = filled;
    }
}
