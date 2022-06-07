package ru.hse.edu.ershestakova.jigsawgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.hse.edu.ershestakova.jigsawgame.view.JigsawController;
import ru.hse.edu.ershestakova.jigsawgame.viewmodel.JigsawViewModel;

import java.io.IOException;

/**
 * The main application class
 */
public class JigsawApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JigsawApplication.class.getResource("jigsaw-view.fxml"));
        Parent root = fxmlLoader.load();
        // Sets specific controller to the window and links it with the view model
        JigsawController controller = fxmlLoader.getController();
        JigsawViewModel viewModel = new JigsawViewModel();
        controller.setViewModel(viewModel);
        Scene scene = new Scene(root, 520, 340);
        stage.setTitle("Jigsaw");
        stage.setScene(scene);
        controller.setTimer();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Platform.exit();
                System.exit(0);
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}