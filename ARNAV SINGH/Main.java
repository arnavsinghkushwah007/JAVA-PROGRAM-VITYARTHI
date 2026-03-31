import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Main class for the Inventory Management System.
 * This class contains the GUI and the main entry point.
 */
public class Main {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentUser = "";
    private boolean isLoggedIn = false;

    // Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color LIGHT_BG = new Color(236, 240, 241);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection.initializeDatabase();
            new Main().createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        mainFrame = new JFrame("Inventory Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create different panels
        createLoginPanel();
        createRegisterPanel();
        createForgotPasswordPanel();
        createMainDashboardPanel();
        createProductManagementPanel();
        createSalesPanel();
        createAccountsPanel();
        createReportsPanel();

        mainFrame.add(mainPanel);
        showLoginPanel();
        mainFrame.setVisible(true);
    }

    private void createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("INVENTORY MANAGEMENT SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Center Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(LIGHT_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel loginForm = new JPanel(new GridBagLayout());
        loginForm.setBackground(Color.WHITE);
        loginForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel loginTitle = new JLabel("USER LOGIN", JLabel.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 20));
        loginTitle.setForeground(PRIMARY_COLOR);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginForm.add(loginTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        loginForm.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        loginForm.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        loginForm.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        loginForm.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = createStyledButton("Login", SUCCESS_COLOR);
        JButton registerButton = createStyledButton("Register", SECONDARY_COLOR);
        JButton forgotButton = createStyledButton("Forgot Password", WARNING_COLOR);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Please enter both username and password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (UserAuth.login(username, password)) {
                currentUser = username;
                isLoggedIn = true;
                showMainDashboard();
                showMessage("Login successful! Welcome, " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> showRegisterPanel());
        forgotButton.addActionListener(e -> showForgotPasswordPanel());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(forgotButton);

        gbc.gridy = 3;
        loginForm.add(buttonPanel, gbc);

        centerPanel.add(loginForm);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(panel, "LOGIN");
    }

    private void createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JButton backButton = new JButton("← Back to Login");
        backButton.addActionListener(e -> showLoginPanel());
        styleButton(backButton, Color.WHITE, PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("USER REGISTRATION", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Registration Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LIGHT_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel registerForm = new JPanel(new GridBagLayout());
        registerForm.setBackground(Color.WHITE);
        registerForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SECONDARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        int row = 0;
        
        JTextField usernameFieldReg = new JTextField(20);
        JPasswordField passwordFieldReg = new JPasswordField(20);
        JTextField emailFieldReg = new JTextField(20);
        JTextField securityQuestionFieldReg = new JTextField(20);
        JPasswordField securityAnswerFieldReg = new JPasswordField(20);

        addFormField(registerForm, "Username:", usernameFieldReg, gbc, row++);
        addFormField(registerForm, "Password:", passwordFieldReg, gbc, row++);
        addFormField(registerForm, "Email:", emailFieldReg, gbc, row++);
        addFormField(registerForm, "Security Question:", securityQuestionFieldReg, gbc, row++);
        addFormField(registerForm, "Security Answer:", securityAnswerFieldReg, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton registerButton = createStyledButton("Register", SUCCESS_COLOR);
        registerForm.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String username = usernameFieldReg.getText();
            String password = new String(passwordFieldReg.getPassword());
            String email = emailFieldReg.getText();
            String securityQuestion = securityQuestionFieldReg.getText();
            String securityAnswer = new String(securityAnswerFieldReg.getPassword());

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || 
                securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
                showMessage("Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (UserAuth.registerUser(username, password, email, securityQuestion, securityAnswer)) {
                showMessage("Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                showLoginPanel();
            } else {
                showMessage("Registration failed! Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(registerForm);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(panel, "REGISTER");
    }

    private void createForgotPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JButton backButton = new JButton("← Back to Login");
        backButton.addActionListener(e -> showLoginPanel());
        styleButton(backButton, Color.WHITE, PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("FORGOT PASSWORD", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Forgot Password Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LIGHT_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel forgotForm = new JPanel(new GridBagLayout());
        forgotForm.setBackground(Color.WHITE);
        forgotForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(WARNING_COLOR, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JTextField usernameField = new JTextField(20);
        JLabel securityQuestionLabel = new JLabel("Security Question will appear here");
        JTextField securityAnswerField = new JTextField(20);

        int row = 0;
        addFormField(forgotForm, "Username:", usernameField, gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        forgotForm.add(new JLabel("Security Question:"), gbc);
        gbc.gridx = 1;
        securityQuestionLabel.setForeground(SECONDARY_COLOR);
        forgotForm.add(securityQuestionLabel, gbc);
        row++;

        addFormField(forgotForm, "Security Answer:", securityAnswerField, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton getQuestionButton = createStyledButton("Get Security Question", SECONDARY_COLOR);
        JButton resetButton = createStyledButton("Reset Password", SUCCESS_COLOR);

        getQuestionButton.addActionListener(e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                showMessage("Please enter username!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String question = UserAuth.getSecurityQuestion(username);
            if (question != null) {
                securityQuestionLabel.setText(question);
            } else {
                showMessage("Username not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        resetButton.addActionListener(e -> {
            String username = usernameField.getText();
            String answer = securityAnswerField.getText();

            if (username.isEmpty() || answer.isEmpty()) {
                showMessage("Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (UserAuth.verifySecurityAnswer(username, answer)) {
                String tempPassword = UserAuth.generateTempPassword();
                if (UserAuth.resetPassword(username, tempPassword)) {
                    showMessage("Password reset successful! Your temporary password is: " + 
                               tempPassword + "\nPlease change it after login.", 
                               "Success", JOptionPane.INFORMATION_MESSAGE);
                    showLoginPanel();
                } else {
                    showMessage("Password reset failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessage("Incorrect security answer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(getQuestionButton);
        buttonPanel.add(resetButton);
        forgotForm.add(buttonPanel, gbc);

        formPanel.add(forgotForm);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(panel, "FORGOT_PASSWORD");
    }

    private void createMainDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 70));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser, JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        styleButton(logoutButton, Color.WHITE, ACCENT_COLOR);

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Navigation Panel
        JPanel navPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        navPanel.setBackground(LIGHT_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttons = {
            "Product Management", "Sales & Billing", "Account Management",
            "Reports & Analytics", "Low Stock Alert", "Quick Stats"
        };

        Color[] colors = {SECONDARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, PRIMARY_COLOR, ACCENT_COLOR, new Color(155, 89, 182)};

        for (int i = 0; i < buttons.length; i++) {
            JButton button = new JButton("<html><center>" + buttons[i] + "</center></html>");
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(colors[i]);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final int index = i;
            button.addActionListener(e -> {
                switch (index) {
                    case 0: showProductManagementPanel(); break;
                    case 1: showSalesPanel(); break;
                    case 2: showAccountsPanel(); break;
                    case 3: showReportsPanel(); break;
                    case 4: showLowStockAlert(); break;
                    case 5: showQuickStats(); break;
                }
            });

            navPanel.add(button);
        }

        // Quick Stats Panel
        JPanel statsPanel = createStatsPanel();

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(navPanel, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "DASHBOARD");
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Sample stats - in real app, fetch from database
        String[] stats = {"Total Products: 45", "Low Stock: 3", "Today's Sales: $1,234", "Monthly Revenue: $12,345"};
        Color[] colors = {PRIMARY_COLOR, ACCENT_COLOR, SUCCESS_COLOR, WARNING_COLOR};

        for (int i = 0; i < stats.length; i++) {
            JLabel statLabel = new JLabel("<html><center>" + stats[i] + "</center></html>", JLabel.CENTER);
            statLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statLabel.setForeground(Color.WHITE);
            statLabel.setOpaque(true);
            statLabel.setBackground(colors[i]);
            statLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
            panel.add(statLabel);
        }

        return panel;
    }

    private void createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header with back button
        JPanel headerPanel = createHeaderPanel("Product Management", "DASHBOARD");

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(LIGHT_BG);

        JButton addButton = createStyledButton("Add Product", SUCCESS_COLOR);
        JButton refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        JButton lowStockButton = createStyledButton("Low Stock Alert", WARNING_COLOR);

        toolbar.add(addButton);
        toolbar.add(refreshButton);
        toolbar.add(lowStockButton);

        // Products Table
        String[] columns = {"ID", "Name", "Category", "Price", "Cost", "Stock", "Min Stock", "Actions"};
        Object[][] data = {}; // Will be populated from database

        JTable productsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(productsTable);

        contentPanel.add(toolbar, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(panel, "PRODUCTS");
    }

    private void createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        JPanel headerPanel = createHeaderPanel("Sales & Billing", "DASHBOARD");
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LIGHT_BG);
        
        // New Sale Tab
        tabbedPane.addTab("New Sale", createNewSalePanel());
        
        // Sales History Tab
        tabbedPane.addTab("Sales History", createSalesHistoryPanel());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(panel, "SALES");
    }

    private JPanel createNewSalePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Customer details
        JPanel customerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        customerPanel.add(new JLabel("Customer Name:"));
        JTextField customerNameField = new JTextField();
        customerPanel.add(customerNameField);
        customerPanel.add(new JLabel("Payment Method:"));
        JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Cash", "Card", "Online"});
        customerPanel.add(paymentCombo);

        // Products selection
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBackground(Color.WHITE);
        productsPanel.setBorder(BorderFactory.createTitledBorder("Select Products"));

        // This would be populated with actual products from database
        String[] productColumns = {"Select", "Product", "Price", "Stock"};
        Object[][] productData = {};
        JTable productsSelectionTable = new JTable(productData, productColumns);
        productsPanel.add(new JScrollPane(productsSelectionTable), BorderLayout.CENTER);

        // Bill summary
        JPanel billPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        billPanel.setBackground(Color.WHITE);
        billPanel.setBorder(BorderFactory.createTitledBorder("Bill Summary"));

        billPanel.add(new JLabel("Subtotal:"));
        billPanel.add(new JLabel("$0.00"));
        billPanel.add(new JLabel("Tax (10%):"));
        billPanel.add(new JLabel("$0.00"));
        billPanel.add(new JLabel("Discount:"));
        JTextField discountField = new JTextField("0");
        billPanel.add(discountField);
        billPanel.add(new JLabel("Total:"));
        billPanel.add(new JLabel("$0.00"));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(LIGHT_BG);
        JButton generateBillButton = createStyledButton("Generate Bill", SUCCESS_COLOR);
        JButton clearButton = createStyledButton("Clear", ACCENT_COLOR);
        actionPanel.add(generateBillButton);
        actionPanel.add(clearButton);

        panel.add(customerPanel, BorderLayout.NORTH);
        panel.add(productsPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(billPanel, BorderLayout.CENTER);
        southPanel.add(actionPanel, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSalesHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(LIGHT_BG);

        filterPanel.add(new JLabel("From:"));
        JTextField fromDate = new JTextField(10);
        filterPanel.add(fromDate);
        filterPanel.add(new JLabel("To:"));
        JTextField toDate = new JTextField(10);
        filterPanel.add(toDate);
        JButton filterButton = createStyledButton("Filter", SECONDARY_COLOR);
        filterPanel.add(filterButton);

        // Sales table
        String[] columns = {"Bill No", "Date", "Customer", "Amount", "Payment", "Actions"};
        Object[][] data = {};
        JTable salesTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        JPanel headerPanel = createHeaderPanel("Account Management", "DASHBOARD");
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LIGHT_BG);
        
        tabbedPane.addTab("Add Transaction", createAddTransactionPanel());
        tabbedPane.addTab("View Transactions", createViewTransactionsPanel());
        tabbedPane.addTab("Financial Summary", createFinancialSummaryPanel());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(panel, "ACCOUNTS");
    }

    private JPanel createAddTransactionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        JTextField amountField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField categoryField = new JTextField(20);

        int row = 0;
        addFormField(formPanel, "Transaction Type:", typeCombo, gbc, row++);
        addFormField(formPanel, "Amount:", amountField, gbc, row++);
        addFormField(formPanel, "Description:", descriptionField, gbc, row++);
        addFormField(formPanel, "Category:", categoryField, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton addButton = createStyledButton("Add Transaction", SUCCESS_COLOR);
        formPanel.add(addButton, gbc);

        panel.add(formPanel);
        return panel;
    }

    private JPanel createViewTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(LIGHT_BG);

        filterPanel.add(new JLabel("Type:"));
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"All", "Income", "Expense"});
        filterPanel.add(typeFilter);
        filterPanel.add(new JLabel("Period:"));
        JComboBox<String> periodFilter = new JComboBox<>(new String[]{"All", "Today", "This Week", "This Month"});
        filterPanel.add(periodFilter);
        JButton filterButton = createStyledButton("Apply Filter", SECONDARY_COLOR);
        filterPanel.add(filterButton);

        // Transactions table
        String[] columns = {"Date", "Type", "Amount", "Description", "Category"};
        Object[][] data = {};
        JTable transactionsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFinancialSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        summaryPanel.setBackground(LIGHT_BG);

        String[] summaries = {
            "Total Income: $12,345.67",
            "Total Expense: $8,901.23", 
            "Net Profit: $3,444.44",
            "Current Balance: $15,678.90"
        };

        Color[] colors = {SUCCESS_COLOR, ACCENT_COLOR, new Color(46, 204, 113), PRIMARY_COLOR};

        for (int i = 0; i < summaries.length; i++) {
            JLabel summaryLabel = new JLabel("<html><center><h2>" + summaries[i] + "</h2></center></html>", JLabel.CENTER);
            summaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
            summaryLabel.setForeground(Color.WHITE);
            summaryLabel.setOpaque(true);
            summaryLabel.setBackground(colors[i]);
            summaryLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
            summaryPanel.add(summaryLabel);
        }

        panel.add(summaryPanel, BorderLayout.CENTER);
        return panel;
    }

    private void createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        JPanel headerPanel = createHeaderPanel("Reports & Analytics", "DASHBOARD");
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(LIGHT_BG);
        
        tabbedPane.addTab("Sales Report", createSalesReportPanel());
        tabbedPane.addTab("Inventory Report", createInventoryReportPanel());
        tabbedPane.addTab("Financial Report", createFinancialReportPanel());
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(panel, "REPORTS");
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Would contain sales charts and detailed reports
        JLabel placeholder = new JLabel("<html><center><h1>Sales Report</h1><p>Charts and detailed sales analysis would appear here</p></center></html>", JLabel.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInventoryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Would contain inventory analysis
        JLabel placeholder = new JLabel("<html><center><h1>Inventory Report</h1><p>Stock levels, turnover rates, and inventory analysis would appear here</p></center></html>", JLabel.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFinancialReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Would contain financial charts
        JLabel placeholder = new JLabel("<html><center><h1>Financial Report</h1><p>Income statements, balance sheets, and financial charts would appear here</p></center></html>", JLabel.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);

        return panel;
    }

    // Helper methods
    private JPanel createHeaderPanel(String title, String backTo) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 70));

        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            switch (backTo) {
                case "DASHBOARD": showMainDashboard(); break;
                case "LOGIN": showLoginPanel(); break;
            }
        });
        styleButton(backButton, Color.WHITE, PRIMARY_COLOR);

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        styleButton(button, Color.WHITE, bgColor);
        return button;
    }

    private void styleButton(JButton button, Color fgColor, Color bgColor) {
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addFormField(JPanel panel, String label, Component field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(mainFrame, message, title, messageType);
    }

    // Navigation methods
    private void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void showRegisterPanel() {
        cardLayout.show(mainPanel, "REGISTER");
    }

    private void showForgotPasswordPanel() {
        cardLayout.show(mainPanel, "FORGOT_PASSWORD");
    }

    private void showMainDashboard() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        // This is a good place to update dynamic content
        // For example, update the welcome label
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    private void showProductManagementPanel() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        cardLayout.show(mainPanel, "PRODUCTS");
    }

    private void showSalesPanel() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        cardLayout.show(mainPanel, "SALES");
    }

    private void showAccountsPanel() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        cardLayout.show(mainPanel, "ACCOUNTS");
    }

    private void showReportsPanel() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        cardLayout.show(mainPanel, "REPORTS");
    }

    private void showLowStockAlert() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        // Implementation for low stock alert
        showMessage("Showing low stock products...", "Low Stock Alert", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showQuickStats() {
        if (!isLoggedIn) {
            showLoginPanel();
            return;
        }
        // Implementation for quick stats
        showMessage("Showing quick statistics...", "Quick Stats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        isLoggedIn = false;
        currentUser = "";
        showLoginPanel();
        showMessage("Logged out successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * Handles database connection and initialization.
 */
class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:inventory.db";
    
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
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
            if (conn != null) {
                for (String sql : createTables) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                    }
                }
                System.out.println("Database initialized successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
    
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

/**
 * Manages user authentication tasks like registration, login, and password reset.
 */
class UserAuth {
    
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
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
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
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
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
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("security_question");
                }
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

/**
 * Manages product-related database operations.
 */
class ProductManager {
    
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
            try (ResultSet rs = pstmt.executeQuery()) {
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

/**
 * Data class for a Product.
 */
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

/**
 * Manages sales, billing, and reporting.
 */
class SalesManager {
    
    public static String generateBillNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "BILL-" + sdf.format(new java.util.Date());
    }
    
    public static Sale createSale(String customerName, List<SaleItem> items, 
                                double discount, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.connect();
            conn.setAutoCommit(false);
            
            double totalAmount = items.stream().mapToDouble(SaleItem::getTotalPrice).sum();
            double taxAmount = totalAmount * 0.10; // 10% tax
            double finalAmount = totalAmount + taxAmount - discount;
            String billNumber = generateBillNumber();
            
            // Insert sale record
            String saleSql = "INSERT INTO sales(bill_number, customer_name, total_amount, tax_amount, discount, final_amount, payment_method) VALUES(?,?,?,?,?,?,?)";
            int saleId;
            try (PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setString(1, billNumber);
                saleStmt.setString(2, customerName);
                saleStmt.setDouble(3, totalAmount);
                saleStmt.setDouble(4, taxAmount);
                saleStmt.setDouble(5, discount);
                saleStmt.setDouble(6, finalAmount);
                saleStmt.setString(7, paymentMethod);
                saleStmt.executeUpdate();
                
                try (ResultSet rs = saleStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        saleId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating sale failed, no ID obtained.");
                    }
                }
            }
            
            // Insert sale items and update product quantities
            String itemSql = "INSERT INTO sale_items(sale_id, product_id, product_name, quantity, unit_price, total_price) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                for (SaleItem item : items) {
                    itemStmt.setInt(1, saleId);
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setString(3, item.getProductName());
                    itemStmt.setInt(4, item.getQuantity());
                    itemStmt.setDouble(5, item.getUnitPrice());
                    itemStmt.setDouble(6, item.getTotalPrice());
                    itemStmt.addBatch();
                    
                    ProductManager.updateProductQuantity(item.getProductId(), -item.getQuantity());
                }
                itemStmt.executeBatch();
            }
            
            // Record income in accounts
            AccountManager.addTransaction("income", finalAmount, "Sale - " + billNumber, "sales");
            
            conn.commit();
            
            return new Sale(saleId, billNumber, customerName, totalAmount, taxAmount, discount, finalAmount, paymentMethod, new java.util.Date());
            
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
    
    public static List<Sale> getSalesReport(java.util.Date startDate, java.util.Date endDate) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date DESC";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(new Sale(
                        rs.getInt("id"),
                        rs.getString("bill_number"),
                        rs.getString("customer_name"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("tax_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("sale_date")
                    ));
                }
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
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new SaleItem(
                        rs.getInt("id"),
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total_price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting sale items: " + e.getMessage());
        }
        return items;
    }
}

/**
 * Data class for a Sale.
 */
class Sale {
    private int id;
    private String billNumber;
    private String customerName;
    private double totalAmount;
    private double taxAmount;
    private double discount;
    private double finalAmount;
    private String paymentMethod;
    private java.util.Date saleDate;
    
    public Sale(int id, String billNumber, String customerName, double totalAmount, 
                double taxAmount, double discount, double finalAmount, String paymentMethod, java.util.Date saleDate) {
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
    public java.util.Date getSaleDate() { return saleDate; }
}

/**
 * Data class for an item within a Sale.
 */
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

/**
 * Manages financial transactions.
 */
class AccountManager {
    
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
        StringBuilder sql = new StringBuilder("SELECT * FROM accounts WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.equalsIgnoreCase("all")) {
            sql.append(" AND transaction_type = ?");
            params.add(type);
        }
        
        if (period != null) {
            switch (period.toLowerCase()) {
                case "today":
                    sql.append(" AND DATE(transaction_date) = DATE('now')");
                    break;
                case "week":
                    sql.append(" AND transaction_date >= DATE('now', '-7 days')");
                    break;
                case "month":
                    sql.append(" AND transaction_date >= DATE('now', '-1 month')");
                    break;
            }
        }
        
        sql.append(" ORDER BY transaction_date DESC");
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new AccountTransaction(
                        rs.getInt("id"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getTimestamp("transaction_date")
                    ));
                }
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
}

/**
 * Data class for an Account Transaction.
 */
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