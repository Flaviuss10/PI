package erp.service;

import erp.database.DatabaseManager;
import erp.model.FinishedProduct;
import erp.model.Recipe;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Clasa care modeleaza o "carte cu retete"
 * Aceasta contine toate retetele produselor finite ale companiei
 */
public class RecipeBook {

    /** Un Map care face atribuirea Produs - reteta */
    private Map<FinishedProduct, Recipe> retete = new HashMap<>();

    /**
     * Getter pentru reteta unui produs dat
     * @param produs produsul pentru care se cere reteta
     * @return reteta produsului dat, obiect Recipe
     */
    public Recipe getReteta(FinishedProduct produs) {
        return retete.get(produs);
    }

    /**
     * Metoda pentru adaugarea unei retete in cartea de retete
     * @param produs produsul pentru care se adauga reteta
     * @param reteta reteta propriu-zisa
     */
    public void addReteta(FinishedProduct produs, Recipe reteta) {
        retete.put(produs, reteta);
    }

    /**
     * Metoda care verifica daca exista o reteta pentru un produs finit dat
     * @param produs produsul finit pentru care se cauta reteta
     * @return True daca exista reteta, False altfel
     */
    public boolean existaReteta(FinishedProduct produs) {
        return retete.containsKey(produs);
    }

    /**
     * Metoda care cauta un produs finit in baza de date
     * @param id id-ul produsului
     * @param conn conexiunea la baza de date
     * @return obiect FinishedProduct
     * @throws SQLException
     */
    private FinishedProduct findFinishedProduct(int id, Connection conn) {
        FinishedProduct product = null;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM finished_products WHERE id = " + id)) {

            if (rs.next()) {
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
     * Metoda care incarca toate retetele din baza de date
     * Prima data se cauta id-ul retetei si id-ul produsului finit pentru care exista reteta
     * Se creeaza un obiect FinishedProduct care reprezinta produsul cu id-ul gasit mai sus
     * Se creeaza o reteta pentru FinishedProduct, folosind liniile din recipe_lines ce contin recipe_id = id-ul retetei
     * @throws SQLException
     */
    public void loadAllRecipes() {
        try {
             Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM recipes");

            while (rs.next()) {
                int recipeId = rs.getInt("id");
                int finishedProductId = rs.getInt("product_id");

                FinishedProduct p = findFinishedProduct(finishedProductId, conn);
                if (p != null) {
                    Recipe r = new Recipe(recipeId, p);
                    r.loadFromDatabase(); // incarca liniile din recipe_lines
                    retete.put(p, r);
                }
            }
            System.out.println("Toate retetele au fost incarcate din baza de date. (" + retete.size() + " retete)");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
