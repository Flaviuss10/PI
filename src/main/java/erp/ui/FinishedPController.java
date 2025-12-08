package erp.ui;

import erp.model.FinishedProduct;
import erp.service.InventoryManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;
import java.util.stream.Collectors;

public class FinishedPController {

    @FXML private TableView<FinishedPWrapper> tableProduse;
    @FXML private TableColumn<FinishedPWrapper, String> colCod;
    @FXML private TableColumn<FinishedPWrapper, String> colTip;
    @FXML private TableColumn<FinishedPWrapper, String> colAroma;
    @FXML private TableColumn<FinishedPWrapper, Double> colGramaj;
    @FXML private TableColumn<FinishedPWrapper, String> colUnitate;
    @FXML private TableColumn<FinishedPWrapper, Integer> colCantitate;

    @FXML private ChoiceBox<String> choiceTipProdus;
    @FXML private ChoiceBox<String> choiceAroma;
    @FXML private ChoiceBox<Double> choiceGramaj;
    @FXML private TextField txtSearch;

    private ObservableList<FinishedPWrapper> produseList = FXCollections.observableArrayList();



    @FXML
    private void goHome(){
        MainController.navigateTo("MainView.fxml");
    }
    // incarca datele in tabel
    private void reloadTabel(){

        //evita duplicare
        produseList.clear();


        Map<FinishedProduct, Integer> map = InventoryManager.getInstance().getStocProdusFinit().getStoc();
        for(Map.Entry<FinishedProduct, Integer> i : map.entrySet())
        {
            FinishedProduct fp = i.getKey();
            Integer qty = i.getValue();
            produseList.add(new FinishedPWrapper(fp, qty));
        }
        tableProduse.setItems(produseList);
    }

    @FXML
    public void initialize() {
        // Setare coloane tabel
        colCod.setCellValueFactory(new PropertyValueFactory<>("codProdus"));
        colTip.setCellValueFactory(new PropertyValueFactory<>("tipProdus"));
        colAroma.setCellValueFactory(new PropertyValueFactory<>("aroma"));
        colGramaj.setCellValueFactory(new PropertyValueFactory<>("gramaj"));
        colUnitate.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colCantitate.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        //lista de produse finite + setare tabel
        reloadTabel();

        // prima setare choicebox
        initChoiceBoxes();

//        // Setare choicebox uri in functie de selectii
//        choiceTipProdus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateChoiceBoxes(produseList));
//
//        choiceAroma.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateChoiceBoxes(produseList));

    }

    //prima initializare pt choiceboxes
    private void initChoiceBoxes(){
        Set<String> tip = produseList.stream()
                .map(FinishedPWrapper::getTipProdus)
                .collect(Collectors.toSet());
        choiceTipProdus.setItems(FXCollections.observableArrayList(tip));

        Set<String> arome = produseList.stream()
                .map(FinishedPWrapper::getAroma)
                .collect(Collectors.toSet());
        choiceAroma.setItems(FXCollections.observableArrayList(arome));

        Set<Double> gramaj = produseList.stream()
                .map(FinishedPWrapper::getGramaj)
                .collect(Collectors.toSet());
        choiceGramaj.setItems(FXCollections.observableArrayList(gramaj));

    }

    // setare choicebox uri filtrare
    private void updateChoiceBoxes(ObservableList<FinishedProduct> produseList){
        String tipSelectat = choiceTipProdus.getValue();
        String aromaSelectata = choiceAroma.getValue();

        //tip
        if (choiceTipProdus.getItems().isEmpty()) {
            Set<String> tip = produseList.stream()
                    .map(FinishedProduct::getTipProdus)
                    .collect(Collectors.toSet());
            choiceTipProdus.setItems(FXCollections.observableArrayList(tip));
        }


        // aroma
        Set<String> arome = produseList.stream().
                filter(p-> tipSelectat == null || p.getTipProdus().equals(tipSelectat)).
                map(FinishedProduct::getAroma).collect(Collectors.toSet());
        choiceAroma.setItems(FXCollections.observableArrayList(arome));

//        if (aromaSelectata != null && arome.contains(aromaSelectata)) {
//            choiceAroma.setValue(aromaSelectata);
//        } else {
//            choiceAroma.setValue(null); // sau lasă necompletat
//        }


        // gramaj
        Set<Double> gramaje = produseList.stream().filter(p-> tipSelectat == null || p.getTipProdus().equals(tipSelectat))
                .filter(p -> aromaSelectata == null || p.getAroma().equals(aromaSelectata))
                .map(FinishedProduct::getGramaj).collect(Collectors.toSet());
        choiceGramaj.setItems(FXCollections.observableArrayList(gramaje));

    }
    @FXML
    private void handleFiltrare() {
        String selectedTip = choiceTipProdus.getValue();
        String selectedAroma = choiceAroma.getValue();
        Double selectedGramaj = choiceGramaj.getValue();
        String searchText = txtSearch.getText().toLowerCase();

        List<FinishedPWrapper> filtrate = produseList.stream()
                .filter(p -> (selectedTip == null || p.getTipProdus().equals(selectedTip)) &&
                        (selectedAroma == null || p.getAroma().equals(selectedAroma)) &&
                        (selectedGramaj == null || p.getGramaj() == selectedGramaj) &&
                        (searchText.isEmpty() || p.getCodProdus().toLowerCase().contains(searchText) ||
                                p.getTipProdus().toLowerCase().contains(searchText) ||
                                p.getAroma().toLowerCase().contains(searchText))
                )
                .collect(Collectors.toList());

        tableProduse.setItems(FXCollections.observableArrayList(filtrate));
    }

    @FXML
    private void handleResetFiltre() {
        choiceTipProdus.setValue(null);
        choiceAroma.setValue(null);
        choiceGramaj.setValue(null);
        txtSearch.clear();
        tableProduse.setItems(produseList);
    }

    @FXML
    private void handleReload() {
        reloadTabel();
        handleResetFiltre();
    }


    @FXML
    private void goBack() {
        MainController.goBack();
    }

    private void showAlert(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenție");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    public static class FinishedPWrapper{
        private final FinishedProduct fp;
        private final Integer quantity;

        public FinishedPWrapper(FinishedProduct fp, Integer quantity) {
            this.fp = fp;
            this.quantity = quantity;
        }

        public FinishedProduct getFp() {
            return fp;
        }

        public String getCodProdus(){
            return fp.getCodProdus();
        }

        public String getTipProdus(){
            return fp.getTipProdus();
        }

        public String getAroma(){
            return fp.getAroma();
        }

        public double getGramaj(){
            return fp.getGramaj();
        }

        public String getUnit(){
            return fp.getUnit();
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}
