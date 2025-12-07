package erp.model;

public class FinishedProduct{

    private int id;
    private String codProdus;
    private String tipProdus;
    private String aroma;
    private double gramaj;
    private String unit;

    public FinishedProduct(int id, String codProdus, String tipProdus, String aroma, double gramaj, String unit){
        this.id = id;
        this.codProdus = codProdus;
        this.tipProdus = tipProdus;
        this.aroma = aroma;
        this.gramaj = gramaj;
        this.unit = unit;
    }

    public int getId(){return id;}
    public String getCodProdus(){
        return codProdus;
    }
    public String getTipProdus(){return tipProdus;}
    public String getAroma(){return aroma;}
    public double getGramaj(){return gramaj;}
    public String getUnit(){return unit;}

    @Override
    public String toString() {
        return tipProdus + ", " + aroma + ", " + gramaj;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(!(obj instanceof FinishedProduct)) return false;

        FinishedProduct p = (FinishedProduct) obj;
        return this.id == p.id;
    }

    public int hashCode(){
        return Integer.hashCode(id);
    }
}
