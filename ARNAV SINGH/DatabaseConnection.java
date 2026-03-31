// DatabaseConnection.java
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:inventory.db";
    
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }
    
    public static void initializeDatabase() {
        String[] createTables = {
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT UNIQUE NOT NULL, " +
            "password TEXT NOT NULL, " +
            "email TEXT NOT NULL, " +
            "security_question TEXT NOT NULL, " +
            "security_answer TEXT NOT NULL, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)",

            "CREATE TABLE IF NOT EXISTS products (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "description TEXT, " +
            "category TEXT, " +
            "price REAL NOT NULL, " +
            "cost REAL NOT NULL, " +
            "quantity INTEGER NOT NULL, " +
            "min_stock_level INTEGER DEFAULT 0, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)",

            "CREATE TABLE IF NOT EXISTS sales (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "bill_number TEXT UNIQUE NOT NULL, " +
            "customer_name TEXT, " +
            "total_amount REAL NOT NULL, " +
            "tax_amount REAL NOT NULL, " +
            "discount REAL DEFAULT 0, " +
            "final_amount REAL NOT NULL, " +
            "payment_method TEXT, " +
            "sale_date DATETIME DEFAULT CURRENT_TIMESTAMP)",

            "CREATE TABLE IF NOT EXISTS sale_items (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "sale_id INTEGER, " +
            "product_id INTEGER, " +
            "product_name TEXT NOT NULL, " +
            "quantity INTEGER NOT NULL, " +
            "unit_price REAL NOT NULL, " +
            "total_price REAL NOT NULL, " +
            "FOREIGN KEY (sale_id) REFERENCES sales (id), " +
            "FOREIGN KEY (product_id) REFERENCES products (id))",

            "CREATE TABLE IF NOT EXISTS accounts (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "transaction_type TEXT NOT NULL, " +
            "amount REAL NOT NULL, " +
            "description TEXT, " +
            "category TEXT, " +
            "transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection conn = connect()) {
            for (String sql : createTables) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
    
    // Add this method to your existing DatabaseConnection class
    public static void testConnection() {
        try (Connection conn = connect()) {
            if (conn != null) {
                System.out.println("Database connection test successful!");
            } else {
                System.out.println("Failed to make database connection!");
            }
        } catch (SQLException e) {
            System.out.println("Connection test failed: " + e.getMessage());
        }
    }
}