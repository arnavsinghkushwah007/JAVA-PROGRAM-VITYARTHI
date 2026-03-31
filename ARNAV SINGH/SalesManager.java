// SalesManager.java
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesManager {
    
    public static String generateBillNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "BILL-" + sdf.format(new Date());
    }
    
    public static Sale createSale(String customerName, List<SaleItem> items, 
                                double discount, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.connect();
            conn.setAutoCommit(false);
            
            // Calculate totals
            double totalAmount = 0;
            for (SaleItem item : items) {
                totalAmount += item.getTotalPrice();
            }
            
            double taxAmount = totalAmount * 0.10; // 10% tax
            double finalAmount = totalAmount + taxAmount - discount;
            
            String billNumber = generateBillNumber();
            
            // Insert sale record
            String saleSql = "INSERT INTO sales(bill_number, customer_name, total_amount, tax_amount, discount, final_amount, payment_method) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            saleStmt.setString(1, billNumber);
            saleStmt.setString(2, customerName);
            saleStmt.setDouble(3, totalAmount);
            saleStmt.setDouble(4, taxAmount);
            saleStmt.setDouble(5, discount);
            saleStmt.setDouble(6, finalAmount);
            saleStmt.setString(7, paymentMethod);
            saleStmt.executeUpdate();
            
            ResultSet rs = saleStmt.getGeneratedKeys();
            int saleId = 0;
            if (rs.next()) {
                saleId = rs.getInt(1);
            }
            
            // Insert sale items and update product quantities
            String itemSql = "INSERT INTO sale_items(sale_id, product_id, product_name, quantity, unit_price, total_price) VALUES(?,?,?,?,?,?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            
            for (SaleItem item : items) {
                itemStmt.setInt(1, saleId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setString(3, item.getProductName());
                itemStmt.setInt(4, item.getQuantity());
                itemStmt.setDouble(5, item.getUnitPrice());
                itemStmt.setDouble(6, item.getTotalPrice());
                itemStmt.addBatch();
                
                // Update product quantity
                ProductManager.updateProductQuantity(item.getProductId(), -item.getQuantity()); // This can throw SQLException
            }
            
            itemStmt.executeBatch();
            
            // Record income in accounts
            String accountSql = "INSERT INTO accounts(transaction_type, amount, description, category) VALUES(?,?,?,?)";
            PreparedStatement accountStmt = conn.prepareStatement(accountSql);
            accountStmt.setString(1, "income");
            accountStmt.setDouble(2, finalAmount);
            accountStmt.setString(3, "Sale - " + billNumber);
            accountStmt.setString(4, "sales");
            accountStmt.executeUpdate();
            
            conn.commit();
            
            return new Sale(saleId, billNumber, customerName, totalAmount, taxAmount, discount, finalAmount, paymentMethod, new Date());
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error during rollback: " + ex.getMessage());
                }
            }
            System.out.println("Error creating sale: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public static List<Sale> getSalesReport(Date startDate, Date endDate) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date DESC";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getString("bill_number"),
                    rs.getString("customer_name"),
                    rs.getDouble("total_amount"),
                    rs.getDouble("tax_amount"),
                    rs.getDouble("discount"),
                    rs.getDouble("final_amount"),
                    rs.getString("payment_method"),
                    rs.getTimestamp("sale_date")
                );
                sales.add(sale);
            }
        } catch (SQLException e) {
            System.out.println("Error getting sales report: " + e.getMessage());
        }
        return sales;
    }
    
    public static List<SaleItem> getSaleItems(int saleId) {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT * FROM sale_items WHERE sale_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SaleItem item = new SaleItem(
                    rs.getInt("id"),
                    rs.getInt("sale_id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("total_price")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error getting sale items: " + e.getMessage());
        }
        return items;
    }
    
    public static String generateBillReceipt(Sale sale, List<SaleItem> items) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("================================\n");
        receipt.append("          INVOICE\n");
        receipt.append("================================\n");
        receipt.append("Bill No: ").append(sale.getBillNumber()).append("\n");
        receipt.append("Date: ").append(sale.getSaleDate()).append("\n");
        receipt.append("Customer: ").append(sale.getCustomerName()).append("\n");
        receipt.append("--------------------------------\n");
        receipt.append("Items:\n");
        
        for (SaleItem item : items) {
            receipt.append(String.format("%s x %d @ $%.2f = $%.2f\n", 
                item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getTotalPrice()));
        }
        
        receipt.append("--------------------------------\n");
        receipt.append(String.format("Subtotal: $%.2f\n", sale.getTotalAmount()));
        receipt.append(String.format("Tax (10%%): $%.2f\n", sale.getTaxAmount()));
        receipt.append(String.format("Discount: $%.2f\n", sale.getDiscount()));
        receipt.append(String.format("Total: $%.2f\n", sale.getFinalAmount()));
        receipt.append("Payment: ").append(sale.getPaymentMethod()).append("\n");
        receipt.append("================================\n");
        receipt.append("Thank you for your business!\n");
        receipt.append("================================\n");
        
        return receipt.toString();
    }
}

class Sale {
    private int id;
    private String billNumber;
    private String customerName;
    private double totalAmount;
    private double taxAmount;
    private double discount;
    private double finalAmount;
    private String paymentMethod;
    private Date saleDate;
    
    public Sale(int id, String billNumber, String customerName, double totalAmount, 
                double taxAmount, double discount, double finalAmount, String paymentMethod, Date saleDate) {
        this.id = id;
        this.billNumber = billNumber;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.saleDate = saleDate;
    }
    
    // Getters
    public int getId() { return id; }
    public String getBillNumber() { return billNumber; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
    public double getTaxAmount() { return taxAmount; }
    public double getDiscount() { return discount; }
    public double getFinalAmount() { return finalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public Date getSaleDate() { return saleDate; }
}

class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    
    public SaleItem(int id, int saleId, int productId, String productName, 
                   int quantity, double unitPrice, double totalPrice) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
    
    // Getters
    public int getId() { return id; }
    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
}