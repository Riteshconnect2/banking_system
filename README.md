# Banking System with Java GUI Frontend

This package contains two files:

## 1. BankingSystem.c
- Original C implementation of the banking system
- Features: Account management, deposits, withdrawals, transaction history, undo functionality
- Compile with: gcc -o banking BankingSystem.c
- Run with: ./banking (Linux/Mac) or banking.exe (Windows)

## 2. BankingSystemGUI.java
- Modern Java Swing GUI frontend
- Professional banking theme with blue/green color scheme
- Same functionality as C version but with user-friendly interface
- Features include:
  * Add/Delete accounts
  * Deposit/Withdraw money
  * View all accounts in a table
  * Transaction history display
  * Undo last transaction
  * Input validation and error handling

## How to Run the Java Frontend:

### Method 1: Command Line
```bash
javac BankingSystemGUI.java
java BankingSystemGUI
```

### Method 2: Using an IDE
1. Open BankingSystemGUI.java in your favorite Java IDE (Eclipse, IntelliJ IDEA, VS Code, etc.)
2. Run the main method

## Features of the Java GUI:
- Professional banking-themed interface
- Intuitive navigation with card-based layout
- Real-time balance updates
- Transaction history tracking
- Input validation and user-friendly error messages
- Responsive design with proper spacing and fonts
- Hover effects on buttons
- Confirmation dialogs for critical operations

## System Requirements:
- Java 8 or higher
- Any operating system that supports Java Swing

## Note:
The Java frontend is a standalone application that replicates the functionality of the C program. 
It uses the same data structures (linked lists for accounts, stacks for transaction history) 
but implemented in Java with a modern GUI interface.
