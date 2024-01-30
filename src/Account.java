import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;

public class Account {

    // A map to store all transactions.
    private Map<Integer, Transaction> transactions;

    // A static variable to generate transaction IDs.
    private static int nextTransactionId = 1;

    // Constructor.
    public Account() {
        this.transactions = new HashMap<>();
    }

    // Method to get all transactions.
    public Map<Integer, Transaction> getTransactions() {
        return transactions;
    }

    // Method to add a transaction.
    public void addTransaction(Transaction transaction) {
        transaction.setId(nextTransactionId);
        this.transactions.put(nextTransactionId, transaction);
        nextTransactionId++;
    }

    // Method to remove a transaction by ID.
    public boolean removeTransactionByID(int transactionId) {
        return transactions.remove(transactionId) != null;
    }

    // Method to edit a transaction's amount.
    public void editTransactionAmount(int transactionId, double newAmount) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            transaction.setAmount(newAmount);
        }
    }

    // Method to edit a transaction's date.
    public void editTransactionDate(int transactionId, LocalDate newDate) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            transaction.setDate(newDate);
        }
    }

    // Method to edit a transaction's category.
    public void editTransactionCategory(int transactionId, Category newCategory) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            transaction.setCategory(newCategory);
        }
    }

    // Method to edit a transaction's description.
    public void editTransactionDescription(int transactionId, String newDescription) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            transaction.setDescription(newDescription);
        }
    }

    // A method to edit all fields of a transaction at once.
    public void editTransaction(int transactionId, double newAmount, LocalDate newDate, Category newCategory,
            String newDescription) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            transaction.setAmount(newAmount);
            transaction.setDate(newDate);
            transaction.setCategory(newCategory);
            transaction.setDescription(newDescription);
        }
    }

    // Method to list all transactions.
    public void listTransactions() {
        for (Transaction transaction : transactions.values()) {
            System.out.println(transaction);
        }
    }

    // Method to get the total income.
    public double getTotalIncome() {
        return transactions.values().stream().filter(transaction -> transaction.getAmount() > 0)
                .mapToDouble(Transaction::getAmount).sum();
    }

    // Method to get the total expense.
    public double getTotalExpense() {
        return transactions.values().stream().filter(transaction -> transaction.getAmount() < 0)
                .mapToDouble(Transaction::getAmount).sum();
    }

    // Method to get the current balance.
    public double getCurrentBalance() {
        return getTotalIncome() + getTotalExpense();
    }

    // Method to get the total expenses for a given category.
    public Map<String, Double> getExpensesByCategory() {
        return transactions.values().stream().filter(transaction -> transaction.getAmount() < 0)
                .collect(Collectors.groupingBy(transaction -> transaction.getCategory().toString(),
                        Collectors.summingDouble(Transaction::getAmount)));
    }

    // Method to get the total income for month.
    public double getIncomeForMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return calculateTotalAmountForPeriod(startOfMonth, endOfMonth, true);
    }

    // Method to get the total expenses for month.
    public double getExpensesForMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return calculateTotalAmountForPeriod(startOfMonth, endOfMonth, false);
    }

    // Method to get the total income for year.
    public double getTotalIncomeForYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return calculateTotalAmountForPeriod(startOfYear, endOfYear, true);
    }

    // Method to get the total expenses for year.
    public double getTotalExpensesForYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return calculateTotalAmountForPeriod(startOfYear, endOfYear, false);
    }

    // Method to calculate the total amount of incomes/expenses for a given period.
    private double calculateTotalAmountForPeriod(LocalDate start, LocalDate end, boolean isIncome) {
        return transactions.values().stream()
                .filter(transaction -> !transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end))
                .filter(transaction -> (isIncome && transaction.getAmount() > 0)
                        || (!isIncome && transaction.getAmount() < 0))
                .mapToDouble(Transaction::getAmount).sum();
    }

    // Method to export all transactions to a CSV file.
    public void exportTransactionsToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {

            // Write the headers.
            writer.println("ID,Amount,Date,Category,Description");

            // Write each transaction to a new line.
            for (Transaction transaction : transactions.values()) {
                writer.println(
                        String.join(",", String.valueOf(transaction.getId()), String.valueOf(transaction.getAmount()),
                                transaction.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                                transaction.getCategory().toString(), transaction.getDescription()));
            }
            System.out.println("Transactions exported successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }

    // Method to import transactions from a CSV file.
    public void importTransactionsFromCSV(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {

            // Skip the headers.
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {

                // Split the line into fields.
                String[] fields = scanner.nextLine().split(",");

                // Create a new transaction and add it to the account.
                Transaction transaction = new Transaction(Double.parseDouble(fields[1]),
                        LocalDate.parse(fields[2], DateTimeFormatter.ISO_LOCAL_DATE), Category.valueOf(fields[3]),
                        fields[4]);

                // Set the transaction.
                addTransaction(transaction);
            }

            // Set the next transaction ID.
            if (!transactions.isEmpty()) {
                nextTransactionId = Collections.max(transactions.keySet()) + 1;
            }

            System.out.println("Transactions imported successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
    }
}
