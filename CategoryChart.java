import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CategoryChart extends JDialog {
    private Map<String, Double> data;

    public CategoryChart(Frame owner, Map<String, Double> data) {
        super(owner, "消費趨勢分析", true);
        this.data = data;
        setSize(500, 400);
        setLocationRelativeTo(owner);

        // 加入自定義繪圖面板
        add(new ChartPanel());
    }

    private class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 50;
            int y = 50;
            int barHeight = 30;
            int maxWidth = getWidth() - 150;

            // 遍歷 Service 提供的百分比數據
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String category = entry.getKey();
                double percent = entry.getValue();
                int barWidth = (int) (maxWidth * (percent / 100));

                // 畫長條圖
                g2d.setColor(new Color(70, 130, 180)); // 設為精緻的藍色
                g2d.fillRoundRect(x + 80, y, barWidth, barHeight, 10, 10);

                // 畫文字標籤
                g2d.setColor(Color.WHITE);
                g2d.drawString(category, x, y + 20);
                g2d.drawString(String.format("%.1f%%", percent), x + 90 + barWidth, y + 20);

                y += 50; // 下移一行
            }
        }
    }
}