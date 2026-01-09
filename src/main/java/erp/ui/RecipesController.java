package erp.ui;

import erp.model.*;
import erp.service.InventoryManager;
import erp.service.RecipeBook;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class RecipesController {

    // stanga
    @FXML
    private TextField txtSearchProduct;

    @FXML
    private ListView<FinishedProduct> listProducts;

    //dreapta
    @FXML
    private Label lblSelectedProduct;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnAddIngredient;

    @FXML
    private TableView<RecipeLine> tableRecipeLines;


    @FXML
    private TableColumn<RecipeLine, RawMaterial> colIngredient;

    @FXML
    private TableColumn<RecipeLine, Double> colCantitate;

    @FXML
    private TableColumn<RecipeLine, String> colUnitate;

    // produsul curent care e selectat din tabel
    private FinishedProduct currentProduct;


    /**
     * Metoda apelata la deschiderea ferestrei
     */
    @FXML
    public void initialize() {

        setupTableColumns();

        //produse finite - stanga
        loadProductList();


        listProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectProduct(newVal);
            }
        });
    }

    /**
     * Metoda pentru setarea informatiilor din tabel
     * Fiecare coloana isi ia valoarea din getter-ul specifcat
     */
    private void setupTableColumns() {


        colIngredient.setCellValueFactory(new PropertyValueFactory<>("numeIngredient"));
        colCantitate.setCellValueFactory(new PropertyValueFactory<>("cantitatePerUnitate"));
        colUnitate.setCellValueFactory(new PropertyValueFactory<>("unit"));
    }

    /**
     * Metoda care incarca toate produsele finite ale companiei
     */
    private void loadProductList() {
        listProducts.setItems(FXCollections.observableList(InventoryManager.getInstance().getListProduseFinite()));
    }

    /**
     * Metoda folosita pentru adaugarea unui ingredient in retetarul unui produs
     * Se deschide o fereastra care necesita input pentru ingredient(denumire, cantitate, unitate de masura)
     * Cu informatiile primite se creeaza un obiect RawMaterial nou
     * Daca nu exista acea materie prima in stocul de materii prime -> se adauga cu stoc 0
     * Se adauga in tabel noul ingredient
     * @throws NumberFormatException
     */
    @FXML
    private void handleAddIngredient() {
        if (currentProduct == null) return;

        // dialog
        Dialog<RecipeLine> dialog = new Dialog<>();
        dialog.setTitle("Adaugă Ingredient Nou");
        dialog.setHeaderText("Definește ingredientul și cantitatea");

        ButtonType loginButtonType = new ButtonType("Adaugă", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtIngredient = new TextField();
        txtIngredient.setPromptText("Ex: Făină");

        TextField txtCantitate = new TextField();
        txtCantitate.setPromptText("Ex: 0.5");

        TextField txtUnitate = new TextField();
        txtUnitate.setPromptText("Ex: kg");

        grid.add(new Label("Ingredient:"), 0, 0);
        grid.add(txtIngredient, 1, 0);
        grid.add(new Label("Cantitate:"), 0, 1);
        grid.add(txtCantitate, 1, 1);
        grid.add(new Label("Unitate de masura:"), 0, 2);
        grid.add(txtUnitate, 1, 2);

        dialog.getDialogPane().setContent(grid);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String nume = txtIngredient.getText();
                String unit = txtUnitate.getText();
                String cantStr = txtCantitate.getText();

                if (!nume.isEmpty() && !unit.isEmpty() && !cantStr.isEmpty()) {
                    try {
                        double cant = Double.parseDouble(cantStr);

                        // calcul id = max + 1
                        int maxId = InventoryManager.getInstance().getStocMateriePrima().getStoc().keySet()
                                .stream()
                                .mapToInt(RawMaterial::getId)
                                .max()
                                .orElse(0);
                        int newId = maxId + 1;

                        // creare materie prima
                        RawMaterial newRawM = new RawMaterial(newId, nume, unit);

                        //add la bd cu stoc 0 daca nu exista
                        if(!InventoryManager.getInstance().getStocMateriePrima().getStoc().containsKey(newRawM))
                            InventoryManager.getInstance().getStocMateriePrima().addMaterial(newRawM, 0.0);
                        return new RecipeLine(newRawM, cant);

                    } catch (NumberFormatException e) {
                        System.out.println("Cantitate invalidă!");
                        return null;
                    }
                }
            }
            return null;
        });

        Optional<RecipeLine> result = dialog.showAndWait();

        result.ifPresent(line -> tableRecipeLines.getItems().add(line));
    }

    /**
     * Metoda pentru salvarea unei retete
     * Daca produsul nu are reteta => se creeaza o reteta cu ID = 0
     * Daca are reteta => ramane acelasi ID
     * Se adauga la noua reteta toate recipe lines din tabel (inclusiv noile linii)
     * Se salveaza reteta in bd, dar si in Recipe Book
     */
    @FXML
    private void handleSaveRecipe() {
        if (currentProduct == null) return;

        if (tableRecipeLines.getItems().isEmpty()) {
            System.out.println("Nu poți salva o rețetă goală!");
            return;
        }

        RecipeBook rBook    = InventoryManager.getInstance().getRetetar();
        Recipe oldRecipe = rBook.getReteta(currentProduct);

        int idReteta = oldRecipe == null ? 0 : oldRecipe.getId();

        Recipe newRecipe = new Recipe(idReteta, currentProduct);

        for(var i : tableRecipeLines.getItems())
            newRecipe.adaugaIngredient(i.getIngredient(), i.getCantitatePerUnitate());

        newRecipe.saveToDatabase();

        rBook.addReteta(currentProduct, newRecipe);

    }

    /**
     * Buton 'Inapoi'
     */
    @FXML
    private void goBack() {
        MainController.navigateTo("MainView.fxml");
    }


    /**
     * Metoda care incarca reteta unui produs selectat
     * Se modifica UI-ul cu denumirea produsului selectat
     * Se incarca toate ingredientele din reteta produsului selectat
     * Populare tabel
     * @param product produsul selectat
     */
    private void selectProduct(FinishedProduct product) {
        this.currentProduct = product;

        // Update UI
        lblSelectedProduct.setText("Rețetă pentru: " + product.getTipProdus() + " - " + product.getAroma());
        btnSave.setDisable(false);
        btnAddIngredient.setDisable(false);

        RecipeBook retetar = InventoryManager.getInstance().getRetetar();

        // incarcare ingrediente din reteta produsului selectat + afisare
        if(retetar.existaReteta(product)){

            Recipe recipe = retetar.getReteta(product);

            tableRecipeLines.setItems(FXCollections.observableList(recipe.getRetetar()));
        }else{
            tableRecipeLines.setItems(FXCollections.observableArrayList());
        }
    }}
