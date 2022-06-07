package ru.hse.edu.ershestakova.jigsawgame.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ru.hse.edu.ershestakova.jigsawgame.viewmodel.FinishDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A controller controlling the FinishDialog window
 */
public class FinishDialogController implements Initializable {

    @FXML
    public Label turns;

    @FXML
    public Label time;

    // The view model (demonstration logic)
    private FinishDialogViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    /**
     * Sets the view model corresponding to the view and binds labels to its variables.
     * @param viewModel The view model
     */
    public void setViewModel(FinishDialogViewModel viewModel) {
        this.viewModel = viewModel;
        turns.textProperty().bind(this.viewModel.turnsProperty());
        time.textProperty().bind(this.viewModel.timingProperty());
    }

    /**
     * Defines the action happening when the Reset button is clicked
     * @param event the clicking event
     */
    @FXML
    public void onMouseClickedReset(MouseEvent event) {
        viewModel.buttonMouseClickedResetEventHandler.handle(event);
    }

    /**
     * Defines the action happening when the Exit button is clicked
     * @param event the clicking event
     */
    @FXML
    public void onMouseClickedExit(MouseEvent event) {
        viewModel.buttonMouseClickedExitEventHandler.handle(event);
    }
}
