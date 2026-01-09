import erp.model.*;
import erp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryManagerTest {

    private InventoryManager manager;

    //exemple obiecte test
    private RawMaterial faina;
    private FinishedProduct covr;
    private FinishedProduct corn;

    @BeforeEach
    void setUp() {
        //resetare singleton
        InventoryManager.resetInstance();
        manager = InventoryManager.getInstance();

        // obj test
        faina = new RawMaterial(1, "Faina Alba", "kg");
        covr = new FinishedProduct(100, "COVR", "covrigei", "sare", 700.0, "g");
        corn = new FinishedProduct(3, "CORN", "cornulete", "caise", 350, "g");
    }


    @Test
    void testAdaugaMateriePrima() {

        manager.adaugaMateriePrima(faina, 50);

        double stocActual = manager.getStocMateriePrima().getCantitate(faina);
        assertEquals(50.0, stocActual, "Stocul de faina ar trebui sa fie 50");
    }

    @Test
    void testAdaugaMateriePrimaInvalid() {
        // Testăm validările (cantitate negativă sau null)
        assertThrows(IllegalArgumentException.class, () -> {
            manager.adaugaMateriePrima(null, 10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            manager.adaugaMateriePrima(faina, -5);
        });
    }


    @Test
   void testStocCritic(){
        InventoryManager.getInstance().getStocProdusFinit().getStoc().put(covr, 5);
        InventoryManager.getInstance().getStocProdusFinit().getStoc().put(corn, 12);
        int expected = InventoryManager.getInstance().stocCritic();
        assertEquals(expected, 1);
    }

//    @Test
//    void testAdaugaProdusFinit_StocInsuficient() {
//        // 1. Setup Stoc Materie Primă (Avem doar 2 kg făină)
//        manager.adaugaMateriePrima(faina, 2);
//
//        // 2. Setup Rețetă (1 Pâine cere 1 kg făină)
//        Recipe retetaPaine = new Recipe(paine);
//        retetaPaine.addLine(new RecipeLine(faina, 1.0));
//        manager.getRetetar().addRecipe(retetaPaine);
//
//        // 3. Action: Vrem să producem 5 pâini (Necesar 5 kg, dar avem doar 2 kg)
//        Exception exception = assertThrows(IllegalStateException.class, () -> {
//            manager.adaugaProdusFinit(paine, 5);
//        });
//
//        assertTrue(exception.getMessage().contains("Stoc insuficient"),
//                "Ar trebui să blocheze producția din lipsă de stoc");
//
//        // Verificăm că stocul de materie primă NU s-a atins (tranzacție atomică logică)
//        assertEquals(2.0, manager.getStocMateriePrima().getCantitate(faina),
//                "Făina nu trebuia consumată dacă producția a eșuat");
//    }
//
//    @Test
//    void testStocCritic() {
//        // Adăugăm un produs cu cantitate mică (< 10)
//        manager.getStocProdusFinit().adaugaProdus(paine, 5); // 5 < 10 -> CRITIC
//
//        // Adăugăm un alt produs cu cantitate ok
//        FinishedProduct corn = new FinishedProduct(101, "Corn", 2.0, "buc");
//        manager.getStocProdusFinit().adaugaProdus(corn, 50); // 50 > 10 -> OK
//
//        // Verify
//        assertEquals(1, manager.stocCritic(), "Ar trebui să existe un singur produs cu stoc critic");
//    }
}