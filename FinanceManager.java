import java.util.Scanner;
import java.time.LocalDate;

public class FinanceManager {
    private static FinanceService service = new FinanceService();
    private static FileHandler fileHandler = new FileHandler("records.txt");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        service.setHistory(fileHandler.load());
        service.setMonthlyBudget(fileHandler.loadBudget());// 載入預算
        System.out.println("系統已就緒，預算與紀錄載入完成。");

        while (true) {
            System.out.println("\n--- 財務管理系統 v1.1 ---");
            System.out.println("1. 記帳  2. 統計  3. 搜尋  4. 設定預算  5. 存檔離開");
            System.out.print("請選擇: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                addEntry();
            } else if (choice.equals("2")) {
                showStats();
            } else if (choice.equals("3")) {
                searchEntries();
            } else if (choice.equals("4")) {
                setBudget();
            } else if (choice.equals("5")) {
                fileHandler.save(service.getHistory());
                System.out.println("數據已安全存檔，再見！");
                break;
            } else {
                System.out.println("無效選擇，請輸入 1-4 之間的數字。");
            }
        }
    }

    private static void setBudget() {
        double b = getValidDouble("請輸入本月預算上限: ");
        service.setMonthlyBudget(b);
        fileHandler.saveBudget(b);
        System.out.println("預算已設定並儲存為: $" + b);
    }

    // 獨立的記帳步驟邏輯
    private static void addEntry() {
        String typeChoice = getChoiceInRange("請選擇類型 支出(1) 收入(2): ", "1", "2");
        String type = typeChoice.equals("2") ? "收入" : "支出";

        String cat = getNonEmptyInput("輸入類別 : ");
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
            String keyword = getNonEmptyInput("請輸入類別關鍵字: ");
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

}