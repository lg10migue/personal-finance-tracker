import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

public class App {

    // Create a new account and a scanner object.
    private static Account account = new Account();
    private static Scanner scanner = new Scanner(System.in);
    private static final String CSV_FILENAME = "transactions.csv";

    // Main method.
    public static void main(String[] args) throws Exception {
        boolean running = true;

        // Loop until the user chooses to exit.
        while (running) {
            showMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    viewTransactions();
                    break;
                case 3:
                    editTransaction();
                    break;
                case 4:
                    removeTransaction();
                    break;
                case 5:
                    viewFinancialSummary();
                    break;
                case 6:
                    account.exportTransactionsToCSV(CSV_FILENAME);
                    break;
                case 7:
                    if (!account.getTransactions().isEmpty()) {
                        System.out.println(
                                "Warning: You already have transactions in your account. Importing will override existing transactions.");
                        System.out.print("Do you want to continue? (yes/no): ");
                        String confirmation = scanner.nextLine();
                        if (!confirmation.equalsIgnoreCase("yes")) {
                            System.out.println("Import cancelled.");
                            break;
                        }
                    }
                    account.importTransactionsFromCSV(CSV_FILENAME);
                    break;
                case 9:
                    running = false;
                    System.out.println("\nThank you for using your Personal Finance Tracker!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again");
            }
        }
    }

    // Method to show the main menu.
    private static void showMainMenu() {
        System.out.println("\n--- Personal Finance Tracker ---\n");
        System.out.println("1. Add a transaction");
        System.out.println("2. View transactions");
        System.out.println("3. Edit a transaction");
        System.out.println("4. Remove a transaction");
        System.out.println("5. View financial summary");
        System.out.println("6. Export transactions to CSV");
        System.out.println("7. Import transactions from CSV");
        System.out.println("9. Exit");
        System.out.print("\nPlease enter your choice: ");
    }

    // Method to add a transaction.
    private static void addTransaction() {
        System.out.println("\nEnter transaction details\n");

        System.out.print("Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("\nDate (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();

        LocalDate date;
        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("\nInvalid date format. Transaction not added.");
            return;
        }

        // Print all category options.
        System.out.println("\nCategory options:");
        for (Category category : Category.values()) {
            System.out.println("- " + category);
        }

        System.out.println("\nCategory (e.g., GROCERIES, UTILITIES): ");
        String categoryInput = scanner.nextLine().toUpperCase();
        Category category;
        try {
            category = Category.valueOf(categoryInput);
        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid category. Transaction not added.");
            return;
        }

        System.out.print("\nDescription: ");
        String description = scanner.nextLine();

        Transaction transaction = new Transaction(amount, date, category, description);
        account.addTransaction(transaction);

        System.out.println("\nTransaction added successfully.");
    }

    // Method to view all transactions.
    private static void viewTransactions() {
        System.out.println("\nTransactions:\n");
        account.listTransactions();
    }

    // Method to edit a transaction.
    private static void editTransaction() {
        System.out.print("\nEnter transaction ID: ");
        String transactionIdString = scanner.nextLine();

        try {
            int transactionId = Integer.parseInt(transactionIdString);
            System.out.print("New Amount (or leave blank to keep unchanged): ");
            String amountInput = scanner.nextLine();
            if (!amountInput.isEmpty()) {
                double newAmount = Double.parseDouble(amountInput);
                account.editTransactionAmount(transactionId, newAmount);
            }

            System.out.print("New Date (YYYY-MM-DD) (or leave blank to keep unchanged): ");
            String dateInput = scanner.nextLine();
            if (!dateInput.isEmpty()) {
                try {
                    LocalDate newDate = LocalDate.parse(dateInput);
                    account.editTransactionDate(transactionId, newDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Transaction not edited.");
                }
            }

            // Print all category options.
            System.out.println("Category options:");
            for (Category category : Category.values()) {
                System.out.println("- " + category);
            }

            System.out.print("New Category (or leave blank to keep unchanged): ");
            String categoryInput = scanner.nextLine().toUpperCase();
            if (!categoryInput.isEmpty()) {
                try {
                    Category newCategory = Category.valueOf(categoryInput);
                    account.editTransactionCategory(transactionId, newCategory);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid category. Transaction not edited.");
                }
            }

            System.out.print("New Description (or leave blank to keep unchanged): ");
            String newDescription = scanner.nextLine();
            if (!newDescription.isEmpty()) {
                account.editTransactionDescription(transactionId, newDescription);
            }

            System.out.println("\nTransaction edited successfully.");
        } catch (Exception e) {
            System.out.println("Invalid transaction ID. Please enter a numeric value.");
        }
    }

    // Method to remove a transaction.
    private static void removeTransaction() {
        System.out.print("Enter transaction ID: ");
        String transactionIdString = scanner.nextLine();

        try {
            int transactionId = Integer.parseInt(transactionIdString);
            if (account.removeTransactionByID(transactionId)) {
                System.out.println("Transaction removed successfully.");
            } else {
                System.out.println("Transaction not found.");
            }
        } catch (Exception e) {
            System.out.println("Invalid transaction ID. Please enter a numeric value.");
        }
    }

    // Method to view financial summary.
    private static void viewFinancialSummary() {
        System.out.println("\nFinancial Summary:");
        System.out.println("\nTotal Income: " + account.getTotalIncome());
        System.out.println("Total Expense: " + account.getTotalExpense());
        System.out.println("Current Balance: " + account.getCurrentBalance());
        System.out.println("Expenses by Category:");
        Map<String, Double> expensesByCategory = account.getExpensesByCategory();
        expensesByCategory.forEach((category, total) -> System.out.println(category + ": " + total));
    }

}
