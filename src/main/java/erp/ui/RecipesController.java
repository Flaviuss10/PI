package erp.ui;

import erp.model.FinishedProduct;
import erp.model.RecipeLine;
import erp.model.RawMaterial;
import erp.service.InventoryManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RecipesController {

    // --- ZONA STÂNGA (Master) ---
    @FXML
    private TextField txtSearchProduct;

    @FXML
    private ListView<FinishedProduct> listProducts;

    // --- ZONA DREAPTA (Detail) ---
    @FXML
    private Label lblSelectedProduct;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnAddIngredient;

    @FXML
    private TableView<RecipeLine> tableRecipeLines;

    // Coloanele tabelului
    // Nota: E posibil sa ai nevoie de o clasa Wrapper (ex: RecipeLineWrapper) daca vrei editare usoara,
    // similar cu ce ai facut la RawMController. Aici am pus RecipeLine standard ca exemplu.
    @FXML
    private TableColumn<RecipeLine, RawMaterial> colIngredient;

    @FXML
    private TableColumn<RecipeLine, Double> colCantitate;

    @FXML
    private TableColumn<RecipeLine, String> colUnitate;

    @FXML
    private TableColumn<RecipeLine, Void> colActiuni; // Void pentru butoane

    // Produsul selectat curent (pentru a sti pentru cine salvam reteta)
    private FinishedProduct currentProduct;

    @FXML
    public void initialize() {
        // 1. Configureaza coloanele tabelului (CellValueFactory)
        setupTableColumns();

        // 2. Incarca lista de produse finite din DB in listProducts
        loadProductList();

        // 3. Adauga Listener pe selectia din ListView
        listProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectProduct(newVal);
            }
        });
    }

    private void setupTableColumns() {
        // TODO: Aici vei lega coloanele de atributele clasei RecipeLine
        // TODO: Aici vei defini CellFactory pentru a avea ComboBox pe colIngredient si TextField pe colCantitate




    }

    private void loadProductList() {
        listProducts.setItems(FXCollections.observableList(InventoryManager.getInstance().getListProduseFinite()));
    }

    private void selectProduct(FinishedProduct product) {
        this.currentProduct = product;

        // Update UI
        lblSelectedProduct.setText("Rețetă pentru: " + product.getTipProdus() + " - " + product.getAroma());
        btnSave.setDisable(false);
        btnAddIngredient.setDisable(false);

        // TODO: Verifica daca exista deja o reteta in RecipeBook pentru acest produs
        // Daca DA -> Incarca ingredientele in tableRecipeLines
        // Daca NU -> Goleste tabelul (tableRecipeLines.getItems().clear())
    }

    @FXML
    private void handleAddIngredient() {
        // TODO: Adauga un rand nou gol (sau cu valori default) in tabel
        System.out.println("Adauga ingredient nou (gol) in tabel...");
    }

    @FXML
    private void handleSaveRecipe() {
        if (currentProduct == null) return;

        // TODO: Itereaza prin itemii din tableRecipeLines
        // TODO: Construieste obiectul Recipe
        // TODO: Salveaza in RecipeBook / Baza de date

        System.out.println("Salvare reteta pentru: " + currentProduct.getCodProdus());
    }

    @FXML
    private void goBack() {
        MainController.navigateTo("MainView.fxml");
    }
}