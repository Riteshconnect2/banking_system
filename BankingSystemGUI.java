import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


// Transaction class to represent individual transactions
class Transaction {
    private String type;
    private double amount;
    private Date timestamp;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = new Date();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%s: $%.2f", type, amount);
    }
}


// Account class to represent bank accounts
class Account {
    private int accountNumber;
    private String name;
    private double balance;
    private Stack<Transaction> transactions;

    public Account(int accountNumber, String name, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = initialDeposit;
        this.transactions = new Stack<>();
        this.transactions.push(new Transaction("Initial", initialDeposit));
    }

    // Getters and setters
    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public Stack<Transaction> getTransactions() { return transactions; }

    public void deposit(double amount) {
        balance += amount;
        transactions.push(new Transaction("Deposit", amount));
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        transactions.push(new Transaction("Withdraw", amount));
        return true;
    }

    public boolean undoLastTransaction() {
        if (transactions.size() <= 1) { // Can't undo initial deposit
            return false;
        }
        Transaction lastTransaction = transactions.pop();
        if (lastTransaction.getType().equals("Deposit")) {
            balance -= lastTransaction.getAmount();
        } else if (lastTransaction.getType().equals("Withdraw")) {
            balance += lastTransaction.getAmount();
        }
        return true;
    }
}


// Bank Management System class
class BankManager {
    private List<Account> accounts;

    public BankManager() {
        accounts = new ArrayList<>();
    }

    public void addAccount(int accountNumber, String name, double initialDeposit) {
        accounts.add(new Account(accountNumber, name, initialDeposit));
    }

    public Account findAccount(int accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }

    public boolean deleteAccount(int accountNumber) {
        Account account = findAccount(accountNumber);
        if (account != null) {
            accounts.remove(account);
            return true;
        }
        return false;
    }
}


// Main GUI Application with dark theme
public class BankingSystemGUI extends JFrame {
    private BankManager bankManager;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Dark theme colors inspired by Perplexity dark theme
    private final Color PRIMARY_DARK = new Color(18, 18, 18);        // Dark almost black for main background
    private final Color SECONDARY_DARK = new Color(50, 50, 50);      // Dark gray for panels
    private final Color ACCENT_BLUE = new Color(100, 149, 237);      // Cornflower blue for buttons and highlights
    private final Color SUCCESS_GREEN = new Color(76, 175, 80);      // Green for success actions
    private final Color WARNING_ORANGE = new Color(255, 167, 38);    // Orange for warnings
    private final Color ERROR_RED = new Color(244, 67, 54);          // Red for errors
    private final Color TEXT_LIGHT = new Color(100, 100, 100);       // Light gray for text
    @SuppressWarnings("unused")
    private final Color TEXT_DIM = new Color(100, 100, 100);         // Dim gray for secondary text
    private final Color BUTTON_BG = ACCENT_BLUE;                     // Buttons background
    @SuppressWarnings("unused")
    private final Color BUTTON_HOVER = ACCENT_BLUE.darker();         // Buttons hover

    public BankingSystemGUI() {
        bankManager = new BankManager();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("SecureBank - Banking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createMainMenuPanel();
        createAddAccountPanel();
        createViewAccountsPanel();
        createDepositPanel();
        createWithdrawPanel();
        createTransactionHistoryPanel();
        createUndoTransactionPanel();
        createDeleteAccountPanel();

        add(mainPanel);
        cardLayout.show(mainPanel, "MainMenu");

        setVisible(true);
    }


    private void createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(SECONDARY_DARK);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("SecureBank Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        buttonPanel.setBackground(PRIMARY_DARK);
        buttonPanel.setBorder(new EmptyBorder(40, 100, 40, 100));

        String[] buttonTexts = {
            "Add Account", "View All Accounts",
            "Deposit Money", "Withdraw Money",
            "Transaction History", "Undo Transaction",
            "Delete Account", "Exit System"
        };

        String[] cardNames = {
            "AddAccount", "ViewAccounts",
            "Deposit", "Withdraw",
            "TransactionHistory", "UndoTransaction",
            "DeleteAccount", "Exit"
        };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = createStyledButton(buttonTexts[i], BUTTON_BG);
            final String cardName = cardNames[i];
            button.addActionListener(e -> {
                if (cardName.equals("Exit")) {
                    System.exit(0);
                } else {
                    cardLayout.show(mainPanel, cardName);
                }
            });
            buttonPanel.add(button);
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(panel, "MainMenu");
    }


    private void createAddAccountPanel() {
        JPanel contentPanel = createFormPanel("Add New Account");

        JTextField accountNumberField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField initialDepositField = new JTextField(15);

        contentPanel.add(createFieldPanel("Account Number:", accountNumberField));
        contentPanel.add(createFieldPanel("Account Holder Name:", nameField));
        contentPanel.add(createFieldPanel("Initial Deposit ($):", initialDepositField));

        JButton addButton = createStyledButton("Add Account", SUCCESS_GREEN);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);

        addButton.addActionListener(e -> {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                String name = nameField.getText().trim();
                double initialDeposit = Double.parseDouble(initialDepositField.getText());

                if (name.isEmpty()) {
                    showMessage("Please enter account holder name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (initialDeposit < 0) {
                    showMessage("Initial deposit cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (bankManager.findAccount(accountNumber) != null) {
                    showMessage("Account number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                bankManager.addAccount(accountNumber, name, initialDeposit);
                showMessage("Account added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                accountNumberField.setText("");
                nameField.setText("");
                initialDepositField.setText("");

            } catch (NumberFormatException ex) {
                showMessage("Please enter valid numbers for account number and initial deposit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(PRIMARY_DARK);
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel.getParent(), "AddAccount");
    }


    private void createViewAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(SECONDARY_DARK);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("All Accounts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(titleLabel);

        String[] columnNames = {"Account Number", "Account Holder", "Balance ($)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(ACCENT_BLUE);
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setBackground(SECONDARY_DARK);
        table.setForeground(TEXT_LIGHT);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(SECONDARY_DARK);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(PRIMARY_DARK);

        JButton refreshButton = createStyledButton("Refresh", SUCCESS_GREEN);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);

        refreshButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            for (Account account : bankManager.getAllAccounts()) {
                Object[] row = {
                    account.getAccountNumber(),
                    account.getName(),
                    String.format("%.2f", account.getBalance())
                };
                tableModel.addRow(row);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        bottomPanel.add(refreshButton);
        bottomPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "ViewAccounts");
    }


    private void createDepositPanel() {
        JPanel contentPanel = createFormPanel("Deposit Money");

        JTextField accountNumberField = new JTextField(15);
        JTextField amountField = new JTextField(15);

        contentPanel.add(createFieldPanel("Account Number:", accountNumberField));
        contentPanel.add(createFieldPanel("Deposit Amount ($):", amountField));

        JButton depositButton = createStyledButton("Deposit", SUCCESS_GREEN);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);

        depositButton.addActionListener(e -> {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                double amount = Double.parseDouble(amountField.getText());

                if (amount <= 0) {
                    showMessage("Deposit amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Account account = bankManager.findAccount(accountNumber);
                if (account == null) {
                    showMessage("Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                account.deposit(amount);
                showMessage(String.format("Deposit successful!\nNew Balance: $%.2f", account.getBalance()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                accountNumberField.setText("");
                amountField.setText("");

            } catch (NumberFormatException ex) {
                showMessage("Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(PRIMARY_DARK);
        buttonPanel.add(depositButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel.getParent(), "Deposit");
    }


    private void createWithdrawPanel() {
        JPanel contentPanel = createFormPanel("Withdraw Money");

        JTextField accountNumberField = new JTextField(15);
        JTextField amountField = new JTextField(15);

        contentPanel.add(createFieldPanel("Account Number:", accountNumberField));
        contentPanel.add(createFieldPanel("Withdrawal Amount ($):", amountField));

        JButton withdrawButton = createStyledButton("Withdraw", WARNING_ORANGE);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);

        withdrawButton.addActionListener(e -> {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                double amount = Double.parseDouble(amountField.getText());

                if (amount <= 0) {
                    showMessage("Withdrawal amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Account account = bankManager.findAccount(accountNumber);
                if (account == null) {
                    showMessage("Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (account.withdraw(amount)) {
                    showMessage(String.format("Withdrawal successful!\nNew Balance: $%.2f", account.getBalance()),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    accountNumberField.setText("");
                    amountField.setText("");
                } else {
                    showMessage("Insufficient funds!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                showMessage("Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(PRIMARY_DARK);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel.getParent(), "Withdraw");
    }


    private void createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(SECONDARY_DARK);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(titleLabel);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(SECONDARY_DARK);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        accountLabel.setForeground(TEXT_LIGHT);
        JTextField accountNumberField = new JTextField(10);
        accountNumberField.setBackground(PRIMARY_DARK);
        accountNumberField.setForeground(TEXT_LIGHT);
        JButton viewButton = createStyledButton("View History", SUCCESS_GREEN);

        inputPanel.add(accountLabel);
        inputPanel.add(accountNumberField);
        inputPanel.add(viewButton);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> transactionList = new JList<>(listModel);
        transactionList.setFont(new Font("Arial", Font.PLAIN, 14));
        transactionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionList.setBackground(SECONDARY_DARK);
        transactionList.setForeground(TEXT_LIGHT);

        JScrollPane scrollPane = new JScrollPane(transactionList);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(SECONDARY_DARK);

        viewButton.addActionListener(e -> {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                Account account = bankManager.findAccount(accountNumber);

                if (account == null) {
                    showMessage("Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                listModel.clear();
                listModel.addElement("Transaction History for: " + account.getName());
                listModel.addElement("Account Number: " + account.getAccountNumber());
                listModel.addElement("Current Balance: $" + String.format("%.2f", account.getBalance()));
                listModel.addElement("-------------------------------------------");

                Stack<Transaction> transactions = account.getTransactions();
                if (transactions.isEmpty()) {
                    listModel.addElement("No transactions found.");
                } else {
                    for (int i = transactions.size() - 1; i >= 0; i--) {
                        Transaction transaction = transactions.get(i);
                        listModel.addElement(transaction.toString());
                    }
                }

            } catch (NumberFormatException ex) {
                showMessage("Please enter a valid account number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(PRIMARY_DARK);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        bottomPanel.add(backButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "TransactionHistory");
    }


    private void createUndoTransactionPanel() {
        JPanel contentPanel = createFormPanel("Undo Last Transaction");

        JTextField accountNumberField = new JTextField(15);
        accountNumberField.setBackground(PRIMARY_DARK);
        accountNumberField.setForeground(TEXT_LIGHT);

        contentPanel.add(createFieldPanel("Account Number:", accountNumberField));

        JButton undoButton = createStyledButton("Undo Transaction", WARNING_ORANGE);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);

        undoButton.addActionListener(e -> {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                Account account = bankManager.findAccount(accountNumber);

                if (account == null) {
                    showMessage("Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (account.undoLastTransaction()) {
                    showMessage(String.format("Transaction undone successfully!\nNew Balance: $%.2f", account.getBalance()),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    accountNumberField.setText("");
                } else {
                    showMessage("Nothing to undo (initial deposit cannot be undone).", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                showMessage("Please enter a valid account number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(PRIMARY_DARK);
        buttonPanel.add(undoButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel.getParent(), "UndoTransaction");
    }


    private void createDeleteAccountPanel() {
        JPanel contentPanel = createFormPanel("Delete Account");

        JTextField accountNumberField = new JTextField(15);
        accountNumberField.setBackground(PRIMARY_DARK);
        accountNumberField.setForeground(TEXT_LIGHT);

        contentPanel.add(createFieldPanel("Account Number:", accountNumberField));

        JButton deleteButton = createStyledButton("Delete Account", ERROR_RED);
        JButton backButton = createStyledButton("Back to Menu", BUTTON_BG);

        deleteButton.addActionListener(e -> {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                Account account = bankManager.findAccount(accountNumber);

                if (account == null) {
                    showMessage("Account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int result = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete account " + accountNumber + " (" + account.getName() + ")?\n" +
                                "This action cannot be undone!",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    bankManager.deleteAccount(accountNumber);
                    showMessage("Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    accountNumberField.setText("");
                }

            } catch (NumberFormatException ex) {
                showMessage("Please enter a valid account number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(PRIMARY_DARK);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        mainPanel.add(contentPanel.getParent(), "DeleteAccount");
    }


    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PRIMARY_DARK);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(SECONDARY_DARK);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(titleLabel);

        panel.add(headerPanel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(SECONDARY_DARK);
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        panel.add(contentPanel);

        return contentPanel;
    }


    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(SECONDARY_DARK);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_LIGHT);
        label.setPreferredSize(new Dimension(180, 25));

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 30));
        field.setBackground(PRIMARY_DARK);
        field.setForeground(TEXT_LIGHT);
        if (field instanceof JTextField) {
            ((JTextField) field).setCaretColor(TEXT_LIGHT);
        }

        panel.add(label);
        panel.add(field);

        return panel;
    }


    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(TEXT_LIGHT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }


    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BankingSystemGUI();
        });
    }
}
