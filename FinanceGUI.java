import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceGUI extends JFrame {
    private FinanceService service = new FinanceService();
    private FileHandler fileHandler = new FileHandler();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public FinanceGUI() {
        // 載入資料
        FinanceData data = fileHandler.loadAll();
        service.setHistory(data.getHistory());
        service.setMonthlyBudget(data.getMonthlyBudget());

        // 視窗初始化
        setTitle("個人財務管理系統 v1.7 - 輕量 GUI");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. 上方狀態列 (Dashboard)
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        updateStatus();
        add(statusLabel, BorderLayout.NORTH);

        // 2. 中間表格 (Transaction Table)
        String[] columns = { "日期", "類別", "類型", "金額" };
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. 下方控制面板 (Buttons)
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("新增紀錄");
        JButton budgetBtn = new JButton("設定預算");
        JButton searchBtn = new JButton("進階搜尋");
        JButton chartBtn = new JButton("趨勢分析");
        JButton saveBtn = new JButton("儲存並離開");

        // 按鈕事件綁定
        addBtn.addActionListener(e -> showAddDialog());
        budgetBtn.addActionListener(e -> {
            String b = JOptionPane.showInputDialog(this, "請輸入本月預算:");
            if (b != null) {
                try {
                    service.setMonthlyBudget(Double.parseDouble(b));
                    updateStatus();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "請輸入正確數字");
                }
            }
        });
        searchBtn.addActionListener(e -> showSearchDialog());
        chartBtn.addActionListener(e -> {
            // 獲取各類別支出百分比數據
            Map<String, Double> stats = service.getCategoryPercentages();
            if (stats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "目前尚無支出紀錄可供分析。");
            } else {
                // 開啟剛剛建立的圖表對話框
                new CategoryChart(this, stats).setVisible(true);
            }
        });
        saveBtn.addActionListener(e -> {
            fileHandler.saveAll(service.getMonthlyBudget(), service.getHistory()); //
            JOptionPane.showMessageDialog(this, "存檔成功！");
            System.exit(0);
        });

        btnPanel.add(addBtn);
        btnPanel.add(budgetBtn);
        btnPanel.add(searchBtn);
        btnPanel.add(chartBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // 解決報錯：更新狀態文字與預算警示
    private void updateStatus() {
        double balance = service.getTotalBalance();
        double expense = Math.abs(service.getMonthlyExpense());
        double budget = service.getMonthlyBudget();
        String alert = service.isNearBudget() ? " ⚠️ 預算超標！" : "";

        statusLabel.setText(String.format("總餘額: $%.2f | 本月支出: $%.2f / 預算: $%.2f %s",
                balance, expense, budget, alert));
        statusLabel.setForeground(service.isNearBudget() ? Color.ORANGE : Color.WHITE);
    }

    // 解決報錯：將 Service 的 List 重新填入 GUI 表格
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Transaction t : service.getHistory()) {
            tableModel.addRow(new Object[] { t.getDate(), t.getCategory(), t.getType(), t.getAmount() });
        }
    }

    // 解決報錯：整合原本的 getCategoryFromHistory 邏輯
    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5)); // 解決 GridLayout 報錯

        // 自動取得現有類別
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

        int result = JOptionPane.showConfirmDialog(this, panel, "新增紀錄", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String cat = (String) catCombo.getSelectedItem();
            if ("輸入新類別...".equals(cat)) {
                cat = JOptionPane.showInputDialog(this, "請輸入新類別名稱:");
            }

            try {
                double amt = Double.parseDouble(amtField.getText());
                String type = (String) typeCombo.getSelectedItem();
                // 使用原有的 Service 邏輯
                service.addTransaction(new Transaction(cat, amt, type, LocalDate.now().toString()));
                refreshTable();
                updateStatus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "輸入格式有誤");
            }
        }
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup(); // 使用 FlatLaf 皮膚
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new FinanceGUI().setVisible(true));
    }

    private void showSearchDialog() {
        String[] options = { "關鍵字搜尋", "大額支出篩選" };
        int searchType = JOptionPane.showOptionDialog(this, "請選擇搜尋方式", "進階搜尋",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (searchType == 0) { // 關鍵字搜尋
            // 1. 取得現有的類別清單
            List<String> categories = service.getHistory().stream()
                    .map(Transaction::getCategory)
                    .distinct()
                    .collect(Collectors.toList());

            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "目前尚無任何紀錄類別。");
                return;
            }

            // 2. 建立下拉選單元件
            JComboBox<String> catCombo = new JComboBox<>(categories.toArray(new String[0]));
            catCombo.setEditable(true); // 允許手動輸入關鍵字，增加靈活性

            int result = JOptionPane.showConfirmDialog(this, catCombo, "請選擇或輸入搜尋類別", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String keyword = (String) catCombo.getSelectedItem();
                if (keyword != null && !keyword.trim().isEmpty()) {
                    var results = service.searchByCategory(keyword); // 調用 Service 搜尋邏輯
                    displaySearchResults("搜尋結果: " + keyword, results);
                }
            }
        } else if (searchType == 1) { // 大額支出篩選
            String thresholdStr = JOptionPane.showInputDialog(this, "請輸入金額門檻 (絕對值):");
            if (thresholdStr != null) {
                try {
                    double threshold = Double.parseDouble(thresholdStr);
                    var results = service.getLargeTransactions(threshold); // 調用 Service 門檻篩選
                    displaySearchResults("大額支出於 $" + threshold, results);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "請輸入有效的數字格式");
                }
            }
        }
    }

    private void displaySearchResults(String title, List<Transaction> results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "找不到符合條件的紀錄。");
            return;
        }

        JDialog resultDialog = new JDialog(this, title, true);
        resultDialog.setSize(500, 350);
        resultDialog.setLocationRelativeTo(this);

        // 設定搜尋結果表格欄位
        String[] columns = { "日期", "類別", "類型", "金額" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Transaction t : results) {
            model.addRow(new Object[] { t.getDate(), t.getCategory(), t.getType(), t.getAmount() });
        }

        JTable resultTable = new JTable(model);
        resultDialog.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // 加入關閉按鈕
        JButton closeBtn = new JButton("關閉");
        closeBtn.addActionListener(e -> resultDialog.dispose());
        resultDialog.add(closeBtn, BorderLayout.SOUTH);

        resultDialog.setVisible(true);
    }
}