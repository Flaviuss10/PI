package erp.model;
import erp.database.DatabaseManager;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

/**
 * Clasa care modeleaza o reteta pentru un anumit tip de produs finit
 * Este o mapare Produs - Linii de reteta
 */
public class Recipe {

    /** id-ul unic al retetei */
    private int id;

    /** Produsul finit */
    private FinishedProduct produs;

    /** O lista ce contine linii de reteta (Obiecte de tip RecipeLine) */
    List<RecipeLine> retetar;

    /**
     * Constructorul retetei
     */
    public Recipe(int id, FinishedProduct produsFinit) {
        this.id = id;
        this.produs = produsFinit;
        this.retetar = new ArrayList<>();
    }

    /**
     * Getter pentru retetar
     * @return Lista cu obiecte RecipeLine
     */
    public List<RecipeLine> getRetetar() {
        return retetar;
    }

    /**
     * Getter pentru Id
     * @return numar intreg - id-ul retetei
     */
    public int getId() {
        return id;
    }

    /**
     * Metoda pentru a adauga un ingredient in retetar
     * Se creeaza un obiect de tip RecipeLine, care este adaugat in lista
     * @param materie materia prima
     * @param cantitatePerUnitate cantitatea
     */
    public void adaugaIngredient(RawMaterial materie, double cantitatePerUnitate) {
        retetar.add(new RecipeLine(materie, cantitatePerUnitate));
    }

    /**
     * Metoda care cauta in baza de date o materie prima
     * @param id id-ul materiei prime care se cauta
     * @param conn obiect de tip Connection, conexiunea la baza de date
     * @return obiect RawMaterial, reprezentand materia prima cu id-ul cautat
     * @throws SQLException
     */
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

    /**
     * Metoda care incarca liniile retetarului din baza de date pentru produsul finit curent
     * @throws SQLException
     */
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

    /**
     * Metoda petru salvarea unei retete in baza de date
     * Se verifica daca exista reteta pentru produsul curent (exista => id != 0)
     * Daca nu exista, se insereaza in tabelul recipes o linie noua (cream reteta pt produsul finit)
     * Daca exista, stergem toate liniile din recipe_lines corespunzatoare retetei pt produsul finit curent
     * La final, inseram toate liniile din retetar in tabelul recipe_lines
     * Astfel, indiferent de existenta retetei, se salveaza modificarile
     * @throws SQLException
     */
    public void saveToDatabase() {
        try {
            Connection conn = DatabaseManager.getConnection();


            if (this.id == 0) {
                // NU exista reteta pt produs
                PreparedStatement ps = conn.prepareStatement("INSERT INTO recipes (product_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, produs.getId());
                ps.executeUpdate();

                ResultSet key = ps.getGeneratedKeys();
                if (key.next()) {
                    this.id = key.getInt(1); // Salvam ID-ul generat de MySQL
                }
            } else {
                PreparedStatement psDelete = conn.prepareStatement("DELETE FROM recipe_lines WHERE recipe_id = ?");
                psDelete.setInt(1, this.id);
                psDelete.executeUpdate();
            }


            String sqlLine = "INSERT INTO recipe_lines (recipe_id, material_id, amount) VALUES (?, ?, ?)";
            PreparedStatement psLine = conn.prepareStatement(sqlLine);

            for (RecipeLine line : retetar) {
                psLine.setInt(1, this.id);
                psLine.setInt(2, line.getIngredient().getId());
                psLine.setDouble(3, line.getCantitatePerUnitate());
                psLine.executeUpdate();
            }

            System.out.println("Rețeta salvată/actualizată cu succes pentru: " + produs.getCodProdus());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Suprascrierea metodei de afisare
     * @return String - descrierea unei retete
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Reteta pentru: " + produs.getCodProdus() + "\n");
        for (RecipeLine linie : retetar) {
            sb.append("   ").append(linie).append("\n");
        }
        return sb.toString();
    }

}
