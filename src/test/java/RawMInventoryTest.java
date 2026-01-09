import erp.model.RawMInventory;
import erp.model.RawMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RawMInventoryTest {

    private RawMInventory inventory;
    private RawMaterial faina;

    @BeforeEach
    void setUp() {
        // Se rulează înainte de fiecare test -> avem un inventory curat
        inventory = new RawMInventory();
        faina = new RawMaterial(1, "Faina", "kg");
    }

    @Test
    void testAdaugareMaterial() {
        // 1. Setup
        inventory.addMaterial(faina, 10.0);

        // Verificăm dacă avem 10 kg
        assertEquals(10.0, inventory.getStoc().get(faina), "Stocul ar trebui sa fie 10.0 dupa adaugare");
    }

    @Test
    void testUpdateMaterial() {

        inventory.addMaterial(faina, 10.0);


        inventory.updateMaterial(faina, 15.0); // Presupunand ca update seteaza valoarea absoluta


        assertEquals(15.0, inventory.getStoc().get(faina));
    }

    @Test
    void testStergereMaterial() {
        inventory.addMaterial(faina, 10.0);


        inventory.deleteMaterial(faina);


        assertFalse(inventory.getStoc().containsKey(faina), "Faina nu ar trebui sa mai existe in stoc");
    }
}