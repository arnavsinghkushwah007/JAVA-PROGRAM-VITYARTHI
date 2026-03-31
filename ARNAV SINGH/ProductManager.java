// ProductManager.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    
    public static boolean addProduct(String name, String description, String category, 
                                   double price, double cost, int quantity, int minStockLevel) {
        String sql = "INSERT INTO products(name, description, category, price, cost, quantity, min_stock_level) VALUES(?,?,?,?,?,?,?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            pstmt.setDouble(4, price);
            pstmt.setDouble(5, cost);
            pstmt.setInt(6, quantity);
            pstmt.setInt(7, minStockLevel);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean updateProduct(int id, String name, String description, String category, 
                                      double price, double cost, int quantity, int minStockLevel) {
        String sql = "UPDATE products SET name=?, description=?, category=?, price=?, cost=?, quantity=?, min_stock_level=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            pstmt.setDouble(4, price);
            pstmt.setDouble(5, cost);
            pstmt.setInt(6, quantity);
            pstmt.setInt(7, minStockLevel);
            pstmt.setInt(8, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }
    
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getDouble("cost"),
                    rs.getInt("quantity"),
                    rs.getInt("min_stock_level")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error getting products: " + e.getMessage());
        }
        return products;
    }
    
    public static Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getDouble("cost"),
                    rs.getInt("quantity"),
                    rs.getInt("min_stock_level")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting product: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean updateProductQuantity(int productId, int quantityChange) {
        String sql = "UPDATE products SET quantity = quantity + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, productId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating product quantity: " + e.getMessage());
            return false;
        }
    }
    
    public static List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= min_stock_level ORDER BY quantity ASC";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getDouble("cost"),
                    rs.getInt("quantity"),
                    rs.getInt("min_stock_level")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error getting low stock products: " + e.getMessage());
        }
        return products;
    }
}

class Product {
    private int id;
    private String name;
    private String description;
    private String category;
    private double price;
    private double cost;
    private int quantity;
    private int minStockLevel;
    
    public Product(int id, String name, String description, String category, 
                   double price, double cost, int quantity, int minStockLevel) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public double getCost() { return cost; }
    public int getQuantity() { return quantity; }
    public int getMinStockLevel() { return minStockLevel; }
    
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setCost(double cost) { this.cost = cost; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
    
    @Override
    public String toString() {
        return String.format("%s (Stock: %d, Price: $%.2f)", name, quantity, price);
    }
}