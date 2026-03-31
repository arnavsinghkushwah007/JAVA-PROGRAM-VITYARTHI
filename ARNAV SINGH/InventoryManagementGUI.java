/// main.java
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class InventoryManagementGUI {
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
            new InventoryManagementGUI().createAndShowGUI();
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
        
        addFormField(registerForm, "Username:", new JTextField(20), gbc, row++);
        addFormField(registerForm, "Password:", new JPasswordField(20), gbc, row++);
        addFormField(registerForm, "Email:", new JTextField(20), gbc, row++);
        addFormField(registerForm, "Security Question:", new JTextField(20), gbc, row++);
        addFormField(registerForm, "Security Answer:", new JPasswordField(20), gbc, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JButton registerButton = createStyledButton("Register", SUCCESS_COLOR);
        registerForm.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            // Get all components and extract data
            Component[] components = registerForm.getComponents();
            String username = ((JTextField) components[1]).getText();
            String password = new String(((JPasswordField) components[3]).getPassword());
            String email = ((JTextField) components[5]).getText();
            String securityQuestion = ((JTextField) components[7]).getText();
            String securityAnswer = new String(((JPasswordField) components[9]).getPassword());

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
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    private void showProductManagementPanel() {
        cardLayout.show(mainPanel, "PRODUCTS");
    }

    private void showSalesPanel() {
        cardLayout.show(mainPanel, "SALES");
    }

    private void showAccountsPanel() {
        cardLayout.show(mainPanel, "ACCOUNTS");
    }

    private void showReportsPanel() {
        cardLayout.show(mainPanel, "REPORTS");
    }

    private void showLowStockAlert() {
        // Implementation for low stock alert
        showMessage("Showing low stock products...", "Low Stock Alert", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showQuickStats() {
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