module ru.hse.edu.ershestakova.jigsawgame {
    requires javafx.controls;
    requires javafx.fxml;

    opens ru.hse.edu.ershestakova.jigsawgame to javafx.fxml;
    opens ru.hse.edu.ershestakova.jigsawgame.model to javafx.fxml;
    opens ru.hse.edu.ershestakova.jigsawgame.view to javafx.fxml;
    opens ru.hse.edu.ershestakova.jigsawgame.viewmodel to javafx.fxml;

    exports ru.hse.edu.ershestakova.jigsawgame;
    exports ru.hse.edu.ershestakova.jigsawgame.model;
    exports ru.hse.edu.ershestakova.jigsawgame.view;
    exports ru.hse.edu.ershestakova.jigsawgame.viewmodel;
}