public class Transaction {
    private String category;
    private double amount;
    private String type; // "收入" 或 "支出"

    public Transaction(String category, double amount, String type) {
        this.category = category;
        this.type = type;
        // 如果是支出，自動將金額轉為負數
        this.amount = type.equals("支出") ? -Math.abs(amount) : Math.abs(amount);
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        // 存檔時多存一個類型欄位
        return category + "," + amount + "," + type;
    }
}