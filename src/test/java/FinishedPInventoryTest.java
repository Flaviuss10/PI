import erp.model.FinishedPInventory;
import erp.model.FinishedProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FinishedPInventoryTest {
    private FinishedPInventory stoc;
    private FinishedProduct produs;

    @BeforeEach
    void setUp(){
        stoc = new FinishedPInventory();
        produs = new FinishedProduct(1, "COVR", "covrigei","sare", 700.0, "g");
    }

    @Test
    void testAdaugaProdus(){
        stoc.adaugaProdus(produs, 100);
        assertTrue(stoc.getStoc().containsKey(produs));
        assertEquals(100, stoc.getCantitate(produs));
    }
    @Test
    void testScadeProdus(){
        //adaugam produsul in Map
        stoc.getStoc().put(produs, 100);
        stoc.scadeProdus(produs, 10);
        assertEquals(90, stoc.getStoc().get(produs));
    }


}


