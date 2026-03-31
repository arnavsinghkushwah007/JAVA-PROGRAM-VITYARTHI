# Inventory Management System

A fully featured desktop application built in Java that helps small and medium-sized businesses manage their products, process sales, track finances, and generate reports — all from a clean, offline-capable graphical interface.

---

## Table of Contents

- [What This Project Does](#what-this-project-does)
- [Why This Project Exists](#why-this-project-exists)
- [Features](#features)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setting Up the Project](#setting-up-the-project)
- [How to Run](#how-to-run)
- [How to Use the Application](#how-to-use-the-application)
- [Database Schema](#database-schema)
- [Security](#security)
- [Known Limitations](#known-limitations)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

## What This Project Does

This Inventory Management System is a Java desktop application that gives a business owner or store manager a single tool to handle everything related to running a shop or warehouse. Instead of juggling spreadsheets, paper bills, and separate accounting notebooks, this application brings it all together in one place.

When you open the application, you are greeted with a login screen. Once authenticated, you land on a dashboard that gives you a quick overview of your business — total products, low stock alerts, today's sales, and monthly revenue. From there you can navigate to any of the core modules:

- You can add new products to your catalog, set their selling price, cost price, stock quantity, and a minimum stock threshold that triggers an alert when inventory runs low.
- You can process a new sale by selecting products, entering customer details, choosing a payment method, and generating a bill with automatic tax calculation and optional discount.
- You can record financial transactions — both income and expenses — and view a running financial summary showing total income, total expenses, net profit, and current balance.
- You can view reports for sales history, inventory status, and financial performance, with date-range filtering.
- You can manage your user account, register new users, and recover a forgotten password using a security question system.

Everything is stored locally in a SQLite database file called `inventory.db` that is created automatically the first time you run the application. No internet connection is required. No external server needs to be running.

---

## Why This Project Exists

Small businesses — particularly retail shops, pharmacies, grocery stores, and small warehouses — often cannot afford enterprise inventory software. They end up using paper records or basic spreadsheets, which leads to stock discrepancies, missed reorder points, untracked expenses, and no clear picture of profitability.

This project was built to solve that problem with a free, open-source, offline desktop tool that anyone can run on a standard Windows, macOS, or Linux machine with Java installed. The goal was to make something practical, not just academic — a tool that a real shop owner could actually use.

---

## Features

**User Authentication**
- User registration with username, password, email, and a custom security question
- Secure login with SHA-256 hashed password verification
- Forgot password flow using security question and answer verification
- Temporary password generation and reset

**Product Management**
- Add new products with name, description, category, selling price, cost price, stock quantity, and minimum stock level
- Edit existing product details
- Delete products from the catalog
- View all products in a sortable table
- Low stock alert that highlights products at or below their minimum threshold

**Sales and Billing**
- Create new sales by selecting products and specifying quantities
- Enter customer name and choose payment method (Cash, Card, or Online)
- Automatic subtotal calculation, 10% tax application, and optional discount
- Generate a formatted bill receipt with bill number, date, itemized list, and totals
- View full sales history with date-range filtering
- Each sale automatically decrements product stock and records income in the accounts ledger

**Account Management**
- Record income and expense transactions with description and category
- Filter transactions by type (Income / Expense) and time period (Today / This Week / This Month / All)
- Financial summary dashboard showing total income, total expenses, net profit, and current balance

**Reports and Analytics**
- Sales report with date filtering
- Inventory report showing stock levels and low-stock items
- Financial report with income vs expense breakdown

**Dashboard**
- Quick stats panel showing total products, low stock count, today's sales, and monthly revenue
- One-click navigation to all modules

---

## Project Structure

```
Arnav singh kushwah/
│
├── Main.java                   # Application entry point and primary GUI class
├── InventoryManagementGUI.java # Alternate GUI entry point (same structure as Main.java)
├── DatabaseConnection.java     # SQLite connection management and schema initialization
├── UserAuth.java               # User registration, login, password hashing, and reset
├── ProductManager.java         # Product CRUD operations and low-stock queries
├── SalesManager.java           # Sale creation, bill generation, and sales history
├── AccountManager.java         # Financial transaction recording and balance calculation
│
├── lib/                        # Place your SQLite JDBC driver JAR here
│
├── README.md                   # This file
└── Project_Report.md           # Full academic project report
```

---

## Technology Stack

| Component        | Technology                        |
|------------------|-----------------------------------|
| Language         | Java SE (Java 8 or higher)        |
| GUI Framework    | Java Swing                        |
| Database         | SQLite (local file-based)         |
| DB Connectivity  | JDBC (Java Database Connectivity) |
| Password Security| SHA-256 via Java MessageDigest    |
| Build Tool       | Manual javac / any Java IDE       |

---

## Prerequisites

Before you can run this project, make sure you have the following installed on your machine.

**Java Development Kit (JDK) — version 8 or higher**

You can check if Java is already installed by opening a terminal or command prompt and running:

```
java -version
```

If you see a version number printed, Java is installed. If not, download and install the JDK from https://www.oracle.com/java/technologies/downloads/ or install OpenJDK from https://adoptium.net/

**SQLite JDBC Driver**

This project uses SQLite as its database. To connect Java to SQLite, you need the SQLite JDBC driver JAR file. Download it from:

https://github.com/xerial/sqlite-jdbc/releases

Download the file named something like `sqlite-jdbc-3.x.x.x.jar`. Place this JAR file inside the `lib/` folder in the project directory. If the `lib/` folder does not exist, create it.

**A Java IDE (Recommended but Optional)**

You can use any of the following IDEs to open and run the project easily:
- IntelliJ IDEA (recommended) — https://www.jetbrains.com/idea/
- Eclipse — https://www.eclipse.org/
- NetBeans — https://netbeans.apache.org/
- VS Code with the Java Extension Pack

You can also compile and run from the command line without any IDE, which is explained below.

---

## Setting Up the Project

Follow these steps carefully from top to bottom. If you do each step in order, the application will run without issues.

**Step 1 — Download or Clone the Project**

If you have Git installed, open a terminal and run:

```
git clone https://github.com/your-username/inventory-management-system.git
```

Or simply download the ZIP file from GitHub, extract it, and navigate into the project folder.

**Step 2 — Download the SQLite JDBC Driver**

Go to https://github.com/xerial/sqlite-jdbc/releases and download the latest `sqlite-jdbc-x.x.x.x.jar` file. Copy or move this file into the `lib/` folder inside the project. If the `lib/` folder does not exist, create it manually.

Your folder structure should look like this after this step:

```
Arnav singh kushwah/
├── lib/
│   └── sqlite-jdbc-3.47.1.0.jar   ← your downloaded JAR goes here
├── Main.java
├── DatabaseConnection.java
...
```

**Step 3 — Open in an IDE (Recommended Path)**

If you are using IntelliJ IDEA:
1. Open IntelliJ IDEA and click "Open"
2. Navigate to the project folder and click OK
3. Right-click on the `lib/` folder, choose "Add as Library"
4. IntelliJ will now recognize the SQLite JDBC driver
5. Open `Main.java` and click the green Run button next to the `main` method

If you are using Eclipse:
1. Open Eclipse and go to File → Import → Existing Projects into Workspace
2. Browse to the project folder and click Finish
3. Right-click the project → Build Path → Configure Build Path
4. Click "Add JARs", navigate to `lib/`, select the SQLite JDBC JAR, and click OK
5. Right-click `Main.java` → Run As → Java Application

**Step 4 — Compile and Run from the Command Line (Alternative Path)**

If you prefer not to use an IDE, open a terminal, navigate to the project folder, and run the following commands.

On Windows (Command Prompt):

```
cd "Arnav singh kushwah"
javac -cp ".;lib\sqlite-jdbc-3.47.1.0.jar" *.java
java -cp ".;lib\sqlite-jdbc-3.47.1.0.jar" Main
```

On macOS or Linux (Terminal):

```
cd "Arnav singh kushwah"
javac -cp ".:lib/sqlite-jdbc-3.47.1.0.jar" *.java
java -cp ".:lib/sqlite-jdbc-3.47.1.0.jar" Main
```

Replace `sqlite-jdbc-3.47.1.0.jar` with the exact filename of the JAR you downloaded.

**Step 5 — First Launch**

The first time the application runs, it will automatically create a file called `inventory.db` in the same directory. This is your SQLite database. All your data — users, products, sales, transactions — will be stored in this single file. You do not need to create it manually or run any SQL setup scripts. The application handles all of that on startup through the `DatabaseConnection.initializeDatabase()` method.

---

## How to Use the Application

**Creating Your First Account**

When the application opens, you will see the login screen. Since this is a fresh installation, no users exist yet. Click the "Register" button to create your first account.

Fill in the following fields:
- Username — choose a unique username you will use to log in
- Password — choose a strong password
- Email — enter your email address
- Security Question — write a question only you know the answer to, for example "What is the name of your first pet?"
- Security Answer — type the answer to your security question

Click "Register". If successful, you will be redirected back to the login screen. Now log in with the username and password you just created.

**Navigating the Dashboard**

After logging in, you will see the main dashboard. At the top is a welcome message with your username and a Logout button. Below that are six large navigation buttons:

- Product Management — manage your product catalog
- Sales and Billing — create new sales and view history
- Account Management — record and view financial transactions
- Reports and Analytics — view business reports
- Low Stock Alert — see products running low
- Quick Stats — view a summary of key numbers

At the bottom of the dashboard is a stats bar showing total products, low stock count, today's sales, and monthly revenue.

**Adding Your First Product**

Click "Product Management" from the dashboard. Click the "Add Product" button in the toolbar. A form will appear asking for:

- Name — the product name, for example "Wireless Mouse"
- Description — a brief description of the product
- Category — the product category, for example "Electronics"
- Price — the selling price (what you charge customers)
- Cost — the cost price (what you paid to acquire it)
- Stock Quantity — how many units you currently have
- Minimum Stock Level — the quantity below which you want to be alerted

Fill in the details and click Save. The product will appear in the products table. Repeat this for all your products.

**Processing a Sale**

Click "Sales and Billing" from the dashboard. You will see two tabs: "New Sale" and "Sales History".

On the New Sale tab:
1. Enter the customer's name in the Customer Name field
2. Select the payment method from the dropdown (Cash, Card, or Online)
3. In the product selection table, check the products the customer is buying and enter quantities
4. The bill summary at the bottom will automatically calculate the subtotal, add 10% tax, and show the total
5. If you want to apply a discount, enter the discount amount in the Discount field
6. Click "Generate Bill" to complete the sale

The system will automatically reduce the stock quantity of each sold product and record the sale amount as income in the accounts ledger. A formatted receipt will be displayed showing the bill number, date, itemized list, and final total.

To view past sales, click the "Sales History" tab. You can filter by date range using the From and To date fields.

**Recording a Financial Transaction**

Click "Account Management" from the dashboard. You will see three tabs.

On the "Add Transaction" tab, you can record any income or expense that is not directly tied to a sale — for example, paying rent, buying office supplies, or receiving a refund. Select the transaction type (Income or Expense), enter the amount, a description, and a category, then click "Add Transaction".

On the "View Transactions" tab, you can see all recorded transactions. Use the Type and Period filters to narrow down what you see.

On the "Financial Summary" tab, you will see four summary cards showing your total income, total expenses, net profit, and current balance — all calculated live from the database.

**Checking Low Stock**

Click "Low Stock Alert" from the dashboard. This will show you a list of all products where the current stock quantity is at or below the minimum stock level you set when adding the product. Use this list to know what needs to be reordered.

**Recovering a Forgotten Password**

On the login screen, click "Forgot Password". Enter your username and click "Get Security Question". Your security question will appear. Type the answer and click "Reset Password". The system will generate a temporary password and display it on screen. Use that temporary password to log in, then change it to something you prefer.

---

## Database Schema

The application uses five tables stored in a local SQLite file called `inventory.db`.

**users table** — stores all registered user accounts

| Column            | Type     | Description                              |
|-------------------|----------|------------------------------------------|
| id                | INTEGER  | Auto-incremented primary key             |
| name              | TEXT     | Unique username                          |
| password          | TEXT     | SHA-256 hashed password                  |
| email             | TEXT     | User email address                       |
| security_question | TEXT     | Custom security question for password reset |
| security_answer   | TEXT     | SHA-256 hashed security answer           |
| created_at        | DATETIME | Account creation timestamp               |

**products table** — stores the product catalog

| Column          | Type     | Description                              |
|-----------------|----------|------------------------------------------|
| id              | INTEGER  | Auto-incremented primary key             |
| name            | TEXT     | Product name                             |
| description     | TEXT     | Product description                      |
| category        | TEXT     | Product category                         |
| price           | REAL     | Selling price                            |
| cost            | REAL     | Cost/purchase price                      |
| quantity        | INTEGER  | Current stock quantity                   |
| min_stock_level | INTEGER  | Minimum threshold for low-stock alert    |
| created_at      | DATETIME | Record creation timestamp                |
| updated_at      | DATETIME | Last update timestamp                    |

**sales table** — stores bill-level sale records

| Column         | Type     | Description                              |
|----------------|----------|------------------------------------------|
| id             | INTEGER  | Auto-incremented primary key             |
| bill_number    | TEXT     | Unique bill identifier (timestamp-based) |
| customer_name  | TEXT     | Customer name                            |
| total_amount   | REAL     | Subtotal before tax and discount         |
| tax_amount     | REAL     | Tax amount (10% of subtotal)             |
| discount       | REAL     | Discount applied                         |
| final_amount   | REAL     | Final amount charged                     |
| payment_method | TEXT     | Cash, Card, or Online                    |
| sale_date      | DATETIME | Date and time of sale                    |

**sale_items table** — stores individual line items for each sale

| Column       | Type    | Description                              |
|--------------|---------|------------------------------------------|
| id           | INTEGER | Auto-incremented primary key             |
| sale_id      | INTEGER | Foreign key referencing sales.id         |
| product_id   | INTEGER | Foreign key referencing products.id      |
| product_name | TEXT    | Product name at time of sale             |
| quantity     | INTEGER | Quantity sold                            |
| unit_price   | REAL    | Price per unit at time of sale           |
| total_price  | REAL    | quantity × unit_price                    |

**accounts table** — stores all financial transactions

| Column           | Type     | Description                              |
|------------------|----------|------------------------------------------|
| id               | INTEGER  | Auto-incremented primary key             |
| transaction_type | TEXT     | "income" or "expense"                    |
| amount           | REAL     | Transaction amount                       |
| description      | TEXT     | Description of the transaction           |
| category         | TEXT     | Category label                           |
| transaction_date | DATETIME | Date and time of transaction             |

---

## Security

This application takes basic but important security measures:

**Password Hashing** — Passwords are never stored in plaintext. When you register or change your password, it is passed through the SHA-256 cryptographic hash function using Java's built-in `MessageDigest` class. Only the resulting hash is stored in the database. When you log in, your entered password is hashed again and compared to the stored hash. This means even if someone opens the `inventory.db` file directly, they cannot read any passwords.

**Security Answer Hashing** — The same SHA-256 hashing is applied to security answers, so those are also protected in the database.

**Prepared Statements** — Most database queries use `PreparedStatement` with parameterized inputs, which prevents SQL injection attacks by separating SQL code from user-supplied data.

**Transaction Rollback** — The sale creation process uses database transactions. If any part of the sale fails (inserting the sale record, inserting line items, updating stock), the entire operation is rolled back so the database never ends up in a partially written, inconsistent state.

---

## Known Limitations

- The dashboard stats panel currently shows placeholder values rather than live database queries. This is a known gap that would be addressed in the next development iteration.
- The `AccountManager.getTransactions()` method builds part of its SQL query using string concatenation for the type filter. While the values come from a controlled dropdown (limiting injection risk), this should be replaced with fully parameterized queries.
- Two GUI entry point files exist — `Main.java` and `InventoryManagementGUI.java` — that contain nearly identical code. This is a result of iterative development. Only `Main.java` needs to be used as the entry point; `InventoryManagementGUI.java` is a legacy file.
- There is no role-based access control. All logged-in users have full access to all features. A future version would distinguish between admin and staff roles.
- The reports module currently shows placeholder panels. Full chart-based reporting with JFreeChart integration is planned.
- There is no data export functionality yet. Exporting bills to PDF or sales data to CSV would be a valuable addition.

---

## Future Improvements

- Connect the dashboard stats panel to live database queries so numbers update in real time
- Add PDF bill generation so receipts can be printed or saved
- Add CSV/Excel export for sales and financial reports
- Implement role-based access control (Admin vs Staff)
- Add data visualization charts (bar charts, pie charts) using JFreeChart
- Add a supplier management module to track purchase orders
- Add a customer management module to track repeat customers and purchase history
- Replace all string-concatenated SQL with fully parameterized prepared statements
- Add input validation throughout the UI (numeric-only fields, required field checks, email format validation)
- Consolidate `Main.java` and `InventoryManagementGUI.java` into a single entry point
- Add a settings screen where users can configure tax rate, currency symbol, and business name

---

## Author

**Arnav Singh Kushwah**
Registration No: 24BAI10416
B.Tech — Computer Science and Engineering
VIT University
Academic Year: 2026

