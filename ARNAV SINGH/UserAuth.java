// UserAuth.java
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class UserAuth {
    
    public static boolean registerUser(String name, String password, String email, 
                                     String securityQuestion, String securityAnswer) {
        String hashedPassword = hashPassword(password);
        String hashedAnswer = hashPassword(securityAnswer);
        
        String sql = "INSERT INTO users(name, password, email, security_question, security_answer) VALUES(?,?,?,?,?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setString(4, securityQuestion);
            pstmt.setString(5, hashedAnswer);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean login(String name, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean verifySecurityAnswer(String name, String securityAnswer) {
        String hashedAnswer = hashPassword(securityAnswer);
        String sql = "SELECT * FROM users WHERE name = ? AND security_answer = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, hashedAnswer);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error verifying security answer: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean resetPassword(String name, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE users SET password = ? WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, name);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }
    
    public static String getSecurityQuestion(String name) {
        String sql = "SELECT security_question FROM users WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("security_question");
            }
        } catch (SQLException e) {
            System.out.println("Error getting security question: " + e.getMessage());
        }
        return null;
    }
    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}