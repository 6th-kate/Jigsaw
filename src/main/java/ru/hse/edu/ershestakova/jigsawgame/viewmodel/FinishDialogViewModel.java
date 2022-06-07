package ru.hse.edu.ershestakova.jigsawgame.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ru.hse.edu.ershestakova.jigsawgame.view.FinishDialogController;
import ru.hse.edu.ershestakova.jigsawgame.view.JigsawController;

/**
 * The View model corresponding to the FinishDialog window
 */
public class FinishDialogViewModel {
    // Constant patterns of labels
    private final String timePattern = "You played for ";
    private final String turnsPattern = "Number of turns: ";

    // The labels' bindings
    private StringProperty timing;
    private StringProperty turns;

    // The game windows controllers
    private FinishDialogController view;
    private JigsawController otherView;

    /**
     * Creates an instance of view model
     * @param timerStr The amount of the time passed
     * @param tetraminoesCount The number of tetraminoes dropped
     * @param view The controller of the FinishDialog window
     * @param otherView The controller of the main window
     */
    public FinishDialogViewModel(String timerStr, int tetraminoesCount,
                                 FinishDialogController view, JigsawController otherView) {
        timing = new SimpleStringProperty(timePattern + timerStr);
        turns = new SimpleStringProperty(turnsPattern + tetraminoesCount);
        this.view = view;
        this.otherView = otherView;
    }

    /**
     * @return The timer label binding
     */
    public StringProperty timingProperty() {
        return timing;
    }

    /**
     * @return The turns label binding
     */
    public StringProperty turnsProperty() {
        return turns;
    }

    /**
     * The handler of the event of the Reset button click.
     * Resets the condition of the main window, closes itself
     * Practically starts a new game
     */
    public EventHandler<MouseEvent> buttonMouseClickedResetEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent actionEvent) {
            otherView.getViewModel().reset();
            Stage stage = (Stage) view.turns.getScene().getWindow();
            stage.close();
        }
    };

    /**
     * The handler of the event of the Exit button click.
     * Ends the gaming process, closes all windows.S
     */
    public EventHandler<MouseEvent> buttonMouseClickedExitEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent actionEvent) {
            Platform.exit();
            System.exit(0);
        }
    };
}
