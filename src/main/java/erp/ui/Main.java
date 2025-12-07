package erp.ui;

import erp.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        DatabaseManager.connect();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/erp/ui/MainView.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("ERP System");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setResizable(true); // acest lucru permite redimensionarea

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
