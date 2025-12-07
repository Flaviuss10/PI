    package erp.ui;
    import erp.model.Order;
    import erp.service.OrderManager;
    import javafx.beans.property.ReadOnlyStringWrapper;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.scene.control.cell.PropertyValueFactory;

    import java.time.LocalDate;

    public class DashboardController {

        // Carduri
        @FXML
        private Label labelTotalOrders;
        @FXML
        private Label labelPendingOrders;
        @FXML
        private Label labelFinishedOrderPercentage;
        @FXML
        private Label labelCriticalStocks;
        @FXML
        private ProgressBar finishedOrderProgressBar;

        // Tabel ultimele comenzi
        @FXML
        private TableView<Order> tableRecentOrders;
        @FXML
        private TableColumn<Order, Integer> colOrderId;
        @FXML
        private TableColumn<Order, String> colClient;
        @FXML
        private TableColumn<Order, LocalDate> colProduse;
        @FXML
        private TableColumn<Order, String> colStatus;

        @FXML
        public void initialize() {
            loadStatistics();
            setupRecentOrdersTable();
        }

        private void loadStatistics() {


            int totalOrders = OrderManager.getInstance().countOrders();
            int pendingOrders = (int) OrderManager.getInstance().countComenziNefinalizate();
            double comenziFinalizate = (double) (totalOrders - pendingOrders) / totalOrders;
            int criticalStockItems = 5;

            labelTotalOrders.setText(String.valueOf(totalOrders));
            labelPendingOrders.setText(String.valueOf(pendingOrders));

            finishedOrderProgressBar.setProgress(comenziFinalizate);
            labelFinishedOrderPercentage.setText((int) (comenziFinalizate * 100) + "%");

            labelCriticalStocks.setText(criticalStockItems + " produse");
        }

        private void setupRecentOrdersTable() {
            // Legătura coloanelor cu atributele din clasa Order
            colOrderId.setCellValueFactory(new PropertyValueFactory<>("Id"));       // caută getId()
            colClient.setCellValueFactory(new PropertyValueFactory<>("Client"));   // caută getClient()
            colProduse.setCellValueFactory(new PropertyValueFactory<>("Produse"));       // caută getData()
            colStatus.setCellValueFactory(cell -> {
                String text = cell.getValue().getProcesata() ? "Finalizată" : "În așteptare";
                return new ReadOnlyStringWrapper(text);
            });


            // Date de test
    //        ObservableList<Order> orders = FXCollections.observableArrayList(
    //                new Order(101, "SC Alimentara SRL", LocalDate.now().minusDays(1), "Finalizată"),
    //                new Order(102, "Distrib SRL", LocalDate.now().minusDays(2), "În așteptare"),
    //                new Order(103, "MegaFood SRL", LocalDate.now().minusDays(3), "Anulată"),
    //                new Order(104, "BioFresh", LocalDate.now().minusDays(4), "Finalizată")
    //        );
            ObservableList<Order> comenzi = FXCollections.observableArrayList(OrderManager.getInstance().getComenzi());
            tableRecentOrders.setItems(comenzi);

        }
    }

