package erp.service;

import erp.database.DatabaseManager;
import erp.model.FinishedProduct;
import erp.model.Recipe;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RecipeBook {
    private Map<FinishedProduct, Recipe> retete = new HashMap<>();

    public void addReteta(FinishedProduct produs, Recipe reteta) {
        retete.put(produs, reteta);
    }

    public Recipe getReteta(FinishedProduct produs) {
        return retete.get(produs);
    }

    public boolean existaReteta(FinishedProduct produs) {
        return retete.containsKey(produs);
    }

    public void afisareToateRetetele() {
        for (Recipe r : retete.values()) {
            System.out.println(r);
        }
    }


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

    public void loadAllRecipes() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM recipes")) {

            while (rs.next()) {
                int recipeId = rs.getInt("id");
                int finishedProductId = rs.getInt("finished_product_id");

                FinishedProduct p = findFinishedProduct(finishedProductId, conn);
                if (p != null) {
                    Recipe r = new Recipe(recipeId, p);
                    r.loadFromDatabase(); // incarca liniile din recipe_lines
                    retete.put(p, r);
                }
            }

            System.out.println("Toate retetele au fost incarcate din baza de date. (" + retete.size() + " retete)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
