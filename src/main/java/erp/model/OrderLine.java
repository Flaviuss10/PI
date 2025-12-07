package erp.model;

public class OrderLine{
    private FinishedProduct produs;
    private int cantitate;

    public OrderLine(FinishedProduct produs, int cantitate){
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public FinishedProduct getProdus() {
        return produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }

    public String getCodProdus(){ return produs.getCodProdus();}

    @Override
    public String toString() {
        return produs.getCodProdus() + " - " + cantitate + " bucati";
    }



}
