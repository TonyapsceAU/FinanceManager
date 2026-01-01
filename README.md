# Personal Finance Manager (CLI) - v1.0

這是一個基於 Java 實作的指令列個人財務管理系統。本專案採用三層架構設計，練習了物件導向 (OOP)、檔案讀寫 (File I/O) 以及資料結構 (HashMap) 的應用。

## ✨ 主要功能
- **記帳功能**：支援區分「收入」與「支出」，自動處理正負號。
- **分類統計**：自動加總相同類別的收支，並顯示總餘額。
- **資料持久化**：程式結束時自動存檔至 `.txt`，啟動時自動載入紀錄。
- **錯誤處理**：針對非法輸入（如非數字金額）具備基本的防錯機制。

## 🏗️ 專案結構 (Architecture)
- `Main (FinanceManager)`: 負責使用者介面與選單調度。
- `Service (FinanceService)`: 處理統計與計算等核心商業邏輯。
- `Data Access (FileHandler)`: 負責檔案讀取與寫入。
- `Model (Transaction)`: 定義交易資料格式。

## 🚀 如何執行
1. 確保已安裝 JDK 17+。
2. 下載所有 `.java` 檔案至同一資料夾。
3. 在終端機執行編譯：`javac FinanceManager.java`
4. 執行程式：`java FinanceManager`