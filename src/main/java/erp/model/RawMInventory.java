package erp.model;
import erp.database.DatabaseManager;
import java.sql.*;
import java.util.*;
public class RawMInventory {
    private Map<RawMaterial, Double> stoc;

    public RawMInventory(){
        stoc = new HashMap<>();
    }

    public void addMaterial(RawMaterial r, double cant){
        if (!stoc.containsKey(r)) {
            stoc.put(r, cant);
        } else {
            stoc.put(r, stoc.get(r) + cant);
        }

        saveToDatabase(r);
    }

    public void updateMaterial(RawMaterial r, double cant){
        if(!stoc.containsKey(r))
            throw new IllegalArgumentException("Materia prima NU exista!");
        stoc.put(r, cant);
        saveToDatabase(r);
    }

    //scade cantitatea
    public void removeMaterial(RawMaterial r, double cant){
        if(stoc.containsKey(r)){
            if(stoc.get(r) < cant)
                throw new IllegalArgumentException("Stoc insuficient pentru " + r.getName());

            stoc.put(r, stoc.get(r) - cant);
        }
        saveToDatabase(r);
    }

    //sterge complet
    public void deleteMaterial(RawMaterial r){
        if(stoc.containsKey(r)){
            stoc.remove(r);
        }
        updateDatabaseAfterDelete(r);
    }

    public double getCantitate(RawMaterial r){
        return stoc.getOrDefault(r, 0.0);
    }

    public Map<RawMaterial, Double> getStoc(){
        return stoc;
    }

    public void loadFromDatabase() {
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM raw_materials");
            while (rs.next()) {
                RawMaterial f = new RawMaterial(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("unit")
                );
                double qty = rs.getDouble("quantity");

                stoc.put(f, qty);
            }
            rs.close();
            st.close();
            System.out.println("Stoc materie prima incarcat din baza de date.");
        } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void saveToDatabase(RawMaterial r){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO raw_materials (id, name, unit, quantity) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity=?");
            ps.setInt(1, r.getId());
            ps.setString(2, r.getName());
            ps.setString(3, r.getUnit());
            ps.setDouble(4, stoc.get(r));
            ps.setDouble(5, stoc.get(r));
            ps.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void updateDatabaseAfterDelete(RawMaterial r){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM raw_materials WHERE id = ?" );
            ps.setInt(1, r.getId());
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}
