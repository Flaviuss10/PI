package erp.service;
import erp.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelarea unei clase utilitare pentru stocurile companiei
 * Este o clasa singleton, deoarece dorim o singura instanta a acestui obiect (acelasi stoc in toata aplicatia)
 * Contine metode utilitare
 */
public class InventoryManager {


    /** Singura entitate din aplicatie */
    private static InventoryManager instance;

    /** Stocul pentru materie prima */
    private RawMInventory stocMateriePrima;

    /** Stocul pentru produsele finite */
    private FinishedPInventory stocProduse;

    /** Retetarul tuturor produselor finite din stoc */
    private RecipeBook retetar;

    /**
     * Constructor private, pentru a evita crearea unei alte instante
     */
    private InventoryManager(){
        stocMateriePrima = new RawMInventory();
        stocProduse = new FinishedPInventory();
        retetar = new RecipeBook();
    }

    /**
     * Metoda statica pentru resetarea fortata a stocului
     * Folosita la pornirea aplicatiei
     */
    public static void resetInstance() {
        instance = new InventoryManager();
    }

    /**
     * Gettter pentru instanta curenta
     * @return obiectul de tip InventoryManager
     */
    public static InventoryManager getInstance(){
        if(instance == null){
            instance = new InventoryManager();
        }
        return instance;
    }

    /**
     * Getter petru stocul de materie prima
     * @return RawMInventory
     */
    public RawMInventory getStocMateriePrima(){
        return stocMateriePrima;
    }

    /**
     * Getter pentru stocul de produse finite
     * @return FinishedPInventory
     */
    public FinishedPInventory getStocProdusFinit(){
        return stocProduse;
    }

    /**
     * Getter pentru Lista de produse finite din stoc
     * @return O lista cu obiecte FinishedProduct
     */
    public List<FinishedProduct> getListProduseFinite(){
        return new ArrayList<>(getInstance().stocProduse.getStoc().keySet());
    }

    /**
     * Getter pentru cantitatea unui produs finit din stoc
     * Foloseste un getter din clasa FinishedPInventory
     * @param p produsul finit dorit
     * @return cantitatea
     */
    public int getCantitateProdusFinit(FinishedProduct p){return stocProduse.getCantitate(p);}

    /**
     * Getter pentru retetar
     * @return RecipeBook
     */
    public RecipeBook getRetetar() {
        return retetar;
    }

    /**
     * Getter pentru stocul de produse finite
     * @return FinishedPInventory
     */
    public FinishedPInventory getStocProduse() {
        return stocProduse;
    }




    // Metodele utilitare mai jos





    /**
     * Metoda care contorizeaza cate stocuri critice exista
     * Stoc critic = Cantitatea unui produs finit < 10
     * @return Numarul de stocuri critice
     */
    public int stocCritic(){
        Map<FinishedProduct, Integer> stoc = instance.getStocProdusFinit().getStoc();

        int cnt = 0;

        for(Map.Entry<FinishedProduct, Integer> i : stoc.entrySet())
            if(i.getValue() < 10)
                cnt++;

        return cnt;
    }

    /**
     * Metoda pentru adagarea unei materii prime in stoc
     * @param r materia prima
     * @param cant cantitatea
     */
    public void adaugaMateriePrima(RawMaterial r, int cant) {
        if (r == null) throw new IllegalArgumentException("Materie prima null");
        if (cant <= 0) throw new IllegalArgumentException("Cantitatea trebuie sa fie > 0");
        stocMateriePrima.addMaterial(r, cant);
    }



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


    /**
     * Metoda pentru a scadea cantitatea unui produs finit
     * @param f produsul finit
     * @param cant cantitatea care trebuie scazuta
     */
    public void scadeProdusFinit(FinishedProduct f, int cant){
        stocProduse.scadeProdus(f, cant);
    }


    /**
     * Metoda care incarca stocurile din baza de date
     * Pentru fiecare stoc, se apeleaza metoda care incarca din baza de date
     * Folosita la pornirea aplicatiei
     */
    public void loadFromDatabase() {
        stocMateriePrima.loadFromDatabase();
        stocProduse.loadFromDatabase();
        retetar.loadAllRecipes();
    }
}
