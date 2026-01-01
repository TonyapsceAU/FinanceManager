import java.io.*;
import java.util.*;

public class FileHandler {
    private String fileName;

    public FileHandler(String fileName) {
        this.fileName = fileName;
    }

    // 儲存資料：將 Transaction 清單寫入檔案
    public void save(List<Transaction> history) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Transaction t : history) {
                writer.println(t.toString());
            }
        } catch (IOException e) {
            System.err.println("檔案儲存失敗: " + e.getMessage());
        }
    }

    // 讀取資料：從檔案解析出 Transaction 清單
    public List<Transaction> load() {
        List<Transaction> loadedHistory = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return loadedHistory; // 如果檔案不存在，回傳空清單
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                // 在 load() 方法中的 split 部分改為：
                if (parts.length == 3) {
                    String cat = parts[0];
                    double amt = Double.parseDouble(parts[1]);
                    String type = parts[2];
                    loadedHistory.add(new Transaction(cat, Math.abs(amt), type));
                }
            }
        } catch (Exception e) {
            System.err.println("檔案讀取失敗: " + e.getMessage());
        }
        return loadedHistory;
    }
}