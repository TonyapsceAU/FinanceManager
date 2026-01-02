import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;

public class FileHandler {
    private String jsonFile = "data.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 儲存所有資料 (預算 + 紀錄)
    public void saveAll(double budget, List<Transaction> history) {
        try (Writer writer = new FileWriter(jsonFile)) {
            FinanceData data = new FinanceData(budget, history);
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("JSON 存檔失敗: " + e.getMessage());
        }
    }

    // 載入所有資料
    public FinanceData loadAll() {
        File file = new File(jsonFile);
        if (!file.exists())
            return new FinanceData(0.0, new ArrayList<>());

        try (Reader reader = new FileReader(jsonFile)) {
            FinanceData data = gson.fromJson(reader, FinanceData.class);
            return (data != null) ? data : new FinanceData(0.0, new ArrayList<>());
        } catch (Exception e) {
            return new FinanceData(0.0, new ArrayList<>());
        }
    }
}