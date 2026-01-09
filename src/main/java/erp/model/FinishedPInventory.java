package erp.model;
import erp.database.DatabaseManager;

import java.sql.*;
import java.util.*;

/**
 * Clasa utilitara pentru gestiunea stocului de produse finite
 */
public class FinishedPInventory {

    /** Map ce face atribuirea Produs - Cantitate, se refera la stocul de produse existente*/
    private Map<FinishedProduct, Integer> stoc;

    /**  Lista cu toate produsele companiei, indiferent de disponibilitatea lor */
    private List<FinishedProduct> catalogProduse;

    /** Constructor */
    public FinishedPInventory() {
        stoc = new HashMap<>();
        catalogProduse = new ArrayList<>();
    }

    /**
     * Getter pentru stocul de produse
     * @return Map : Produs -> Cantitate
     */
    public Map<FinishedProduct, Integer> getStoc() {
        return stoc;
    }

    /**
     * Getter pentru cantitatea unui produs
     * @param f produsul cerut
     * @return cantitatea produsului
     */
    public int getCantitate(FinishedProduct f) {
        return stoc.getOrDefault(f, 0);
    }

    /**
     * Getter pentru lista tuturor produselor dintr-o companie
     * @return Lista cu obiecte FinishedProduct
     */
    public List<FinishedProduct> getCatalogProduse(){return catalogProduse;}


    /**
     * Metoda pentru cresterea cantitatii unui produs finit
     * @param f produsul finit
     * @param cant cantitatea care se adauga
     */
    public void adaugaProdus(FinishedProduct f, int cant) {
        stoc.put(f, stoc.getOrDefault(f, 0) + cant);
    }

    /**
     * Metoda care scade cantitatea unui produs
     * @param f produsul finit
     * @param cant cantitatea care se scade
     */
    public void scadeProdus(FinishedProduct f, int cant) {
        int curent = stoc.getOrDefault(f, 0);
        if (curent < cant) {
            throw new IllegalArgumentException("Stoc insuficient pentru " + f.getCodProdus());
        }
        stoc.put(f, curent - cant);
        updateDatabase(f);
    }


    /**
     * Metoda care actualizeaza baza de date in cazul unei modificari in legatura cu un produs
     * @param p produsul care a fost modificat
     * @throws SQLException
     * @throws IllegalStateException pentru Unit Tests
     */
    public void updateDatabase(FinishedProduct p){

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE `erp`.`finished_products`\n" +
                    "        SET\n" +
                    "`id` = ?,\n" +
                    "`cod_produs` = ?,\n" +
                    "`tip_produs` =  ?,\n" +
                    "`aroma` = ?,\n" +
                    "`gramaj` = ?,\n" +
                    "`unit` = ?,\n" +
                    "`quantity` = ?\n" +
                    "        WHERE `id` = ?;");

            ps.setInt(1, p.getId());
            ps.setString(2, p.getCodProdus());
            ps.setString(3, p.getTipProdus());
            ps.setString(4, p.getAroma());
            ps.setDouble(5, p.getGramaj());
            ps.setString(6, p.getUnit());
            ps.setDouble(7, stoc.get(p));
            ps.setDouble(8, p.getId());
            ps.executeUpdate();
        }catch (IllegalStateException e)
        {
            System.out.println("Modul Test pt Unit Test. Modificare doar in memorie!");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Metoda care incarca produsele finite din baza de date
     * @throws SQLException
     */
    public void loadFromDatabase(){
        try{
            stoc.clear();
            catalogProduse.clear();
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

                catalogProduse.add(p);
                stoc.put(p, rs.getInt("quantity"));
            }
            rs.close();
            st.close();
            System.out.println("Stoc produse finite incarcat din baza de date.");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //not used
    private void saveToDatabase(FinishedProduct p){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO finished_products (id, cod_produs, tip_produs, aroma, gramaj, unit, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
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

}
