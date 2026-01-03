import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.time.LocalDate;

public class FinanceManager {
    private static FinanceService service = new FinanceService();
    private static FileHandler fileHandler = new FileHandler();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        FinanceData data = fileHandler.loadAll();
        service.setHistory(data.getHistory());
        service.setMonthlyBudget(data.getMonthlyBudget());

        System.out.println("======================================");
        System.out.println("   個人財務管理系統 (FinanceManager)   ");
        System.out.println("         版本: v1.6 (穩健正式版)        ");
        System.out.println("======================================");
        System.out.println("系統已就緒，數據與預算載入完成。");

        while (true) {
            System.out.println("\n--- 主功能選單 ---");
            System.out.println("1. 記帳錄入      2. 分類統計報告");
            System.out.println("3. 消費趨勢分析  4. 進階搜尋功能");
            System.out.println("5. 設定月度預算  6. 存檔並離開系統");

            // 使用 v1.4 的驗證工具確保輸入正確
            String choice = getChoiceInRange("請選擇功能 (1-6): ", "1", "2", "3", "4", "5", "6");

            if (choice.equals("1")) {
                addEntry(); // 內含水平類別選擇器 v1.5
            } else if (choice.equals("2")) {
                showStats();
            } else if (choice.equals("3")) {
                showVisualStats(); // 趨勢圖 v1.6
            } else if (choice.equals("4")) {
                searchEntries();
            } else if (choice.equals("5")) {
                setBudget();
            } else if (choice.equals("6")) {
                fileHandler.saveAll(service.getMonthlyBudget(), service.getHistory());
                System.out.println("數據已安全存檔到 records.txt。");
                System.out.println("感謝使用，再見！");
                break;
            }
        }
    }

    private static void setBudget() {
        double b = getValidDouble("請輸入本月預算上限: ");
        service.setMonthlyBudget(b);
        System.out.println("預算已設定並儲存為: $" + b);
    }

    // 獨立的記帳步驟邏輯
    private static void addEntry() {
        String typeChoice = getChoiceInRange("請選擇類型 支出(1) 收入(2): ", "1", "2");
        String type = typeChoice.equals("2") ? "收入" : "支出";

        String cat = getCategoryFromHistory();

        double amt = getValidDouble("輸入金額: ");

        String now = LocalDate.now().toString();
        service.addTransaction(new Transaction(cat, amt, type, now));
        System.out.println("✅ 成功記錄一筆" + type + "！");
    }

    // 獨立的顯示統計邏輯
    private static void showStats() {
        System.out.println("\n--- 分類統計報告 ---");
        service.getCategoryStats().forEach((k, v) -> System.out.printf("%-10s : $%.2f%n", k, v));
        System.out.println("------------------");

        double expense = service.getMonthlyExpense();
        System.out.printf("本月總支出: $%.2f / 本月預算: $%.2f%n", Math.abs(expense), service.getMonthlyBudget());

        // 觸發預算警示
        if (service.getMonthlyBudget() > 0 && service.isNearBudget()) {
            System.out.println("⚠️ 警告：您的支出已達預算的 80% 以上！");
        }

        System.out.printf("總支出: $%.2f\n", service.getTotalExpense());
        System.out.printf("總餘額: $%.2f\n", service.getTotalBalance());
    }

    private static void searchEntries() {
        System.out.println("\n--- 搜尋功能 ---");
        // 使用驗證工具確保只能選 1 或 2
        String subChoice = getChoiceInRange("1. 關鍵字搜尋  2. 大額支出篩選: ", "1", "2");

        if (subChoice.equals("1")) {
            // 確保關鍵字不為空
            String keyword = getCategoryFromHistory();
            var results = service.searchByCategory(keyword);

            if (results.isEmpty()) {
                System.out.println("找不到包含 「" + keyword + "」 的紀錄。");
            } else {
                results.forEach(t -> System.out.println(t.getDate() + " | " + t.getCategory() + ": $" + t.getAmount()));
            }
        } else if (subChoice.equals("2")) {
            // 確保輸入的是數字，否則會重複詢問
            double threshold = getValidDouble("請輸入金額門檻: ");
            var results = service.getLargeTransactions(threshold);

            if (results.isEmpty()) {
                System.out.println("沒有超過 $" + threshold + " 的大額支出。");
            } else {
                System.out.println("超過 $" + threshold + " 的紀錄如下：");
                results.forEach(t -> System.out.println(t.getDate() + " | " + t.getCategory() + ": $" + t.getAmount()));
            }
        }
    }

    private static String getNonEmptyInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("❌ 錯誤：此欄位不能留空，請重新輸入。");
        }
    }

    private static double getValidDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ 錯誤：請輸入有效的數字格式（例如：100 或 50.5）。");
            }
        }
    }

    private static String getChoiceInRange(String prompt, String... validChoices) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            for (String choice : validChoices) {
                if (input.equals(choice))
                    return input;
            }
            System.out.println("❌ 錯誤：請輸入有效的選項 (" + String.join("/", validChoices) + ")。");
        }
    }

    private void showAddDialog() {
        // 建立一個小面板來放置輸入元件
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // 取得現有類別作為選單
        List<String> categories = service.getHistory().stream()
                .map(Transaction::getCategory).distinct().collect(Collectors.toList());
        categories.add("輸入新類別...");

        JComboBox<String> catCombo = new JComboBox<>(categories.toArray(new String[0]));
        JTextField amtField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "支出", "收入" });

        panel.add(new JLabel("交易類型:"));
        panel.add(typeCombo);
        panel.add(new JLabel("選擇類別:"));
        panel.add(catCombo);
        panel.add(new JLabel("金額:"));
        panel.add(amtField);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增財務紀錄", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String cat = (String) catCombo.getSelectedItem();
            // 如果選到「輸入新類別...」，彈出視窗詢問名稱
            if (cat.equals("輸入新類別...")) {
                cat = JOptionPane.showInputDialog("請輸入新類別名稱:");
            }

            try {
                double amt = Double.parseDouble(amtField.getText());
                String type = (String) typeCombo.getSelectedItem();
                // 調用原本的 Service 與 Transaction 模型
                service.addTransaction(new Transaction(cat, amt, type, LocalDate.now().toString()));
                refreshTable();
                updateStatus();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "請輸入有效的數字金額！");
            }
        }
    }

    private static void showVisualStats() {
        System.out.println("\n--- 支出趨勢分析 ---");
        Map<String, Double> percentages = service.getCategoryPercentages();

        if (percentages.isEmpty()) {
            System.out.println("尚無支出紀錄可供分析。");
            return;
        }

        percentages.forEach((cat, percent) -> {
            int barLength = (int) (percent / 2); // 每 2% 顯示一個區塊
            String bar = "■".repeat(barLength);
            System.out.printf("%-10s | %-25s %.1f%%%n", cat, bar, percent);
        });
        System.out.println("--------------------------------");
    }

}