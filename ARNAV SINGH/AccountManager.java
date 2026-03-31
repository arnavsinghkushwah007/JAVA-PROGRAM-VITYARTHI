// AccountManager.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    
    public static boolean addTransaction(String transactionType, double amount, 
                                       String description, String category) {
        String sql = "INSERT INTO accounts(transaction_type, amount, description, category) VALUES(?,?,?,?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transactionType);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.setString(4, category);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
            return false;
        }
    }
    
    public static List<AccountTransaction> getTransactions(String type, String period) {
        List<AccountTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE 1=1";
        
        if (type != null && !type.equals("all")) {
            sql += " AND transaction_type = '" + type + "'";
        }
        
        if (period != null) {
            switch (period) {
                case "today":
                    sql += " AND DATE(transaction_date) = DATE('now')";
                    break;
                case "week":
                    sql += " AND transaction_date >= DATE('now', '-7 days')";
                    break;
                case "month":
                    sql += " AND transaction_date >= DATE('now', '-1 month')";
                    break;
            }
        }
        
        sql += " ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                AccountTransaction transaction = new AccountTransaction(
                    rs.getInt("id"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getTimestamp("transaction_date")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error getting transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    public static double getBalance() {
        String sql = "SELECT " +
                    "SUM(CASE WHEN transaction_type = 'income' THEN amount ELSE 0 END) - " +
                    "SUM(CASE WHEN transaction_type = 'expense' THEN amount ELSE 0 END) as balance " +
                    "FROM accounts";
        
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("Error getting balance: " + e.getMessage());
        }
        return 0.0;
    }
    
    public static double getTotalIncome(String period) {
        return getTransactionTotal("income", period);
    }
    
    public static double getTotalExpense(String period) {
        return getTransactionTotal("expense", period);
    }
    
    private static double getTransactionTotal(String type, String period) {
        String sql = "SELECT SUM(amount) as total FROM accounts WHERE transaction_type = ?";
        
        if (period != null) {
            switch (period) {
                case "today":
                    sql += " AND DATE(transaction_date) = DATE('now')";
                    break;
                case "week":
                    sql += " AND transaction_date >= DATE('now', '-7 days')";
                    break;
                case "month":
                    sql += " AND transaction_date >= DATE('now', '-1 month')";
                    break;
            }
        }
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error getting " + type + " total: " + e.getMessage());
        }
        return 0.0;
    }
}

class AccountTransaction {
    private int id;
    private String transactionType;
    private double amount;
    private String description;
    private String category;
    private Timestamp transactionDate;
    
    public AccountTransaction(int id, String transactionType, double amount, 
                            String description, String category, Timestamp transactionDate) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.transactionDate = transactionDate;
    }
    
    // Getters
    public int getId() { return id; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Timestamp getTransactionDate() { return transactionDate; }
}