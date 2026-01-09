package erp.model;

import java.util.ArrayList;
import java.util.List;
import erp.database.DatabaseManager;
import java.sql.*;

/**
 * Clasa ce modeleaza o comanda primita de companie.
 * In viata reala, o astfel de comanda este formata din mai multe linii
 * cu proprietatea urmatoare: <Produs> - <Nr. bucati>
 * @author Flavius Neamu
 */
public class Order {
    /** Numarul de identificare al comenzii */
    private int id;

    /** Numele clientului */
    private String client;

    /** * Lista de produse comandate
    * Fiecare element este o linie de comanda (OrderLine) */
    private List<OrderLine> produse;

    /** Indica daca comanda a fost procesata */
    boolean procesata;

    /**
     * Constructor pentru obiectul de tip Order
     * @param id id-ul comenzii
     * @param client numele clientului
     * La creearea obiectului, lista de produse e goala, comanda fiind setata ca neprocesata
     */
    public Order(int id, String client){
        this.id = id;
        this.client = client;
        this.produse = new ArrayList<>();
        procesata = false;
    }

    /**
     * Metoda pentru adaugarea unui produs in Comanda
     * Se adauga in lista de produse un nou obiect de tip OrderLine
     * @param p Obiect de tip FinishedProduct - produsul care trebuie adaugat
     * @param cant Intreg - numarul de bucati
     */
    public void addToOrder(FinishedProduct p, int cant){
        produse.add(new OrderLine(p, cant));
    }

    /**
     * Getter pentru lista de produse
     * @return Lista cu produse din comanda
     */
    public List<OrderLine> getProduse() {
        return produse;
    }

    /**
     * Getter pentru numele clientului care a plasat comanda
     * @return String - numele clientului
     */
    public String getClient() {
        return client;
    }

    /**
     * Getter pentru numarul comenzii
     * @return id-ul comenzii plasate
     */
    public int getId() {
        return id;
    }

    /**
     * Getter pentru statusul comenzii
     * @return Boolean - status comanda
     */
    public boolean getProcesata() {
        return procesata;
    }

    /**
     * Setter pentru id-ul comenzii
     * @param orderId numarul comenzii
     */
    public void setId(int orderId) {
        id = orderId;
    }

    /**
     * Setter pentru statusul comenzii
     * @param processed status comanda
     */
    public void setProcesata(boolean processed) {
        this.procesata = processed;
    }

    /**
     * Metoda pentru cautarea unui produs finit in baza de date
     * @param id id-ul produsului finit
     * @param conn obiect de tip Connection - conexiunea la baza de date
     * @return Un obiect de tip FinishedProduct daca s-a gasit produsul, altfel null
     * @throws SQLException
     */
    private FinishedProduct findFinishedProduct(int id, Connection conn){
        FinishedProduct product = null;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM finished_products WHERE id = " + id);
            while(rs.next()){
                    product = new FinishedProduct(
                  rs.getInt("id"),
                  rs.getString("cod_produs"),
                  rs.getString("tip_produs"),
                  rs.getString("aroma"),
                  rs.getDouble("gramaj"),
                  rs.getString("unit")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }


    /**
     * Metoda pentru incarcarea Comenzilor din baza de date
     * Se populeaza lista de produse cu obiectele de tip OrderLine, create
     * in urma interogarii bazei de date
     * @throws SQLException
     */
    public void loadFromDatabase(){
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM order_lines WHERE order_id = " + id);
            while(rs.next()){
                int productId=rs.getInt("product_id");
                FinishedProduct produs = findFinishedProduct(productId, conn);
                int qty = rs.getInt("quantity");

                if(produs != null)
                    produse.add(new OrderLine(produs, qty));
            }
            rs.close();
            st.close();
            System.out.println("Comanda #" + id + " incarcata din baza de date (" + produse.size() + " produse).");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Suprascrierea metodei de afisare a obiectului
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Comanda #" + id + " - " + client + " - procesata: " + procesata + "\n");
        for (OrderLine line : produse) {
            sb.append("  ").append(line).append("\n");
        }
        return sb.toString();
    }


}
