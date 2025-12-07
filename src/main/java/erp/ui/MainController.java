package erp.ui;

import erp.database.DatabaseManager;
import erp.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.*;
public class MainController {


    private static MainController instance;

    private final Stack<String> pageHistory = new Stack<>();

    public MainController() {
        instance = this;
    }

//    public static void loadPageFromOutside(String fxml) {
//        instance.loadView(fxml);
//    }

    @FXML
    private VBox menuLeft;

    @FXML
    private BorderPane mainPane;

    public void hideMenu() {
        mainPane.setLeft(null);
    }

    public void showMenu() {
        mainPane.setLeft(menuLeft);
    }

    //ascunde meniul din stanga
    public static void hideMenuFromOutside(){
        instance.hideMenu();
    }

    public static void showMenuFromOutside(){
        instance.showMenu();
    }
    @FXML
    private StackPane contentArea;

    private void loadAllData() {
        // Încarcă datele o singură dată pentru întreaga aplicație

        InventoryManager.resetInstance();
        InventoryManager.getInstance().loadFromDatabase();

        OrderManager.init(InventoryManager.getInstance());
        OrderManager.getInstance().loadOrdersFromDatabase();
    }




    @FXML
    public void initialize(){
        loadAllData();
        navigateTo("Dashboard.fxml");
    }


    //incarca o pagina noua si adauga la stack
    private void loadView(String fileName, boolean addToHistory) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/erp/ui/" + fileName));
            if (addToHistory)
                pageHistory.push(fileName);
            contentArea.getChildren().setAll(view);

            // actualizează titlul
            String pageName = fileName.replace(".fxml", "");
            setPageTitle(pageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //schimba pagina
    public static void navigateTo(String fileName){
        instance.loadView(fileName, true);
    }

    // navigare pagina anterioara
    public static void goBack(){
        if(instance.pageHistory.size() > 1){
            instance.pageHistory.pop();
            String previousPage = instance.pageHistory.peek();
            instance.loadView(previousPage, false);
        }
    }

    @FXML
    private void openRecipes() {
        navigateTo("Recipes.fxml");
        showMenu();
    }

    @FXML
    private void openDashboard() {
        navigateTo("Dashboard.fxml");
        showMenu();
    }

    @FXML
    private void openStocks() {
        navigateTo("Stocks.fxml");
        showMenu();
    }

    @FXML
    private void openOrders() {
        navigateTo("Orders.fxml");
        showMenu();
    }

    @FXML
    private Label pageTitle;

    private void setPageTitle(String title) {
        pageTitle.setText(title);
    }



}
