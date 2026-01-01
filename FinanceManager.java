import java.util.Scanner;

public class FinanceManager {
    private static FinanceService service = new FinanceService();
    private static FileHandler fileHandler = new FileHandler("records.txt");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        service.setHistory(fileHandler.load());
        System.out.println("系統已就緒，歷史紀錄載入完成。");

        while (true) {
            System.out.println("\n--- 財務管理系統 v1.1 ---");
            // 更新選單文字，增加第 4 個選項
            System.out.println("1. 記帳  2. 統計  3. 搜尋與篩選  4. 存檔並離開");
            System.out.print("請選擇: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                addEntry();
            } else if (choice.equals("2")) {
                showStats();
            } else if (choice.equals("3")) {
                searchEntries(); // 呼叫我們剛剛寫的搜尋方法
            } else if (choice.equals("4")) {
                fileHandler.save(service.getHistory());
                System.out.println("數據已安全存檔，再見！");
                break;
            } else {
                System.out.println("無效選擇，請輸入 1-4 之間的數字。");
            }
        }
    }

    // 獨立的記帳步驟邏輯
    private static void addEntry() {
        System.out.print("請選擇類型 支出(1)   收入(2):  ");
        String typeChoice = scanner.nextLine();
        String type = typeChoice.equals("2") ? "收入" : "支出";

        System.out.print("輸入類別 : ");
        String cat = scanner.nextLine();

        System.out.print("輸入金額: ");
        try {
            double amt = Double.parseDouble(scanner.nextLine());
            // 建立物件並交給服務層
            service.addTransaction(new Transaction(cat, amt, type));
            System.out.println("成功記錄一筆" + type + "！");
        } catch (NumberFormatException e) {
            System.out.println("錯誤：金額必須輸入數字。");
        }
    }

    // 獨立的顯示統計邏輯
    private static void showStats() {
        System.out.println("\n--- 分類統計報告 ---");
        service.getCategoryStats().forEach((k, v) -> System.out.printf("%-10s : $%.2f%n", k, v));
        System.out.println("------------------");
        System.out.printf("總餘額 : $%.2f%n", service.getTotalBalance());
    }

    private static void searchEntries() {
        System.out.println("\n--- 搜尋功能 ---");
        System.out.println("1. 關鍵字搜尋  2. 大額支出篩選");
        String subChoice = scanner.nextLine();

        if (subChoice.equals("1")) {
            System.out.print("請輸入類別關鍵字: ");
            String keyword = scanner.nextLine();
            var results = service.searchByCategory(keyword);
            results.forEach(t -> System.out.println(t.getCategory() + ": $" + t.getAmount()));
        } else if (subChoice.equals("2")) {
            System.out.print("請輸入金額門檻: ");
            double threshold = Double.parseDouble(scanner.nextLine());
            var results = service.getLargeTransactions(threshold);
            results.forEach(t -> System.out.println(t.getCategory() + ": $" + t.getAmount()));
        }
    }
}