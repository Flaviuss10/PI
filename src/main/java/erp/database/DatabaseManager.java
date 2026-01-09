package erp.database;

import java.sql.*;

/**
 *  Clasa responsabila cu gestionarea conexiunii la baza de date MySQL
 *  Implementeaza metode statice pentru a oferi acces doar la o singura instanta
 *  a conexiunii in toata aplicatia
 */
public class DatabaseManager {
    // adresa de conectare la bd
    private static final String URL = "jdbc:mysql://localhost:3306/erp";
    private static final String USER = "root";
    private static final String PASSWORD = "Flavius10!";

    // Obiectul care reprezinta conexiunea propriu-zisa
    private static Connection connection;

    /**
     * Metoda care realizeaza conexiunea la baza de date folosind inform de mai sus
     * Aceasta este apelata o singura data, la pornirea aplicatiei
     */
    public static void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexiune MySQL reusita!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda care realizeaza inchiderea conexiunii la baza de date
     * Se apeleaza la iesirea din aplicatie
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter pentru instanta curenta a conexiunii
     * Metoda este apelată în special de alte clase care implementează metode
     * ce executa interogari asupra bazei de date
     * @return obiectul de tip Connection
     * @throws IllegalStateException daca nu s-a realizat o conexiune catre bd
     */
    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Conexiunea la baza de date nu este activă!");
        }
        return connection;
    }

}
