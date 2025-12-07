package erp.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class StocksController {

    @FXML
    private ImageView imgMateriiPrime;

    @FXML
    private ImageView imgProduseFinite;

    @FXML
    private Button btnMateriiPrime;

    @FXML
    private Button btnProduseFinite;

    @FXML
    public void initialize() {
        Image img1 = new Image(getClass().getResource("/img/raw.png").toExternalForm());
        imgMateriiPrime.setImage(img1);

        Image img2 = new Image(getClass().getResource("/img/finish.png").toExternalForm());
        imgProduseFinite.setImage(img2);
    }

    @FXML
    private void handleMateriiPrime(ActionEvent event) {
        MainController.showMenuFromOutside();
        MainController.navigateTo("MateriiPrime.fxml");
      //  loadScene("/erp/ui/MateriiPrime.fxml", event);
    }

    @FXML
    private void handleProduseFinite(ActionEvent event) {
        MainController.navigateTo("ProdusFinit.fxml");
    }


    @FXML
    private void handleGoBack(ActionEvent event) {
        loadScene("/erp/ui/MainView.fxml", event);
    }

    private void loadScene(String fxmlPath, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
