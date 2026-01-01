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

    // 新增：根據關鍵字搜尋
    public List<Transaction> searchByCategory(String keyword) {
        return history.stream()
                .filter(t -> t.getCategory().contains(keyword)) // 只要類別名稱包含關鍵字
                .collect(java.util.stream.Collectors.toList());
    }

    // 新增：篩選大於特定金額的紀錄
    public List<Transaction> getLargeTransactions(double threshold) {
        return history.stream()
                .filter(t -> Math.abs(t.getAmount()) >= threshold) // 絕對值大於門檻
                .collect(java.util.stream.Collectors.toList());
    }
}