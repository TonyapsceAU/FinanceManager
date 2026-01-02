import java.util.List;

public class FinanceData {
    private double monthlyBudget;
    private List<Transaction> history;

    public FinanceData(double monthlyBudget, List<Transaction> history) {
        this.monthlyBudget = monthlyBudget;
        this.history = history;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    public List<Transaction> getHistory() {
        return history;
    }
}