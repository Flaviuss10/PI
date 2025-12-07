package erp.model;
import erp.database.DatabaseManager;

import java.sql.*;
import java.util.*;
public class FinishedPInventory {
    private Map<FinishedProduct, Integer> stoc;

    public FinishedPInventory() {
        stoc = new HashMap<>();
    }



    public void adaugaProdus(FinishedProduct f, int cant) {
        stoc.put(f, stoc.getOrDefault(f, 0) + cant);
    }

    public void scadeProdus(FinishedProduct f, int cant) {
        int curent = stoc.getOrDefault(f, 0);
        if (curent < cant) {
            throw new IllegalArgumentException("Stoc insuficient pentru " + f.getCodProdus());
        }
        stoc.put(f, curent - cant);
    }

    public void loadFromDatabase(){
        try{
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM finished_products");
            while(rs.next()){

                FinishedProduct p = new FinishedProduct(
                        rs.getInt("id"),
                        rs.getString("cod_produs"),
                        rs.getString("tip_produs"),
                        rs.getString("aroma"),
                        rs.getDouble("gramaj"),
                        rs.getString("unit"));

                stoc.put(p, rs.getInt("quantity"));
            }
            rs.close();
            st.close();
            System.out.println("Stoc produse finite incarcat din baza de date.");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void saveToDatabase(FinishedProduct p){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO finished_products (id, cod_produs, tip_produs, aroma, gramaj, unit, quantity) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity=?");

            ps.setInt(1, p.getId());
            ps.setString(2, p.getCodProdus());
            ps.setString(3, p.getTipProdus());
            ps.setString(4, p.getAroma());
            ps.setDouble(5, p.getGramaj());
            ps.setString(6, p.getUnit());
            ps.setDouble(7, stoc.get(p));
            ps.setDouble(8, stoc.get(p));
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public int getCantitate(FinishedProduct f) {
        return stoc.getOrDefault(f, 0);
    }

    public Map<FinishedProduct, Integer> getStoc() {
        return stoc;
    }
}
