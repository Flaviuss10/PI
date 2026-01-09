package erp.ui;

import erp.database.DatabaseManager;
import erp.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.*;

public class MainController {

    private static MainController instance;
    private final Stack<String> pageHistory = new Stack<>();

    /**
     * Constructorul clasei
     * Avem o singura instanta a obiectului
     */
    public MainController() {
        instance = this;
    }

    //    public static void loadPageFromOutside(String fxml) {
    //        instance.loadView(fxml);
    //    }

    @FXML private VBox menuLeft;
    @FXML private BorderPane mainPane;
    @FXML private BorderPane mainBorderPane;
    @FXML private StackPane contentArea;
    @FXML private Label pageTitle;

    /**
     * Metoda specifica pentru butonul de 'Inapoi'
     * Incarca direct continutul Dashboard-ului in centrul ecranului
     */
    @FXML
    private void handleBack() throws IOException {
        Parent dashboardContent = FXMLLoader.load(getClass().getResource("DashboardContent.fxml"));
        mainBorderPane.setCenter(dashboardContent);
    }

    /**
     * Handler pentru butonul din meniu care deschide Dashboard-ul.
     * Navigheaza catre pagina si se asigura ca meniul lateral este vizibil.
     */
    @FXML
    private void openDashboard() {
        navigateTo("Dashboard.fxml");
        showMenu();
    }

    /**
     * Scoate meniul din stanga (sidebar-ul) din interfata
     * Utila daca vrem sa afisam o pagina pe tot ecranul
     */
    public void hideMenu() {
        mainPane.setLeft(null);
    }

    /**
     * Reafiseaza meniul din stanga in interfata principala.
     */
    public void showMenu() {
        mainPane.setLeft(menuLeft);
    }

    /**
     * Metoda statica prin care alte clase pot cere ascunderea meniului.
     * Foloseste instanta unica (Singleton) creata in constructor.
     */
    public static void hideMenuFromOutside(){
        instance.hideMenu();
    }

    /**
     * Metoda statica prin care alte clase pot cere afisarea meniului.
     */
    public static void showMenuFromOutside(){
        instance.showMenu();
    }

    /**
     * Incarca toate datele necesare aplicatiei din baza de date.
     * Aceasta metoda este critica: initializeaza managerii de stoc si comenzi
     * si populeaza listele din memorie la pornirea aplicatiei.
     */
    private void loadAllData() {
        // Resetam si incarcam stocul (Materii prime + Produse finite)
        InventoryManager.resetInstance();
        InventoryManager.getInstance().loadFromDatabase();

        // Initializam managerul de comenzi si incarcam istoricul comenzilor
        OrderManager.init(InventoryManager.getInstance());
        OrderManager.getInstance().loadOrdersFromDatabase();
    }

    @FXML
    public void initialize(){
        loadAllData();
        navigateTo("Dashboard.fxml");
    }

    /**
     * Motorul de navigare al aplicatiei.
     * <p>
     * 1. Incarca fisierul FXML specificat.
     * 2. Il adauga in istoricul de navigare (Stack) pentru a putea da Back.
     * 3. Inlocuieste continutul din centrul ecranului cu noua pagina.
     * 4. Actualizeaza titlul ferestrei.
     * </p>
     * @param fileName Numele fisierului .fxml (ex: "Orders.fxml")
     * @param addToHistory Daca pagina trebuie tinuta minte in istoric (true/false)
     */
    private void loadView(String fileName, boolean addToHistory) {
        try {
            // Incarcam pagina
            Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/erp/ui/" + fileName)));

            // Adaugam in stiva de istoric daca e cazul
            if (addToHistory)
                pageHistory.push(fileName);

            // Afisam pagina
            contentArea.getChildren().setAll(view);

            // Setam titlul frumos (fara extensia .fxml)
            String pageName = fileName.replace(".fxml", "");
            setPageTitle(pageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda publica si statica pentru schimbarea paginii.
     * Orice controller poate apela MainController.navigateTo("Pagina.fxml").
     */
    public static void navigateTo(String fileName){
        instance.loadView(fileName, true);
    }

    /**
     * Implementeaza functionalitatea butonului "Inapoi" global.
     * Verifica daca exista pagini in istoric, scoate ultima pagina vizitata
     * si o incarca pe cea anterioara (fara sa o mai bage o data in istoric).
     */
    public static void goBack(){
        if(instance.pageHistory.size() > 1){
            instance.pageHistory.pop(); // Scoatem pagina curenta
            String previousPage = instance.pageHistory.peek(); // Vedem care e anterioara
            instance.loadView(previousPage, false); // O incarcam
        }
    }

    /**
     * Handler pentru butonul din meniu care deschide pagina de Retete.
     */
    @FXML
    private void openRecipes() {
        navigateTo("Recipes.fxml");
        showMenu();
    }

    /**
     * Handler pentru butonul din meniu care deschide pagina de Stocuri.
     */
    @FXML
    private void openStocks() {
        navigateTo("Stocks.fxml");
        showMenu();
    }

    /**
     * Handler pentru butonul din meniu care deschide pagina de Comenzi.
     */
    @FXML
    private void openOrders() {
        navigateTo("Orders.fxml");
        // showMenu(); // Putem decomenta daca vrem sa fortam meniul
    }

    /**
     * Actualizeaza textul din partea de sus a ferestrei (Titlul paginii).
     */
    private void setPageTitle(String title) {
        pageTitle.setText(title);
    }

}