package erp.main;
import erp.database.DatabaseManager;
import erp.model.*;
import erp.service.InventoryManager;
import erp.service.OrderManager;
import erp.service.RecipeBook;

import java.sql.*;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //testare in consola
        DatabaseManager.connect();
        InventoryManager.resetInstance();
        InventoryManager.getInstance().loadFromDatabase();

        OrderManager.init(InventoryManager.getInstance());
        OrderManager.getInstance().loadOrdersFromDatabase();

        OrderManager.getInstance().afisareComenzi();


        DatabaseManager.close();
        /**
        // 1) Initializam managerul de stocuri
        InventoryManager manager = new InventoryManager();

        // 2) Definim cateva materii prime
        RawMaterial faina = new RawMaterial(1, "Faina alba", "kg");
        RawMaterial zahar = new RawMaterial(2, "Zahar", "kg");
        RawMaterial gemCaise = new RawMaterial(3, "Gem caise", "kg");
        RawMaterial ou = new RawMaterial(4,"Ou", "ml");


        // 3) Adaugam materii prime in stocul de materii
        manager.adaugaMateriePrima(faina, 100);
        manager.adaugaMateriePrima(zahar, 50);
        manager.adaugaMateriePrima(gemCaise, 30);
        manager.adaugaMateriePrima(ou, 5000);
        // 4) Definim cateva produse finite
        FinishedProduct cornuleteCaise = new FinishedProduct(10, "CG350", "Cornulete", "Caise", 350, "buc");
        FinishedProduct covrigei = new FinishedProduct(11, "covr", "Covrigei", "Simpli", 700, "buc");

        // Cream retetarul pentru cele 2 produse
        Recipe retetaCornulete = new Recipe(cornuleteCaise);
        retetaCornulete.adaugaIngredient(faina, 0.25);
        retetaCornulete.adaugaIngredient(zahar, 0.1);
        retetaCornulete.adaugaIngredient(gemCaise, 0.5);

        Recipe retetaCovrigei = new Recipe(covrigei);
        retetaCovrigei.adaugaIngredient(faina, 0.3);
        retetaCovrigei.adaugaIngredient(ou, 500);

        RecipeBook retetar = new RecipeBook();
        retetar.addReteta(covrigei, retetaCovrigei);
        retetar.addReteta(cornuleteCaise, retetaCornulete);

        manager.setRetetar(retetar);
        //  Afisam stocurile initiale
      //  System.out.println("=== STOCURI INITIALE ===");
      //  manager.afisareStocuri();

        //  Adaugam produse finite in stocul de produse

            manager.adaugaProdusFinit(cornuleteCaise, 10);
            manager.adaugaProdusFinit(covrigei, 4);
            manager.adaugaProdusFinit(covrigei, 6);

        //System.out.println("=== STOCURI DUPA ADAUGARE FINIT ===");
        //manager.afisareStocuri();


        Order comanda = new Order(1, "xyz");
        comanda.addProdus(covrigei, 10);
        comanda.addProdus(cornuleteCaise, 5);
        OrderManager manComanda = new OrderManager(manager);

        Map<FinishedProduct, Integer> disponibilitate = manComanda.verificaDisponibilitate(comanda);
        if(disponibilitate.isEmpty()){
            System.out.println("COmanda POATE fi onorata!");
            manComanda.proceseazaComanda(comanda);
            System.out.println("=== STOCURI DUPA COMANDA ===");
            manager.afisareStocuri();
        }
        else{
            System.out.println("Comanda NU poate fi onorata!");
            manComanda.showProduseLipsa(comanda);
        }

    */
    }

}
