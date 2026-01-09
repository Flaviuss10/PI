package erp.model;

/**
 * Clasa care modeleaza o linie de comanda 
 * O linie de comanda reprezinta de fapt o mapare de tipul Produs - Cantitate
 */
public class OrderLine{
    /** Produsul finit */
    private FinishedProduct produs;
    
    /** Cantiatea (nr. bucati) */
    private int cantitate;

    /**
     * Constructor
     */
    public OrderLine(FinishedProduct produs, int cantitate){
        this.produs = produs;
        this.cantitate = cantitate;
    }

    /**
     * Getter pentru obiectul de tip Produs
     * @return FinishedProduct
     */
    public FinishedProduct getProdus() {
        return produs;
    }

    /**
     * Getter pentru cantitate
     * @return numar intreg, numarul de bucati
     */
    public int getCantitate() {
        return cantitate;
    }

    /**
     * Getter pentru cod Produs
     * @return String, codul produsului
     */
    public String getCodProdus(){ return produs.getCodProdus();}

    /**
     * Suprascrierea metodei de afisare
     * @return descrierea obiectului FinishedProduct
     */
    @Override
    public String toString() {
        return produs.getCodProdus() + " - " + cantitate + " bucati";
    }
}
