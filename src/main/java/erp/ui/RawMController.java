package erp.ui;

import erp.model.RawMaterial;
import erp.service.InventoryManager;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;
import java.util.Optional;

public class RawMController {

    @FXML private TableView<RawMaterialWrapper> tableMaterii;
    @FXML private TableColumn<RawMaterialWrapper, String> colNume;
    @FXML private TableColumn<RawMaterialWrapper, Integer> colCantitate;
    @FXML private TableColumn<RawMaterialWrapper, String> colUnitate;

    @FXML private TextField txtNume;
    @FXML private TextField txtCantitate;
    @FXML private TextField txtUnitate;

    @FXML
    public void initialize() {
        colNume.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCantitate.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitate.setCellValueFactory(new PropertyValueFactory<>("unit"));

        reloadTable();
    }

    @FXML
    private void reloadTable() {
        ObservableList<RawMaterialWrapper> data = FXCollections.observableArrayList();
        for (Map.Entry<RawMaterial, Double> entry : InventoryManager.getInstance().getStocMateriePrima().getStoc().entrySet()) {
            RawMaterial rm = entry.getKey();
            double qty = entry.getValue();
            data.add(new RawMaterialWrapper(rm, qty));
        }
        tableMaterii.setItems(data);
    }

    @FXML
    private void handleAdauga() {
        String nume = txtNume.getText();
        String cant = txtCantitate.getText();
        String unit = txtUnitate.getText();

        if (nume.isEmpty() || cant.isEmpty() || unit.isEmpty()) {
            showAlert("Toate câmpurile sunt obligatorii.");
            return;
        }

        try {
            int cantitate = Integer.parseInt(cant);
            RawMaterial mat = new RawMaterial(generateId(), nume, unit);
            InventoryManager.getInstance().adaugaMateriePrima(mat,cantitate);
            reloadTable();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Cantitatea trebuie să fie un număr întreg.");
        }
    }

    @FXML
    private void handleSterge() {
        RawMaterialWrapper selected = tableMaterii.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selectează o materie primă din tabel.");
            return;
        }
        InventoryManager.getInstance().getStocMateriePrima().deleteMaterial(selected.getMaterial());
        reloadTable();
    }

    @FXML
    private void handleActualizeaza() {
        RawMaterialWrapper selected = tableMaterii.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selectează o materie primă pentru actualizare.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Actualizează Cantitatea");
        dialog.setHeaderText("Introduceți noua cantitate pentru: " + selected.getName());
        dialog.setContentText("Cantitate:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                double newQty = Double.parseDouble(input);
                if (newQty < 0) {
                    showAlert("Cantitatea nu poate fi negativă.");
                    return;
                }

                InventoryManager.getInstance().getStocMateriePrima().updateMaterial(selected.getMaterial(), newQty);
                reloadTable();

            } catch (NumberFormatException e) {
                showAlert("Introduceți o valoare numerică validă.");
            }
        });
    }


    @FXML
    private void goBack() {
        MainController.goBack();
    }

    private void clearFields() {
        txtNume.clear();
        txtCantitate.clear();
        txtUnitate.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private int generateId() {
        return InventoryManager.getInstance().getStocMateriePrima().getStoc().keySet()
                .stream().mapToInt(RawMaterial::getId).max().orElse(0) + 1;
    }

    //wrapper folosit pt afisare in tabel
    public static class RawMaterialWrapper {
        private final RawMaterial material;
        private final double quantity;

        public RawMaterialWrapper(RawMaterial material, double quantity) {
            this.material = material;
            this.quantity = quantity;
        }

        public String getName() { return material.getName(); }
        public double getQuantity() { return quantity; }
        public String getUnit() { return material.getUnit(); }
        public RawMaterial getMaterial() { return material; }
    }
}
