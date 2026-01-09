package erp.model;

/**
 * Clasa care modeleaza un Produs Finit din cadrul companiei din industria alimentara
 */
public class FinishedProduct{

    /** ID-ul unic al produsului finit din baza de date */
    private int id;

    /** Codul Produsului, format din caractere alfanumerice, ex: CVCAS350*/
    private String codProdus;

    /** Tipul Produsului */
    private String tipProdus;

    /** Aroma produsului */
    private String aroma;

    /** Gramajul */
    private double gramaj;

    /** Unitatea de masura: kg/g */
    private String unit;

    /**
     * Constructor-ul obiectului
     */
    public FinishedProduct(int id, String codProdus, String tipProdus, String aroma, double gramaj, String unit){
        this.id = id;
        this.codProdus = codProdus;
        this.tipProdus = tipProdus;
        this.aroma = aroma;
        this.gramaj = gramaj;
        this.unit = unit;
    }

    /**
     * Getter pentru id
     * @return id-ul produsului
     */
    public int getId(){return id;}

    /**
     * Getter pentru codul Produsului
     * @return cod Produs
     */
    public String getCodProdus(){
        return codProdus;
    }

    /**
     * Getter tip Produs
     * @return tip Produs
     */
    public String getTipProdus(){return tipProdus;}

    /**
     * Getter pentru Aroma produsului
     * @return Aroma
     */
    public String getAroma(){return aroma;}

    /**
     * Getter pentru gramaj
     * @return gramajul Produsului
     */
    public double getGramaj(){return gramaj;}

    /**
     * Getter pentru unitatea de masura
     * @return unitate de masura
     */
    public String getUnit(){return unit;}

    /**
     * Suprascrierea metodei de afisare
     * @return String - descrierea obiectului de tip FinishedProduct
     */
    @Override
    public String toString() {
        return codProdus + " - " +  tipProdus + " - " + aroma + " - " + gramaj + " - " + unit;
    }

    /**
     * Suprascscrierea metodei pentru comparare
     * Necesara pentru a lucra cu Map, List
     * @param obj   Obiectul de tip Object cu care se compara
     * @return True daca cele 2 obiecte sunt egale, False altfel
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(!(obj instanceof FinishedProduct)) return false;

        FinishedProduct p = (FinishedProduct) obj;
        return this.id == p.id;
    }

    /**
     * Calcularea hashcode-ului in functie de id-ul produsului
     * @return hashCode-ul id-ului
     */
    public int hashCode(){
        return Integer.hashCode(id);
    }
}
