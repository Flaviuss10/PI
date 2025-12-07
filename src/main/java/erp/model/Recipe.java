package erp.model;
import erp.database.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
public class Recipe {
    private int id;
    private FinishedProduct produs;
    List<RecipeLine> retetar;

    public Recipe(int id, FinishedProduct produsFinit) {
        this.id = id;
        this.produs = produsFinit;
        this.retetar = new ArrayList<>();
    }

    public void adaugaIngredient(RawMaterial materie, double cantitatePerUnitate) {
        retetar.add(new RecipeLine(materie, cantitatePerUnitate));
    }

    public FinishedProduct getProdus() {
        return produs;
    }

    public List<RecipeLine> getRetetar() {
        return retetar;
    }

    private RawMaterial findRawMaterialById(int id, Connection conn){
        RawMaterial materiePrima = null;
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM raw_materials WHERE id = " + id);
            while(rs.next()){

                materiePrima = new RawMaterial(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("unit")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materiePrima;
    }
    public void loadFromDatabase(){
        try{
            Connection conn = DatabaseManager.getConnection();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM recipe_lines WHERE recipe_id =  " + id);
            while(rs.next()){
                int raw_material_id = rs.getInt("material_id");
                RawMaterial materiePrima = findRawMaterialById(raw_material_id, conn);
                double qty = rs.getDouble("amount");
                RecipeLine line = new RecipeLine(materiePrima, qty);
                retetar.add(line);
            }


        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void saveToDatabase(){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO recipes (product_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, produs.getId());
            ps.executeUpdate();

            ResultSet key = ps.getGeneratedKeys();

            if(key.next())
                id = key.getInt(1);
            
            //adaugam ingredientele Retetei in recipe_lines
            for(RecipeLine line : retetar){
                PreparedStatement ps2 = conn.prepareStatement("INSERT INTO wrecipe_lines (recipe_id, material_id, amount) VALUES (?, ?, ?)");
                ps2.setInt(1, id);
                ps2.setInt(2, line.getIngredient().getId());
                ps2.setDouble(3, line.getCantitatePerUnitate());
                ps2.executeUpdate();
            }

            System.out.println("Reteta salvata in baza de date: " + produs.getCodProdus());


        }catch(SQLException e){
            e.printStackTrace();
        }


    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Reteta pentru: " + produs.getCodProdus() + "\n");
        for (RecipeLine linie : retetar) {
            sb.append("   ").append(linie).append("\n");
        }
        return sb.toString();
    }

}
