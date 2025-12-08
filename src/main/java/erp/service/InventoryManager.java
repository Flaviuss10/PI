package erp.service;
import erp.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    //clasa singleton ca sa limitez acces la o singura entitate

    private static InventoryManager instance;

    private RawMInventory stocMateriePrima;
    private FinishedPInventory stocProduse;
    private RecipeBook retetar;

    private InventoryManager(){
        stocMateriePrima = new RawMInventory();
        stocProduse = new FinishedPInventory();
        retetar = new RecipeBook();
    }

    public static InventoryManager getInstance(){
        if(instance == null){
            instance = new InventoryManager();
        }
        return instance;
    }

    //pentru resetare fortata
    public static void resetInstance() {
        instance = new InventoryManager();
    }




    // Metode utilitare mai jos




    public int stocCritic(){
       Map<FinishedProduct, Integer> stoc = instance.getStocProdusFinit().getStoc();

       int cnt = 0;

       for(Map.Entry<FinishedProduct, Integer> i : stoc.entrySet())
           if(i.getValue() < 10)
               cnt++;

       return cnt;
    }

    public void adaugaMateriePrima(RawMaterial r, int cant){
        if (r == null) throw new IllegalArgumentException("Materie prima null");
        if (cant <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie > 0");
        stocMateriePrima.addMaterial(r, cant);
    }

    public void setRetetar(RecipeBook r){
        retetar = r;
    }


    public void adaugaProdusNou(){}

    //CONSUMA** produs finit
    public void adaugaProdusFinit(FinishedProduct f, int cant){

        if (f == null) throw new IllegalArgumentException("Produs finit null");
        if (cant <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie > 0");


        if (!retetar.existaReteta(f)) {
            throw new IllegalStateException("Nu exista reteta pentru produsul: " + f.getCodProdus());
        }

        Map<RawMaterial, Double> consumTotal = new HashMap<>();
        List<RecipeLine> retetaProdus = retetar.getReteta(f).getRetetar();
        for(RecipeLine i : retetaProdus){
           RawMaterial ingredient = i.getIngredient();
           double cantitate = i.getCantitatePerUnitate() * cant;
           consumTotal.put(ingredient, consumTotal.getOrDefault(ingredient, 0.0) + cantitate);
        }
        for (Map.Entry<RawMaterial, Double> e : consumTotal.entrySet()) {
            double disponibil = stocMateriePrima.getCantitate(e.getKey());
            if (disponibil + 1e-9 < e.getValue()) { // toleranta mica pt precizie double
                throw new IllegalStateException(
                        "Stoc insuficient pentru " + e.getKey().getName() +
                                ". Necesar: " + e.getValue() + " " + e.getKey().getUnit() +
                                ", disponibil: " + disponibil
                );
            }
        }


        for(Map.Entry<RawMaterial, Double> i : consumTotal.entrySet())
            stocMateriePrima.removeMaterial(i.getKey(), i.getValue());

        stocProduse.adaugaProdus(f, cant);

    }

    public void scadeMateriePrima(RawMaterial r, int cant){
        stocMateriePrima.removeMaterial(r, cant);
    }

    public void scadeProdusFinit(FinishedProduct f, int cant){
        stocProduse.scadeProdus(f, cant);
    }

    public RawMInventory getStocMateriePrima(){
       return stocMateriePrima;
    }

    public FinishedPInventory getStocProdusFinit(){
        return stocProduse;
    }

    public List<FinishedProduct> getListProduseFinite(){
        return new ArrayList<>(getInstance().stocProduse.getStoc().keySet());
    }

    public double getCantitateMateriePrima(RawMaterial r){return stocMateriePrima.getCantitate(r);}

    public int getCantitateProdusFinit(FinishedProduct p){return stocProduse.getCantitate(p);}

    public void afisareStocuri() {
        System.out.println("=== STOC MATERII PRIME ===");
        for (Map.Entry<RawMaterial, Double> entry : stocMateriePrima.getStoc().entrySet()) {
            System.out.println(entry.getKey().toString() + " -> " + entry.getValue());
        }

        System.out.println("\n=== STOC PRODUSE FINITE ===");
        for (Map.Entry<FinishedProduct, Integer> entry : stocProduse.getStoc().entrySet()) {
            System.out.println(entry.getKey().toString() + " -> " + entry.getValue());
        }
    }
    public void loadFromDatabase() {
        stocMateriePrima.loadFromDatabase();
        stocProduse.loadFromDatabase();
        retetar.loadAllRecipes();
    }
}
