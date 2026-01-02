# 個人財務管理系統 (FinanceManager) - v1.7

這是一個基於 Java 開發的個人財務管理工具，支援收入與支出紀錄、預算設定、分類統計及消費趨勢分析。

## 🚀 版本更新：v1.7 (JSON 升級版)
- **資料存儲轉型**：從 CSV (`records.txt`) 全面升級為 **JSON** 格式 (`data.json`)。
- **依賴管理**：引入 Google **Gson** 函式庫進行高效的物件序列化。
- **整合存取**：預算設定與交易紀錄現在統一存儲於單一 JSON 檔案中。

## 🛠️ 環境需求
- **JDK 17** 或更高版本。
- **Gson 函式庫** (已附帶 `gson-2.13.1.jar`)。

## 📦 如何在 VS Code 執行
由於專案使用了外部 Jar 檔，請確保按照以下步驟操作：
1. 開啟專案資料夾。
2. 在左側 **Java Projects** 檢視窗中，找到 **Referenced Libraries**。
3. 點擊 **+** 號並選擇目錄下的 `gson-2.13.1.jar`。
4. 打開 `FinanceManager.java` 並點擊右上角的 **Run** 按鈕。

## 📂 檔案結構
- `FinanceManager.java`: 系統主入口與選單邏輯。
- `FinanceService.java`: 財務運算與資料處理邏輯。
- `FileHandler.java`: 負責 JSON 檔案的讀取與寫入。
- `FinanceData.java`: 定義 JSON 存儲的資料結構。
- `Transaction.java`: 交易紀錄模型。

## 📝 使用說明
1. **記帳錄入**：支援水平類別選擇器。
2. **分類統計**：顯示各類別支出金額與佔比。
3. **趨勢分析**：使用文字圖表顯示消費分佈。
4. **預算警示**：當支出超過預算 80% 時自動提醒。