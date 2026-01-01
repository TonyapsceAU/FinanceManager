import java.util.*;
import java.util.stream.Collectors;

public class FinanceService {
    private List<Transaction> history = new ArrayList<>();

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
}