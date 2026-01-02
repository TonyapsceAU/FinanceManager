import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceService {
    private List<Transaction> history = new ArrayList<>();
    private double monthlyBudget = 0; // 預算變數

    public void setMonthlyBudget(double budget) {
        this.monthlyBudget = budget;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    // 顯示「當月」總支出 (符合預算邏輯)
    public double getMonthlyExpense() {
        LocalDate now = LocalDate.now();
        return history.stream()
                .filter(t -> t.getAmount() < 0) // 篩選支出
                .filter(t -> {
                    // 將 String 類型的日期轉換為 LocalDate 物件進行比較
                    LocalDate transactionDate = LocalDate.parse(t.getDate());
                    return transactionDate.getMonth() == now.getMonth() &&
                            transactionDate.getYear() == now.getYear();
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // 計算總支出（只加總負數金額）
    public double getTotalExpense() {
        return history.stream()
                .filter(t -> t.getAmount() < 0)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // 檢查是否超過預算的 80%
    public boolean isNearBudget() {
        if (monthlyBudget == 0)
            return false;
        return Math.abs(getMonthlyExpense()) >= (monthlyBudget * 0.8);
    }

    public void addTransaction(Transaction t) {
        history.add(t);
    }

    public List<Transaction> getHistory() {
        return history;
    }

    // 計算總餘額
    public double getTotalBalance() {
        return history.stream().mapToDouble(Transaction::getAmount).sum();
    }

    // 取得分類統計 (使用 Map)
    public Map<String, Double> getCategoryStats() {
        return history.stream().collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)));
    }

    public void setHistory(List<Transaction> loadedHistory) {
        this.history = loadedHistory;
    }

    // 根據關鍵字搜尋
    public List<Transaction> searchByCategory(String keyword) {
        return history.stream()
                .filter(t -> t.getCategory().contains(keyword)) // 只要類別名稱包含關鍵字
                .collect(java.util.stream.Collectors.toList());
    }

    // 篩選大於特定金額的紀錄
    public List<Transaction> getLargeTransactions(double threshold) {
        return history.stream()
                .filter(t -> Math.abs(t.getAmount()) >= threshold) // 絕對值大於門檻
                .collect(java.util.stream.Collectors.toList());
    }

    // 計算各類別支出佔總支出的比例
    public Map<String, Double> getCategoryPercentages() {
        double totalExpense = Math.abs(getTotalExpense());
        if (totalExpense == 0)
            return new HashMap<>();

        Map<String, Double> stats = getCategoryStats();
        Map<String, Double> percentages = new HashMap<>();

        stats.forEach((cat, amt) -> {
            if (amt < 0) { // 只計算支出
                percentages.put(cat, (Math.abs(amt) / totalExpense) * 100);
            }
        });
        return percentages;
    }
}