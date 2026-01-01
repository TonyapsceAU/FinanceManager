# Personal Finance Manager (CLI) - v1.1

這是一個基於 Java 實作的指令列個人財務管理系統。

## 🆕 v1.1 更新亮點
- **進階搜尋與篩選**：引入 **Java Stream API**，支援關鍵字搜尋（如搜尋「飲食」）以及大額支出門檻篩選。
- **UI 優化**：重新設計選單結構，提供更直觀的操作流程。

## ✨ 核心功能
- **記帳功能**：支援區分「收入」與「支出」，自動處理正負號。
- **分類統計**：自動加總相同類別的收支，並顯示總餘額。
- **資料持久化**：程式結束時自動透過 `FileHandler` 存檔至 `.txt`。
- **健壯性**：具備基本的輸入防錯機制（如非數字金額檢查）。

## 🏗️ 專案架構 (N-Tier Architecture)
- **FinanceManager**: 負責 UI 顯示與使用者互動。
- **FinanceService**: 負責商業邏輯（計算、統計、Stream 篩選）。
- **FileHandler**: 負責 Data Access（檔案 I/O）。
- **Transaction**: 資料實體模型。

## 🚀 如何執行
1. 確保已安裝 JDK 17+。
2. 編譯：`javac FinanceManager.java`
3. 執行：`java FinanceManager`