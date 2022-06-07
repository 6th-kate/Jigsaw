package ru.hse.edu.ershestakova.jigsawgame.viewmodel;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.hse.edu.ershestakova.jigsawgame.JigsawApplication;
import ru.hse.edu.ershestakova.jigsawgame.model.Tetrominoe;
import ru.hse.edu.ershestakova.jigsawgame.view.FinishDialogController;
import ru.hse.edu.ershestakova.jigsawgame.view.JigsawController;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class JigsawViewModel {
    // The number of tetraminoes already put on the grid
    private int tetraminoesCount;
    // The controller of the main window
    private JigsawController view;

    public JigsawViewModel() {
        tetraminoesCount = 0;
    }

    // Auxiliary coordinates for the dragging
    double originalAbsoluteX, originalAbsoluteY;
    double originalTranslationX, originalTranslationY;

    /**
     * The handler of the event of the mouse press on the tetraminoe
     * Saves its initial coordinates
     */
    public EventHandler<MouseEvent> canvasOnMousePressedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            originalAbsoluteX = mouseEvent.getSceneX();
            originalAbsoluteY = mouseEvent.getSceneY();
            originalTranslationX = ((Canvas) (mouseEvent.getSource())).getTranslateX();
            originalTranslationY = ((Canvas) (mouseEvent.getSource())).getTranslateY();
        }
    };

    /**
     * The handler of the event of the mouse drag of the tetraminoe
     * Counts offset values and resets tetraminoe coordinates
     */
    public EventHandler<MouseEvent> canvasOnMouseDraggedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            double offsetX = mouseEvent.getSceneX() - originalAbsoluteX;
            double offsetY = mouseEvent.getSceneY() - originalAbsoluteY;
            double newTranslateX = originalTranslationX + offsetX;
            double newTranslateY = originalTranslationY + offsetY;

            ((Canvas) (mouseEvent.getSource())).setTranslateX(newTranslateX);
            ((Canvas) (mouseEvent.getSource())).setTranslateY(newTranslateY);
        }
    };

    /**
     * The handler of the event of the Finish button click.
     * Opens a new dialog window
     */
    public EventHandler<MouseEvent> buttonMouseClickedFinishEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent actionEvent) {
            // Loads new window markup
            FXMLLoader fxmlLoader = new FXMLLoader(JigsawApplication.class.getResource("finish-dialog.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 300, 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Adds corresponding controllers and the model view
            FinishDialogController controller = fxmlLoader.getController();
            FinishDialogViewModel viewModel =
                    new FinishDialogViewModel(makeStringFromTimer(), getTetraminoesCount(), controller, view);
            controller.setViewModel(viewModel);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }
    };


    /**
     * The handler of the event of the mouse release of the tetraminoe
     * Checks if the tetraminoe can be placed on the board and places it if possible
     */
    public EventHandler<MouseEvent> canvasOnMouseReleasedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            // Absolute coordinates of the grid
            Bounds gridBounds = view.grid.localToScene(view.grid.getBoundsInLocal());
            Bounds enrichedGridBounds = new BoundingBox(gridBounds.getMinX(), gridBounds.getMinY(),
                    gridBounds.getWidth() + view.getCellSize(), gridBounds.getHeight() + view.getCellSize());
            // Absolute coordinates of the tetraminoe canvas and the tetraminoe itself
            Bounds tetrominoeBounds = view.tetrominoe.localToScene(view.tetrominoe.getBoundsInLocal());
            Bounds currentTetrominoeBounds = new BoundingBox(tetrominoeBounds.getMinX(), tetrominoeBounds.getMinY(),
                    view.currentTetrominoe.getWidth(), view.currentTetrominoe.getHeight());
            if (enrichedGridBounds.contains(currentTetrominoeBounds)) {
                // Offset coordinates of the first tile in the tetrominoe in the grid
                double[] currentTetrominoeGridOffset = getFirstTileTranslationToGrid(gridBounds, tetrominoeBounds);
                // Row and column of the cell corresponding to the first tetraminoe tile position
                int[] startingCellCoords = findStartingCellCoords(currentTetrominoeGridOffset);
                if (checkIfPlaceable(startingCellCoords)) {
                    // If the tetraminoe can be placed, places it, makes new and adds to the count
                    redrawCells(startingCellCoords);
                    makeNewTetrominoe();
                    ++tetraminoesCount;
                }
            }
            mouseEvent.consume();
        }
    };

    /**
     * Counts the offset of the grid and the first tile of the tetraminoe coordinates
     *
     * @param gridBounds       Bounds of the grid
     * @param tetrominoeBounds Bounds of the tetraminoe itself
     * @return The offset of the grid and the first tile of the tetraminoe
     */
    private double[] getFirstTileTranslationToGrid(Bounds gridBounds, Bounds tetrominoeBounds) {
        // Gets an offset to the container canvas
        double[] firstTileTranslation = view.currentTetrominoe.getFirstBlockTranslation();
        double[] firstTileCenterAbsolutePosition =
                new double[]{tetrominoeBounds.getMinX() + firstTileTranslation[0],
                        tetrominoeBounds.getMinY() + firstTileTranslation[1]};
        return new double[]{firstTileCenterAbsolutePosition[0] - gridBounds.getMinX(),
                firstTileCenterAbsolutePosition[1] - gridBounds.getMinY()};
    }

    /**
     * Gets row and column of the cell corresponding to the first tetraminoe tile position
     * @param currentTetrominoeGridOffset Offset of the grid and the first tile of the tetraminoe coordinates
     * @return Row and column of the cell corresponding to the first tetraminoe tile position
     */
    private int[] findStartingCellCoords(double[] currentTetrominoeGridOffset) {
        return new int[]{getRowCol(currentTetrominoeGridOffset[1]),
                getRowCol(currentTetrominoeGridOffset[0])};
    }

    /**
     * Gets the row|col of the cell corresponding to the first tetraminoe tile position by Y/X
     * @param currentTetrominoeGridOffset Offset of the grid and the first tile of the tetraminoe coordinates
     * @return the index of the row|col of the cell corresponding to the first tetraminoe tile position
     */
    private int getRowCol(double currentTetrominoeGridOffset) {
        int paddingsNum = 0;
        int colsNum = 0;
        int iter = 0;
        while (currentTetrominoeGridOffset >= paddingsNum * view.getPadding() + colsNum * view.getCellSize()) {
            if (iter % 2 == 0) {
                ++paddingsNum;
            } else {
                ++colsNum;
            }
            ++iter;
        }
        int colIndex = 0;
        if (paddingsNum == colsNum) {
            if (paddingsNum != 0) {
                colIndex = colsNum - 1;
            }
        } else {
            colIndex = colsNum;
        }
        return colIndex;
    }

    /**
     * Gets the starting coordinates of the 3x3 subgrid of the game board grid in which the tetraminoe can be placed
     * @param startingCellCoords Row and column of the cell corresponding to the first tetraminoe tile position
     * @return Gets the starting coordinates of the 3x3 subgrid
     */
    private int[] getSubgridStartCoords(int[] startingCellCoords) {
        int[] firstTileModelCoords = view.currentTetrominoe.getFirstTileModelCoords();

        return new int[]{startingCellCoords[0] - firstTileModelCoords[0],
                startingCellCoords[1] - firstTileModelCoords[1]};
    }

    /**
     * Compares the cells of the 3x3 subgrid to the boolean 3x3 model of the tetrominoe
     * to check if they are filled or not
     * @param subgridStartCoords The starting coordinates of the 3x3 subgrid
     * @param model The boolean 3x3 model of the tetrominoe
     * @return If the tetrominoe can be placed in the subgrid
     */
    private boolean compare(int[] subgridStartCoords, boolean[][] model) {
        boolean allVoid = true;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (model[i][j] && view.getCells()[subgridStartCoords[0] + i][subgridStartCoords[1] + j].isFilled()) {
                    allVoid = false;
                }
            }
        }
        return allVoid;
    }

    /**
     * Checks if the tetrominoe can be placed in the grid on the dragging drop place
     * @param startingCellCoords Row and column of the cell corresponding to the first tetraminoe tile position
     * @return If the tetrominoe can be placed in the grid on the dragging drop place
     */
    private boolean checkIfPlaceable(int[] startingCellCoords) {
        int[] subgridStartCoords = getSubgridStartCoords(startingCellCoords);
        return compare(subgridStartCoords, view.currentTetrominoe.getModel());
    }

    /**
     * Repaints empty grid cells to the color of the tetrominoe;
     * @param subgridStartCoords The starting coordinates of the 3x3 subgrid
     *                           of the game board grid in which the tetraminoe can be placed
     * @param model The boolean 3x3 model of the tetrominoe
     */
    private void redraw(int[] subgridStartCoords, boolean[][] model) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (model[i][j]) {
                    view.getCells()[subgridStartCoords[0] + i][subgridStartCoords[1] + j].setFilled(true);
                    view.getCells()[subgridStartCoords[0] + i][subgridStartCoords[1] + j].draw(view.getBlocksColor());
                }
            }
        }
    }

    /**
     * Repaints empty grid cells to the color of the tetrominoe;
     * @param startingCellCoords Row and column of the cell corresponding to the first tetraminoe tile position
     */
    private void redrawCells(int[] startingCellCoords) {
        int[] subgridStartCoords = getSubgridStartCoords(startingCellCoords);
        redraw(subgridStartCoords, view.currentTetrominoe.getModel());
    }

    /**
     * Generates a new random tetraminoe.
     */
    private void makeNewTetrominoe() {
        view.tetrominoe.setTranslateX(0);
        view.tetrominoe.setTranslateY(0);
        GraphicsContext gc = view.tetrominoe.getGraphicsContext2D();
        gc.clearRect(0, 0, view.tetrominoe.getWidth(), view.tetrominoe.getHeight());
        view.currentTetrominoe = Tetrominoe.randomTetrominoe();
        view.currentTetrominoe.draw(gc);
    }

    /**
     * Resets the game state, practically starts a new game:
     * Clears the grid-game-board cells
     * Sets the zero number of placed tetraminoes
     * Resets the timer
     */
    public void reset() {
        for (int i = 0; i < view.getRowsNum(); ++i) {
            for (int j = 0; j < view.getColsNum(); ++j) {
                view.getCells()[i][j].setFilled(false);
                view.getCells()[i][j].draw();
            }
        }
        tetraminoesCount = 0;
        timerTask.cancel();
        timerTask = new Task<>() {
            {
                updateValue(0);
            }

            @Override
            protected Integer call() throws Exception {
                for (int counter = 0; counter <= 1000; counter++) {
                    sleep(1000);
                    updateValue(counter);
                }
                return 1000;
            }
        };
        view.setTimer();
    }

    /**
     * The time counting task to execute in a background thread
     */
    private Task<Integer> timerTask = new Task<>() {
        {
            updateValue(0);
        }

        @Override
        protected Integer call() throws Exception {
            for (int counter = 0; counter <= 1000; counter++) {
                sleep(1000);
                updateValue(counter);
            }
            return 1000;
        }
    };

    /**
     * Casts timer's value to a user-friendly string
     *
     * @return Timer's value in a string format
     */
    public String makeStringFromTimer() {
        int seconds = getTimerTask().getValue();
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return hours + ":" + (minutes - hours * 60) + ":" + (seconds - minutes * 60);
    }

    /**
     * Starts the timerTask executing
     */
    public void startTimer() {
        Thread timerThread = new Thread(timerTask);
        timerThread.start();
    }

    public Task<Integer> getTimerTask() {
        return timerTask;
    }

    public int getTetraminoesCount() {
        return tetraminoesCount;
    }

    public void setView(JigsawController view) {
        this.view = view;
    }
}
