package erp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import erp.database.DatabaseManager;
import java.sql.*;
public class Order {
    private int id;
    private String client;
    private List<OrderLine> produse;
    boolean procesata;

    public Order(int id, String client){
        this.id = id;
        this.client = client;
        this.produse = new ArrayList<>();
        procesata = false;
    }

    public void addToOrder(FinishedProduct p, int cant){
        produse.add(new OrderLine(p, cant));
    }

    public List<OrderLine> getProduse() {
        return produse;
    }

    public String getClient() {
        return client;
    }

    public int getId() {
        return id;
    }

    public boolean getProcesata() {
        return procesata;
    }

    public void setProcesata(boolean processed) {
        this.procesata = processed;
    }

    // conexiunea cu db
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Comanda #" + id + " - " + client + " - procesata: " + procesata + "\n");
        for (OrderLine line : produse) {
            sb.append("  ").append(line).append("\n");
        }
        return sb.toString();
    }

    public void setId(int orderId) {
        id = orderId;
    }
}
