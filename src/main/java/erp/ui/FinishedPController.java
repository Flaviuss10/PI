package erp.ui;

import erp.model.FinishedProduct;
import erp.service.InventoryManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Controller pentru fereastra de gestionare a Produselor Finite
 * Se ocupa cu afisarea listei cu informatiile despre toate produsele finite
 */
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

    @FXML private Label nrProduse;

    private ObservableList<FinishedPWrapper> produseList = FXCollections.observableArrayList();


    /**
     * Metoda pentru gestionarea butonului 'Home'
     */
    @FXML
    private void goHome(){
        MainController.navigateTo("MainView.fxml");
    }

    /**
     * Metoda care reimprospateaza datele din tabel
     * Populeaza tabelul cu produse finite si cantitatea acestora
     */
    private void reloadTabel(){

        // sterge tabelul initial
        produseList.clear();

        Map<FinishedProduct, Integer> map = InventoryManager.getInstance().getStocProdusFinit().getStoc();
        for(Map.Entry<FinishedProduct, Integer> i : map.entrySet())
        {
            FinishedProduct fp = i.getKey();
            Integer qty = i.getValue();
            // folosim FinishedPWrapper, deoarece avem nevoie de un singur obiect
            // pe fiecare rand al tabelului ( JavaFX TableView)
            produseList.add(new FinishedPWrapper(fp, qty));
        }
        tableProduse.setItems(produseList);
        int nrProdExistente = (int) produseList.stream().filter(p -> p.quantity > 0).count();
        nrProduse.setText("Produse existente ce respecta criteriile de filtrare: " + nrProdExistente);
    }

    /**
     * Metoda pentru initializarea FinishedPController
     */
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
    }

    //prima initializare pt choiceboxes

    /**
     * Metoda care initializeaza choice box-urile
     * Folosete stream-uri pentru a crea lista cu toate tipurile, aromele si gramajele existente
     */
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


    /**
     todo filtrare choicebox
     */
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


        // gramaj
        Set<Double> gramaje = produseList.stream().filter(p-> tipSelectat == null || p.getTipProdus().equals(tipSelectat))
                .filter(p -> aromaSelectata == null || p.getAroma().equals(aromaSelectata))
                .map(FinishedProduct::getGramaj).collect(Collectors.toSet());
        choiceGramaj.setItems(FXCollections.observableArrayList(gramaje));
    }

    /**
     * Metoda care filtreaza produsele finite
     * Foloseste stream-uri cu filter pentru a realiza filtrarea produselor
     */
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
        nrProduse.setText("Produse existente ce respecta criteriile de filtrare: " + filtrate.stream().filter(p -> p.getQuantity() > 0).count());
    }

    /**
     * Metoda pentru a reseta filtrele
     */
    @FXML
    private void handleResetFiltre() {
        choiceTipProdus.setValue(null);
        choiceAroma.setValue(null);
        choiceGramaj.setValue(null);
        txtSearch.clear();
        tableProduse.setItems(produseList);
        nrProduse.setText("Produse existente ce respecta criteriile de filtrare: " + produseList.stream().filter(p -> p.getQuantity() > 0).count());
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

    /**
     * Metoda pentru afisarea unei alerte
     * @param mesaj textul alertei
     */
    private void showAlert(String mesaj) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenție");
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    /**
     * Clasa wrapper pentru FinishedProduct
     * Folosita pentru TableView in JavaFX
     * Un rand din tabel trebuie sa fie exact un obiect -> FinishedPWrapper care are atat FinishedProduct dar si cantitatea
     */
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
