package erp.ui;

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
import java.util.stream.Collectors;

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

    public void onCreateOrder(){
        System.out.println("dsa");
    }

//    @FXML
//    private void goBack() {
//
//        MainController.navigateTo("MainView.fxml");
//    }


    @FXML
    private TextField searchField;

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

    @FXML
    private void handleRefresh(){
        ordersTable.setItems(FXCollections.observableArrayList(OrderManager.getInstance().getComenzi()));
    }


    @FXML
    private void handleSterge(){
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selectează o materie primă din tabel.");
            return;
        }
       OrderManager.getInstance().deleteOrder(selected);
        handleRefresh();
    }
    @FXML
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

