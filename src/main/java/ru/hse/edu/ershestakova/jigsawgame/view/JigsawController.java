package ru.hse.edu.ershestakova.jigsawgame.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ru.hse.edu.ershestakova.jigsawgame.viewmodel.JigsawViewModel;
import ru.hse.edu.ershestakova.jigsawgame.model.Cell;
import ru.hse.edu.ershestakova.jigsawgame.model.Tetrominoe;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main game window controller
 */
public class JigsawController implements Initializable {
    // Constants defining specific interface features
    private final double cellSize = 30.0;
    private final double padding = 3.0;
    private final int backgroundLineWidth = 10;
    private final int rowsNum = 9;
    private final int colsNum = 9;
    private final Color backgroundColor = Color.DARKGRAY;
    private final Color primaryCellColor = Color.GRAY;
    private final Color secondaryCellColor = Color.LIGHTGRAY;
    private final Color blocksColor = Color.PEACHPUFF;

    // The corresponding view model
    JigsawViewModel viewModel;

    // The gameboard, a grid
    @FXML
    public Canvas grid;
    private Cell[][] cells;

    // The ticking game timer
    @FXML
    public Label timer;

    // The current tetrominoe and its container
    @FXML
    public Canvas tetrominoe;
    public Tetrominoe currentTetrominoe;

    @FXML
    public Button finishButton;

    /**
     * Initializes the nodes of the main window:
     * Draws the gameboard
     * Initializes tetrominoes generation
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        grid.setHeight(padding * 10 + cellSize * 9);
        grid.setWidth(padding * 10 + cellSize * 9);
        GraphicsContext gc = grid.getGraphicsContext2D();
        drawLines(gc);
        cells = new Cell[rowsNum][colsNum];
        initializeCells();
        Tetrominoe.init(cellSize + padding, blocksColor);
        currentTetrominoe = Tetrominoe.randomTetrominoe();
        tetrominoe.setWidth((cellSize + padding) * 3);
        tetrominoe.setHeight((cellSize + padding) * 3);
        currentTetrominoe.draw(tetrominoe.getGraphicsContext2D());
    }

    /**
     * Defines the action happening when the mouse is pressed on the tetrominoe
     *
     * @param mouseEvent the mouse pressing event
     */
    @FXML
    public void onMousePressed(MouseEvent mouseEvent) {
        viewModel.canvasOnMousePressedEventHandler.handle(mouseEvent);
    }

    /**
     * Defines the action happening when the tetrominoe is dragged through the app
     *
     * @param event the dragging event
     */
    @FXML
    public void onMouseDragged(MouseEvent event) {
        viewModel.canvasOnMouseDraggedEventHandler.handle(event);
    }

    /**
     * Defines the action happening when the mouse button is released while holding the tetrominoe
     *
     * @param event the mouse pressing event
     */
    @FXML
    public void onMouseReleased(MouseEvent event) {
        viewModel.canvasOnMouseReleasedEventHandler.handle(event);
    }

    /**
     * Defines the action happening when the Finish button is pressed
     *
     * @param event the mouse clicking event
     */
    @FXML
    public void onMouseClickedFinish(MouseEvent event) { viewModel.buttonMouseClickedFinishEventHandler.handle(event);}

    /**
     * Binds the timer label to its generator. Starts the timer.
     */
    public void setTimer() {
        timer.textProperty().bind(Bindings.createStringBinding(() -> viewModel.makeStringFromTimer(),
                viewModel.getTimerTask().valueProperty()));
        viewModel.startTimer();
    }

    /**
     * Draws grid separator lines
     *
     * @param gc The grid graphic context
     */
    private void drawLines(GraphicsContext gc) {
        gc.setStroke(backgroundColor);
        double lineWidth = gc.getLineWidth();
        gc.setLineWidth(backgroundLineWidth);
        // Horizontal
        for (int i = 0; i <= rowsNum; ++i) {
            gc.strokeLine(0, padding * i + cellSize * i,
                    padding * (colsNum + 1) + cellSize * colsNum, padding * i + cellSize * i);
        }
        // Vertical
        for (int j = 0; j <= colsNum; ++j) {
            gc.strokeLine(padding * j + cellSize * j, 0,
                    padding * j + cellSize * j, padding * (rowsNum + 1) + cellSize * rowsNum);
        }
        gc.setLineWidth(lineWidth);
    }

    /**
     * Initialises grid cells and draws them on the board
     */
    private void initializeCells() {
        for (int i = 0; i < rowsNum; ++i) {
            for (int j = 0; j < colsNum; ++j) {
                double x = padding * (j + 1) + cellSize * j;
                double y = padding * (i + 1) + cellSize * i;
                if (((i >= rowsNum / 3 && i < 2 * rowsNum / 3) && (j < colsNum / 3 || j >= 2 * colsNum / 3)) ||
                        ((j >= colsNum / 3 && j < 2 * colsNum / 3) && (i < rowsNum / 3 || i >= 2 * rowsNum / 3))) {
                    cells[i][j] = new Cell(grid, x, y, cellSize, primaryCellColor);
                } else {
                    cells[i][j] = new Cell(grid, x, y, cellSize, secondaryCellColor);
                }
                cells[i][j].draw();
            }
        }
    }


    public double getCellSize() {
        return cellSize;
    }

    // Practically gets separator lines width
    public double getPadding() {
        return padding;
    }

    public int getRowsNum() {
        return rowsNum;
    }

    public int getColsNum() {
        return colsNum;
    }

    // Gets color of the tetraminoes
    public Color getBlocksColor() {
        return blocksColor;
    }

    public JigsawViewModel getViewModel() {
        return viewModel;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setViewModel(JigsawViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.setView(this);
    }
}
