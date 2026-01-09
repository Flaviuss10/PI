package erp.ui;

import erp.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clasa principala a aplicatiei (Entry Point).
 * <p>
 * Deoarece mosteneste clasa "Application" din JavaFX, aceasta este clasa
 * care porneste interfata grafica si initializeaza intregul sistem ERP.
 * </p>
 */
public class Main extends Application {

    /**
     * Metoda obligatorie in JavaFX care construieste interfata grafica.
     * Este apelata automat dupa ce se executa metoda main -> launch().
     *
     * @param stage "Scena" principala (fereastra aplicatiei).
     * @throws Exception Daca fisierul FXML nu este gasit sau baza de date nu merge.
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Pasul 1: Conectarea la Baza de Date.
        // Este critic sa facem asta inainte sa incarcam interfata, pentru ca
        // tabelele vor incerca sa traga date imediat ce apar pe ecran.
        DatabaseManager.connect();

        // Pasul 2: Incarcarea design-ului vizual.
        // FXMLLoader citeste fisierul .fxml (codul XML care descrie butoanele si tabelele)
        // si il transforma in obiecte Java utilizabile.
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/erp/ui/MainView.fxml")
        );

        // Cream scena (continutul ferestrei) pe baza design-ului incarcat mai sus.
        Scene scene = new Scene(loader.load());

        // Pasul 3: Configurarea ferestrei principale (Stage).
        stage.setTitle("ERP System"); // Titlul care apare in bara de sus
        stage.setScene(scene);        // Atasam scena la fereastra

        // Setam niste dimensiuni minime ca sa nu se strice asezarea elementelor
        // daca utilizatorul incearca sa faca fereastra prea mica.
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setResizable(true); // Permitem utilizatorului sa mareasca fereastra

        // Pasul 4: Afisarea ferestrei pe ecran.
        stage.show();
    }

    /**
     * Metoda standard de pornire pentru orice aplicatie Java.
     * Rolul ei este doar sa apeleze launch(), care porneste motorul JavaFX.
     */
    public static void main(String[] args) {
        launch();
    }
}