package erp.service;
import erp.database.DatabaseManager;
import erp.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {

    private static OrderManager instance;

    private boolean loaded = false;

    private List<Order> comenzi;
    private InventoryManager stoc;

    private OrderManager(InventoryManager stoc){
        comenzi = new ArrayList<>();
        this.stoc = stoc;
    }

    public static void init(InventoryManager stoc){
        if(instance == null)
            instance = new OrderManager(stoc);
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("OrderManager nu a fost inițializat. Apelează OrderManager.init() mai întâi.");
        }
        return instance;
    }


    public void adaugaComanda(Order o){
        comenzi.add(o);
    }

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

   public void proceseazaComanda(Order o){
        List<OrderLine> comanda = o.getProduse();
        for(OrderLine line : comanda){
            FinishedProduct produs = line.getProdus();
            int cantitateComanda = line.getCantitate();
            stoc.scadeProdusFinit(produs, cantitateComanda);
        }
        System.out.println("Comanda procesata cu succes: " + o.getClient());
        o.setProcesata(true);
   }

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

   // conexiunea cu db
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

   public int countOrders() {
        return comenzi.size();
   }

   public long countComenziNefinalizate(){
        return comenzi.stream().filter(c -> !c.getProcesata()).count();
   }

   public void afisareComenzi(){
        comenzi.stream().forEach(System.out::println);
   }

       public List<Order> getComenzi(){
            return comenzi;
       }
}
