package erp.model;
import erp.database.DatabaseManager;
import java.sql.*;
import java.util.*;

/**
 * Clasa utilitara ce gestioneaza stocul de materii prime din cadrul companieix
 */
public class RawMInventory {

    /** stocul de materii prime, Map: Materie Prima -> Cantitate */
    private Map<RawMaterial, Double> stoc;

    /** Constructor */
    public RawMInventory(){
        stoc = new HashMap<>();
    }

    /**
     * Getter pentru cantitatea unei materii prime
     * @param r materia prima
     * @return cantitatea materiei prime din stoc
     */
    public double getCantitate(RawMaterial r){
        return stoc.getOrDefault(r, 0.0);
    }

    /**
     * Getter pentru Stocul de materii prime
     * @return Map reprezentand stocul de materii prime
     */
    public Map<RawMaterial, Double> getStoc(){
        return stoc;
    }

    /**
     * Metoda care creste cantitatea unei materii prime din stoc
     * Se modifica atat Map-ul, dar se actualizeaza si baza de date
     * @param r materia prima
     * @param cant cantitatea care se adauga
     */
    public void addMaterial(RawMaterial r, double cant){
        if (!stoc.containsKey(r)) {
            stoc.put(r, cant);
        } else {
            stoc.put(r, stoc.get(r) + cant);
        }

        saveToDatabase(r);
    }

    /**
     * Metoda ce modifica cantitatea unei materii prime
     * Modifica Map-ul, dar si baza de date
     * @param r materia prima
     * @param cant cantitatea noua
     * @throws IllegalArgumentException
     */
    public void updateMaterial(RawMaterial r, double cant){
        if(!stoc.containsKey(r))
            throw new IllegalArgumentException("Materia prima NU exista!");
        stoc.put(r, cant);
        saveToDatabase(r);
    }

    //scade cantitatea

    /**
     * Metoda ce scade cantitatea unei materii prime
     * @param r materia prima
     * @param cant cantitatea ce trebuie scazuta
     * @throws IllegalArgumentException
     */
    public void removeMaterial(RawMaterial r, double cant){
        if(stoc.containsKey(r)){
            if(stoc.get(r) < cant)
                throw new IllegalArgumentException("Stoc insuficient pentru " + r.getName());

            stoc.put(r, stoc.get(r) - cant);
        }
        saveToDatabase(r);
    }

    /**
     * Metoda ce sterge complet o materie prima atat din Map cat si baza de date
     * @param r materia prima ce trebuie stearsa
     */
    public void deleteMaterial(RawMaterial r){
        if(stoc.containsKey(r)){
            stoc.remove(r);
        }
        updateDatabaseAfterDelete(r);
    }


    /**
     * Metoda pentru incarcarea stocului de materii prime
     * @throws SQLException
     */
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

    /**
     * Metoda penntru actualizarea/salvarea unei materii prime in baza de date
     * @param r materia prima ce trebuie incarcata in baza de date
     * @throws IllegalStateException UnitTests
     * @throws SQLException
     */
    private void saveToDatabase(RawMaterial r){
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO raw_materials (id, name, unit, quantity) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity=?");
            ps.setInt(1, r.getId());
            ps.setString(2, r.getName());
            ps.setString(3, r.getUnit());
            ps.setDouble(4, stoc.get(r));
            ps.setDouble(5, stoc.get(r));
            ps.executeUpdate();
        }catch(IllegalStateException exception) {
            System.out.println("Modul de test pt UnitTest. Modificare in memorie");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Metoda ce actualizeaza baza de date in urma stergerii complete a unei materii prime
     * @param r materia prima ce a fost stearsa
     * @throws SQLException
     */
    private void updateDatabaseAfterDelete(RawMaterial r){
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM raw_materials WHERE id = ?");
            ps.setInt(1, r.getId());
            ps.executeUpdate();
        }catch (IllegalStateException exc){
        System.out.println("Modul de test pt Unit Test. Modificare DOAR in Memorie!");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}
