package erp.ui;

import erp.model.RawMaterial;
import erp.service.InventoryManager;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;
import java.util.Optional;
/**
 * Controller pentru fereastra de gestionare a Materiilor Prime
 * Se ocupa cu afisarea stocului curent si permite adaugarea sau stergerea ingredientelor.
 */
public class RawMController {

    // Legaturile cu elementele vizuale din fisierul .fxml
    @FXML private TableView<RawMaterialWrapper> tableMaterii;
    @FXML private TableColumn<RawMaterialWrapper, String> colNume;
    @FXML private TableColumn<RawMaterialWrapper, Integer> colCantitate;
    @FXML private TableColumn<RawMaterialWrapper, String> colUnitate;

    // Coloanele tabelului
    @FXML private TextField txtNume;
    @FXML private TextField txtCantitate;
    @FXML private TextField txtUnitate;

    @FXML
    private void goHome(){
        MainController.navigateTo("MainView.fxml");
    }

    /**
     * Metoda apelata automat cand se deschide fereastra
     * Aici configuram coloanele sa stie de unde sa isi ia datele
     */
    @FXML
    public void initialize() {
        //fiecare coloana va cauta un anumit getter in RawMaterialWrapper
        colNume.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCantitate.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitate.setCellValueFactory(new PropertyValueFactory<>("unit"));

        // setam datele
        reloadTable();
    }

    /**
     * Metoda care configureaza tabelul
     * Transformam Map-ul cu Materii Prime intr o lista ce contine un singur obiect -> RawMaterialWrapper
     * Populam tabelul cu datele din lista
     */
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

    /**
     * Metoda care se apeleaza la apasarea butonului Adauga
     * Face o verificare daca s-au introdus toate campurile
     * Creeaza un obiect RawMaterial si il adauga in stoc
     * Tabelul este actualizat
     * @throws NumberFormatException
     */
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


    /**
     * Metoda care se apeleaza la apasarea butonului 'Sterge'
     * Verifica daca s-a selectat o linie din tabel
     * In caz afirmativ, sterge acea materie prima din tabel si il actualizeaza
     */
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

    /**
     * Metoda care se apeleaza cand se apasa butonul de modificare
     * Se introduce cantitatea noua si se modifica atat in memorie, dar si in baza de date
     * @throws NumberFormatException
     */
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


    /**
     * Metoda pentru butonul 'Inapoi'
     */
    @FXML
    private void goBack() {
        MainController.goBack();
    }

    /**
     * Metoda care sterge field-urile cu informatiile de la adaugarea unei materii prime
     */
    private void clearFields() {
        txtNume.clear();
        txtCantitate.clear();
        txtUnitate.clear();
    }

    /**
     * Metoda pentru a afisa o alerta
     * @param msg mesajul alertei
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Metoda care genereaza un ID pentru materia prima
     * Folosita la adaugarea unei materii prime in stoc
     * Cauta maximul id-urilor existente(daca exista) si il creste cu 1
     * Daca nu exista => id = 1
     * Este necesara pentru crearea unui obiect RawMaterial si inserarea acestuia in baza de date,
     * deoarece avem nevoie de un id valid
     * @return un intreg, reprezentand urmatorul id (maxID + 1 / 1)
     */
    private int generateId() {
        return InventoryManager.getInstance().getStocMateriePrima().getStoc().keySet()
                .stream().mapToInt(RawMaterial::getId).max().orElse(0) + 1;
    }

    /**
     * Clasa Wrapper pentru obiectele RawMaterial
     * Necesara pentru afisarea in TableView JavaFX
     */
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
