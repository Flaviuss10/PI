package erp.service;
import erp.database.DatabaseManager;
import erp.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelarea unei clase utilitare pentru gestiunea comenzilor primite
 */
public class OrderManager {

    /** Clasa singleton, o singura instanta in toata aplicatia */
    private static OrderManager instance;

    /** Variabila care memoreaza daca au fost incarcate comenzile din baza de date */
    private boolean loaded = false;

    /** Lista ce contine comenzile */
    private List<Order> comenzi;

    /** Stocul companiei, Obiect de tip InventoryManager */
    private InventoryManager stoc;

    /** Constructor private */
    private OrderManager(InventoryManager stoc){
        comenzi = new ArrayList<>();
        this.stoc = stoc;
    }

    /**
     * Metoda statica care initializeaza instanta OrderManager
     * @param stoc stocul companiei
     */
    public static void init(InventoryManager stoc){
        if(instance == null)
            instance = new OrderManager(stoc);
    }

    /**
     * Getter pentru instanta curenta
     * @return OrderManager
     */
    public static OrderManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("OrderManager nu a fost inițializat. Apelează OrderManager.init() mai întâi.");
        }
        return instance;
    }

    /**
     * Getter pentru lista cu comenzi
     * @return O lista cu toate comenzile firmei
     */
    public List<Order> getComenzi(){
        return comenzi;
    }

    /**
     * Metoda pentru adaugarea unei comenzi in lista
     * @param o comanda care trebuie adaugata
     */
    public void adaugaComanda(Order o){
        comenzi.add(o);
    }

    /**
     * Metoda care verifica daca compania are disponibilitate pentru a onora o comanda
     * Aceasta metoda verifica pentru fiecare produs din comanda daca exista disponibilitate
     * Se foloseste o lista ce contine linii de comanda (lipsuri), in care se salveaza
     * produsul finit si diferenta de cantitate
     * Daca lista e goala => comanda poate fi onorata
     * @param o comanda
     * @return O lista continand cantitatile produselor care lipsesc
     */
   public List<OrderLine> verificaDisponibilitate(Order o){



        List<OrderLine> produseComanda = o.getProduse();
        List<OrderLine> lipsuri = new ArrayList<>();
        for(OrderLine line : produseComanda){
            FinishedProduct produs = line.getProdus();
            int cantitateComanda = line.getCantitate();

            int cantitateStoc = stoc.getCantitateProdusFinit(produs);

            //exista produs in stoc?
            if(cantitateComanda > cantitateStoc)
                lipsuri.add(new OrderLine(produs, cantitateComanda - cantitateStoc));
        }
        return lipsuri;
   }

    /**
     * Metoda care proceseaza o comanda
     * Este apelata doar daca comanda poate fi onorata
     * Din stocul de produse finite al companiei se scade pentru fiecare
     * produs din comanda, cantitatea ceruta
     * Statusul comenzii este actualizat
     * Baza de date este actualizata
     *  @param o comanda
     */
   public void proceseazaComanda(Order o){
        List<OrderLine> comanda = o.getProduse();
        for(OrderLine line : comanda){
            FinishedProduct produs = line.getProdus();
            int cantitateComanda = line.getCantitate();
            stoc.scadeProdusFinit(produs, cantitateComanda);
        }
        System.out.println("Comanda procesata cu succes: " + o.getClient());
        o.setProcesata(true);
        updateDatabaseAfterProcessing(o);
   }

    /**
     * Metoda pt afisarea produselor lipsa dintr o comanda
     * @param o comanda
     */
   public void showProduseLipsa(Order o){
        List<OrderLine> lipsuri = verificaDisponibilitate(o);
        if(lipsuri.isEmpty())
            System.out.println("Toate produsele sunt disponibile pentru procesare.");
        else{
            System.out.println("Produse insuficiente pentru comanda:");
            for (OrderLine line : lipsuri) {
                System.out.println(line.getCodProdus() + " - lipsesc " + line.getCantitate() + " bucati");
            }
        }
   }

    /**
     * Metoda care incarca toate comenzile din baza de date, in cazul in care
     * nu au fost incarcate (loaded = false)
     * @throws SQLException
     */
   public void loadOrdersFromDatabase(){
        if(loaded)
            return;
        comenzi.clear();

       try {
           Connection conn = DatabaseManager.getConnection();
           Statement st = conn.createStatement();
           ResultSet rs = st.executeQuery("SELECT * FROM orders");

           while (rs.next()) {
               Order o = new Order(rs.getInt("id"), rs.getString("client"));
               o.setProcesata(rs.getBoolean("processed"));
               o.loadFromDatabase();
               comenzi.add(o);
           }


       } catch (SQLException e) {
           e.printStackTrace();
       }
       System.out.println("Toate comenzile au fost incarcate din baza de date.");
       loaded = true;
   }


    /**
     * Metoda care salveaza o comanda in baza de date
     * Se salveaza comanda atat in tabelul orders,
     * dar se actualizeaza si tabelul order_lines cu produsele din comanda
     * @param comanda comanda care trebuie salvata
     */
   public void saveOrderToDatabase(Order comanda){
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO orders (client, processed) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comanda.getClient());
            ps.setBoolean(2, comanda.getProcesata());
            ps.executeUpdate();


            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int orderId = keys.getInt(1);
                comanda.setId(orderId);
            }

            for(OrderLine line : comanda.getProduse()){
                PreparedStatement statement = conn.prepareStatement("INSERT INTO order_lines (order_id, product_id, quantity) VALUES (?, ?, ?)");
                statement.setInt(1,comanda.getId());
                statement.setInt(2, line.getProdus().getId());
                statement.setDouble(3, line.getCantitate());
                statement.executeUpdate();
            }

                System.out.println("Comanda #" + comanda.getId() + " salvata in baza de date.");
        }catch(SQLException e){
            e.printStackTrace();
        }
   }

    /**
     * Metoda care numara cate comenzi exista
     * @return nr comenzi
     */
   public int countOrders() {
        return comenzi.size();
   }

    /**
     * Metoda care numara cate comenzi nefinalizate exista
     * @return nr comenzi neprocesate
     */
   public long countComenziNefinalizate(){
        return comenzi.stream().filter(c -> !c.getProcesata()).count();
   }

    /**
     * Metoda pentru afisarea comenzilor
     */
   public void afisareComenzi(){
        comenzi.stream().forEach(System.out::println);
   }

    /**
     * Metoda pentru stergerea unei comenzi din baza de date
     * Se sterge comanda din lista de comenzi, dar este actualizata si baza de date
     * @param o comanda care trebuie stearsa
     */
   public void deleteOrder(Order o){
        comenzi.remove(o);
        updateDatabaseAfterDelete(o);
   }

    /**
     * Metoda care actualizeaza baza de date in urma finalizarii unei comenzi
     * Se seteaza campul processed cu true
     * @param o Comanda care s-a procesat
     * @throws  SQLException
     */
   void updateDatabaseAfterProcessing(Order o){
       try{
           Connection conn = DatabaseManager.getConnection();
           PreparedStatement ps = conn.prepareStatement("UPDATE orders SET processed = true WHERE id = ?");
           ps.setInt(1, o.getId());
           ps.executeUpdate();
       }catch(SQLException e){
           e.printStackTrace();
       }
   }

    /**
     * Metoda care actualizeaza baza de date in urma stergerii unei comenzi
     * Se sterg liniile corespunzatoare comenzii din order_lines
     * Apoi se sterge linia din orders cu id-ul comenzii date
     * @param o Comanda care trebuie stearsa
     * @throws SQLException
     */
   void updateDatabaseAfterDelete(Order o){
       try{
           Connection conn = DatabaseManager.getConnection();
           PreparedStatement ps2 = conn.prepareStatement("DELETE FROM order_lines WHERE order_id = ?");
           ps2.setInt(1, o.getId());
           ps2.executeUpdate();
           PreparedStatement ps = conn.prepareStatement("DELETE FROM orders WHERE id = ?" );
           ps.setInt(1, o.getId());
           ps.executeUpdate();
       }catch(SQLException e){
           e.printStackTrace();
       }
   }
}
