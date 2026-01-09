package erp.ui;

import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import org.json.JSONObject;
import org.json.JSONArray;

import erp.model.*;
import erp.service.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller pentru fereastra de gestionare a Comenzilor
 * Se ocupa cu afisarea unui tabel cu informatii despre comenzile primite
 */
public class OrdersController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Button createOrderButton;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> colId;

    @FXML
    private TableColumn<Order, String> colClient;

    @FXML
    private TableColumn<Order, String> colProduse;

    @FXML
    private TableColumn<Order, String> colStatus;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
       setupOrdersTable();
    }

    /**
     * Metoda care seteaza tabelul cu comenzi
     */
    private void setupOrdersTable() {
        // Legătura coloanelor cu atributele din clasa Order
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));       // caută getId()
        colClient.setCellValueFactory(new PropertyValueFactory<>("Client"));   // caută getClient()
        colProduse.setCellValueFactory(new PropertyValueFactory<>("Produse"));       // caută getProduse()
        colStatus.setCellValueFactory(cell -> {
            String text = cell.getValue().getProcesata() ? "Finalizată" : "În așteptare";
            return new ReadOnlyStringWrapper(text);
        });

        ObservableList<Order> comenzi = FXCollections.observableArrayList(OrderManager.getInstance().getComenzi());
        ordersTable.setItems(comenzi);
    }

    /**
     * Metoda care se apeleaza cand se apasa butonul 'Creeaza comanda'
     */
    @FXML
    public void handleCreeaza(){
        handleImportAI();
    }

    /**
     * Metoda pentru butonul 'Inapoi'
     */
    @FXML
    private void goBack() {

        MainController.navigateTo("MainView.fxml");
    }


    @FXML
    private TextField searchField;

    /**
     * Metoda pentru cautarea unei comenzi in lista de comenzi
     * Se filtreaza comenzile ce contin un keyword in numele clientului sau id ul comenzii
     */
    @FXML
    private void onSearchOrder() {
        String cuv = searchField.getText().toLowerCase().trim();

        if (cuv.isEmpty()) {
            ordersTable.setItems(
                    FXCollections.observableArrayList(OrderManager.getInstance().getComenzi())
            );
            return;
        }

        List<Order> list;

        //in caz ca se introduce nr in field
        try{
            int idCautat = Integer.parseInt(cuv);
            list = OrderManager.getInstance().getComenzi().stream()
                    .filter(o -> o.getClient().toLowerCase().contains(cuv) || o.getId() == Integer.parseInt(cuv))
                    .toList();

        }catch (NumberFormatException e){
            list =  OrderManager.getInstance().getComenzi().stream()
                    .filter(o -> o.getClient().toLowerCase().contains(cuv))
                    .toList();
        }

       ObservableList<Order> filtrate = FXCollections.observableArrayList(list);

        ordersTable.setItems(filtrate);
        searchField.clear();
    }

    /**
     * Metoda ce se apeleaza la apasarea butonului de Refresh
     */
    @FXML
    private void handleRefresh(){
        ordersTable.setItems(FXCollections.observableArrayList(OrderManager.getInstance().getComenzi()));
    }

    /**
     * Metoda pentru butonul Sterge
     * Sterge o comanda din tabel
     */
    @FXML
    private void handleSterge(){
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selectează o comandă din tabel.");
            return;
        }
       OrderManager.getInstance().deleteOrder(selected);
        handleRefresh();
    }

    /**
     * Metoda pentru afisarea unui mesaj
     * @param msg mesajul propriu-zis
     */
    @FXML
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Metoda care afiseasza un tabel cu produsele care sunt neceseare pentru onorarea unei comenzi
     * @param lipsuri  o lista de OrderLine
     */
    private void tabelProduseLipsa(List<OrderLine> lipsuri){
        TableView<OrderLine> tableLipsuri = new TableView<>();

        TableColumn<OrderLine, String> colProdus = new TableColumn<>("Produs Lipsă");
        colProdus.setPrefWidth(400);
        colProdus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getProdus().toString())

        );

        TableColumn<OrderLine, Integer> colCantitate = new TableColumn<>("Cantitate Lipsă");
        colCantitate.setPrefWidth(200);
        colCantitate.setCellValueFactory(new PropertyValueFactory<>("cantitate"));

        tableLipsuri.getColumns().addAll(colProdus, colCantitate);
        tableLipsuri.setItems(FXCollections.observableArrayList(lipsuri));

        tableLipsuri.setPrefHeight(700);
        tableLipsuri.setPrefWidth(1000);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Stoc Insuficient");
        dialog.setHeaderText("Comanda nu poate fi onorată integral.\nUrmătoarele produse lipsesc:");


        dialog.getDialogPane().setContent(tableLipsuri);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    /**
     * Metoda pentru butonul Verifica Disponibilitate
     * Daca exista toate produsele => afiseaza mesaj
     * Altfel, se afiseaza un tabel cu produsele lipsa
     */
    @FXML
    private void handleDisponibilitate() {

        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selectează o comandă din tabel.");
            return;
        }

        List<OrderLine> lipsuri = OrderManager.getInstance().verificaDisponibilitate(selected);


        // exista stoc
        if (lipsuri.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Disponibilitate");
            alert.setHeaderText("Stoc suficient");
            alert.setContentText("Toate produsele sunt în stoc. Poți procesa comanda!");
            alert.showAndWait();
            return;
        }

        // nu exista stoc, afisare tabel prod lipsa
        tabelProduseLipsa(lipsuri);
    }

    /**
     * Metoda folosita la apasarea butonului de Creeaza Comanda
     * Deschide meniul pentru selectarea fisierului csv/txt
     */
    @FXML
    private void handleImportAI() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Încarcă Comandă");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fișiere Text/CSV", "*.txt", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(ordersTable.getScene().getWindow());

        if (selectedFile != null) {
            try {

                String content = Files.readString(selectedFile.toPath());


                //lista produse disponibile COD - TIP - AROMA (helper pt ai)
                StringBuilder sb = new StringBuilder();
                for(FinishedProduct p : InventoryManager.getInstance().getListProduseFinite()){
                    sb.append(p.getCodProdus()).append(" - ").append(p.getTipProdus()).append(p.getAroma()).append("; ");
                }


                AIService ai = new AIService();
                String jsonResult = ai.extractOrderData(content, sb.toString());

                if (jsonResult != null) {
                    processAIResponse(jsonResult);
                } else {
                    showAlert("Eroare la procesarea AI.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Eroare la citirea fișierului.");
            }
        }
    }

    /**
     * Metoda care proceseaza JSON-ul returnat de AI
     * Se creeaza o comanda folosind JSON ul
     * @param jsonString JSON-ul returnat
     */
    private void processAIResponse(String jsonString) {
        try {

            JSONObject obj = new JSONObject(jsonString);
            String numeClient = obj.getString("client") != null || ! obj.getString("client").equals("") ? obj.getString("client") : "Nume Client Inexistent";
            JSONArray produseArray = obj.getJSONArray("produse");


            Order newOrder = new Order(0, numeClient);

            List<FinishedProduct> catalogProduse = InventoryManager.getInstance().getStocProduse().getCatalogProduse();

            StringBuilder raport = new StringBuilder("AI a identificat:\n");

            for (int i = 0; i < produseArray.length(); i++) {
                JSONObject item = produseArray.getJSONObject(i);
                String codGasit = item.getString("cod");
                int cantitate = item.getInt("cantitate");

                // cautare produs in catalogProduse (toate produsele existente)
                FinishedProduct produsReal = catalogProduse.stream()
                        .filter(p -> p.getCodProdus().equalsIgnoreCase(codGasit))
                        .findFirst()
                        .orElse(null);

                if (produsReal != null) {
                    newOrder.addToOrder(produsReal, cantitate);
                    raport.append("✅ ").append(codGasit).append(" x ").append(cantitate).append("\n");
                } else {
                    raport.append("❌ Produs necunoscut: ").append(codGasit).append("\n");
                }
            }


            OrderManager.getInstance().adaugaComanda(newOrder);
            OrderManager.getInstance().saveOrderToDatabase(newOrder);

            handleRefresh();

            showAlert(raport.toString() + "\nComanda a fost creată cu succes!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Formatul răspunsului AI este invalid.");
        }

    }

    /**
     * Metoda pentru procesarea unei comenzi
     * Daca nu exista suficient stoc => afisare mesaj de eroare
     * Altfel, procesam comanda
     */
    @FXML
    private void handleProceseaza(){
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selectează o comandă din tabel.");
            return;
        }
        List<OrderLine> lipsuri = OrderManager.getInstance().verificaDisponibilitate(selected);
        if(lipsuri.isEmpty() == false){
            showAlert("Comanda NU poate fi procesata! Lipsesc urmatoarele produse:");
            tabelProduseLipsa(lipsuri);
        }else{
            OrderManager.getInstance().proceseazaComanda(selected);
            showAlert("Comanda procesata cu succes!");
            ordersTable.setItems(FXCollections.observableArrayList(OrderManager.getInstance().getComenzi()));
        }
    }
}

