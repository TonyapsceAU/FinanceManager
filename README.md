# Personal Finance Manager (CLI) - v1.6 Final

這是一個功能完整的 Java 指令列財務管理系統。從最初的簡單記帳，演進至今已具備數據視覺化、智慧輸入建議以及強大的資料持久化功能。

## ✨ v1.6 核心功能
- **📊 消費趨勢視覺化**：新增 ASCII Bar Chart，透過字元圖表直觀顯示各類別支出比例，讓財務狀況一目了然。
- **💡 智慧類別選擇 (預測輸入)**：系統會自動掃描歷史紀錄並提供「建議清單」。使用者可以透過編號快速選擇現有類別，或新增類別，大幅提升操作效率與資料一致性。
- **🛡️ 強大輸入監理**：全系統導入防呆機制，自動攔截無效輸入（如空字串、非數字、超出範圍的選項），確保程式運行永不崩潰。
- **📅 自動化時間追蹤**：每筆收支自動標註日期，並支援精準的「當月預算」比對。
- **💾 雙重持久化**：
  - `records.txt`：儲存所有交易細節。
  - `config.txt`：自動儲存月度預算設定，重啟程式無需重新輸入。

## 🚀 快速開始
1. **環境需求**：Java JDK 17+。
2. **編譯**：`javac FinanceManager.java`
3. **執行**：`java FinanceManager`

## 🛠️ 技術重點
- **Java Stream API**：用於數據過濾、分類統計（groupingBy）以及生成百分比分析。
- **Time API (LocalDate)**：精確處理時間維度的財務數據。
- **File I/O**：實現配置檔案與紀錄檔案的讀寫分離。
- **Refactoring**：採用三層架構（Manager-Service-Model），使代碼易於維護與擴展。

## 📂 專案結構
- `FinanceManager.java`: 使用者介面與輸入驗證邏輯。
- `FinanceService.java`: 核心計算、趨勢分析與數據處理。
- `FileHandler.java`: 負責資料的存檔與載入。
- `Transaction.java`: 基礎資料模型。