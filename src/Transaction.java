import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {

    // Transaction fields.
    private int id;
    private double amount;
    private LocalDate date;
    private Category category;
    private String description;

    // Constructor.
    public Transaction(double amount, LocalDate date, Category category, String description) {
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    // Getters.
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    // Setters.
    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString method for easy printing.
    @Override
    public String toString() {
        return String.format("Transaction ID: %d | Date: %s | Amount: $%.2f | Category: %s | Description: %s",
                id, date.format(DateTimeFormatter.ISO_LOCAL_DATE), amount, category, description);
    }
}
