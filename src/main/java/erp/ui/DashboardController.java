    package erp.ui;
    import erp.model.Order;
    import erp.service.InventoryManager;
    import erp.service.OrderManager;
    import javafx.beans.property.ReadOnlyStringWrapper;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.scene.control.cell.PropertyValueFactory;

    import java.time.LocalDate;
    /**
     * Controller pentru fereastra de gestionare a Dashboard-ului
     * Se ocupa cu afisarea unor statistici si a comenzilor recente
     */
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

        /**
         * Metoda pentru initializarea ui-ului
         */
        @FXML
        public void initialize() {
            loadStatistics();
            setupRecentOrdersTable();
        }

        /**
         * Metoda pentru incarcarea statisticilor din Dashboard
         * Se afiseaza numarul total de comenzi, comenzi in asteptare, stocuri critice
         */
        private void loadStatistics() {
            int totalOrders = OrderManager.getInstance().countOrders();
            int pendingOrders = (int) OrderManager.getInstance().countComenziNefinalizate();
            double comenziFinalizate = (double) (totalOrders - pendingOrders) / totalOrders;
            int criticalStockItems = InventoryManager.getInstance().stocCritic();

            labelTotalOrders.setText(String.valueOf(totalOrders));
            labelPendingOrders.setText(String.valueOf(pendingOrders));

            finishedOrderProgressBar.setProgress(comenziFinalizate);
            labelFinishedOrderPercentage.setText((int) (comenziFinalizate * 100) + "%");

            labelCriticalStocks.setText(criticalStockItems + " produse");
        }

        /**
         * Metoda pentru crearea tabelului cu comenzi recente
         */
        private void setupRecentOrdersTable() {
            // Legătura coloanelor cu atributele din clasa Order
            colOrderId.setCellValueFactory(new PropertyValueFactory<>("Id"));       // caută getId()
            colClient.setCellValueFactory(new PropertyValueFactory<>("Client"));   // caută getClient()
            colProduse.setCellValueFactory(new PropertyValueFactory<>("Produse"));       // caută getData()
            colStatus.setCellValueFactory(cell -> {
                String text = cell.getValue().getProcesata() ? "Finalizată" : "În așteptare";
                return new ReadOnlyStringWrapper(text);
            });

            ObservableList<Order> comenzi = FXCollections.observableArrayList(OrderManager.getInstance().getComenzi());
            tableRecentOrders.setItems(comenzi);
        }

        /**
         * Metoda pt butonul 'Inapoi'
         */
        @FXML
        private void handleBack(){
            MainController.navigateTo("MainView.fxml");
            MainController.showMenuFromOutside();
        }
    }