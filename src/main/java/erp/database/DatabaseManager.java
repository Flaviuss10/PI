package erp.database;

import java.sql.*;


public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/erp";
    private static final String USER = "root";
    private static final String PASSWORD = "Flavius10!";

    private static Connection connection;


    public static void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexiune MySQL reusita!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Conexiunea la baza de date nu este activă!");
        }
        return connection;
    }

}
