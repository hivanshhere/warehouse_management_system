import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.ArrayList;

public class PathVisualizationPanel extends JPanel {
    private List<Product> products;
    private List<Point> path;
    private int selectedProductId = -1;
    private int targetProductId = -1;

    // Grid Dimensions
    private static final int GRID_SIZE = 22;
    private static final int PRODUCT_SIZE = 16;
    private static final int PADDING_LEFT = 32; // extra room for row numbers
    private static final int PADDING_TOP = 26;  // extra room for col numbers
    private static final int PADDING_RIGHT = 16;
    private static final int PADDING_BOTTOM = 16;
    private static final int MAX_X = 30;
    private static final int MAX_Y = 30;
    private static final int START_NODE_X = 0;
    private static final int START_NODE_Y = 0;

    // Modern Colors
    private static final Color CANVAS_BG = Color.WHITE;
    private static final Color GRID_LINE_COLOR = new Color(241, 245, 249);
    private static final Color AXIS_TEXT_COLOR = new Color(148, 163, 184);
    private static final Color START_COLOR = new Color(249, 115, 22);
    private static final Color PRODUCT_NORMAL = new Color(37, 99, 235);
    private static final Color PRODUCT_SOURCE = new Color(16, 185, 129);
    private static final Color PRODUCT_TARGET = new Color(239, 68, 68);
    private static final Color PATH_LINE_COLOR = new Color(99, 102, 241);

    public PathVisualizationPanel() {
        products = new ArrayList<>();
        path = new ArrayList<>();

        int preferredWidth = MAX_X * GRID_SIZE + PADDING_LEFT + PADDING_RIGHT;
        int preferredHeight = MAX_Y * GRID_SIZE + PADDING_TOP + PADDING_BOTTOM;

        setLayout(new BorderLayout());
        setBackground(CANVAS_BG);

        // Drawing Panel
        JPanel contentPanel = new JPanel() {
            {
                ToolTipManager.sharedInstance().registerComponent(this);
            }

            @Override
            public String getToolTipText(MouseEvent e) {
                int mouseX = e.getX() - PADDING_LEFT;
                int mouseY = e.getY() - PADDING_TOP;

                if (mouseX >= 0 && mouseY >= 0) {
                    int cellX = mouseX / GRID_SIZE;
                    int cellY = mouseY / GRID_SIZE;

                    if (cellX == START_NODE_X && cellY == START_NODE_Y) {
                        return "<html><b>Warehouse Entrance</b><br>Coordinates: (Row 1, Col 1)</html>";
                    }

                    for (Product p : products) {
                        if (p.x == cellX && p.y == cellY) {
                            String role = "";
                            if (p.id == selectedProductId) role = " <font color='#10B981'><b>(SOURCE)</b></font>";
                            else if (p.id == targetProductId) role = " <font color='#EF4444'><b>(TARGET)</b></font>";

                            return String.format(
                                    "<html><b>Product: %s</b>%s<br>Rack Location: Row %d, Col %d<br>Available Stock: <b>%d units</b></html>",
                                    p.name, role, p.x + 1, p.y + 1, p.quantity);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 1. Draw Column Numbers (Top Axis)
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2d.setColor(AXIS_TEXT_COLOR);
                for (int x = 0; x < MAX_X; x++) {
                    if ((x + 1) % 5 == 0 || x == 0) {
                        String colStr = String.valueOf(x + 1);
                        int strW = g2d.getFontMetrics().stringWidth(colStr);
                        g2d.drawString(colStr, PADDING_LEFT + x * GRID_SIZE + (GRID_SIZE - strW) / 2, PADDING_TOP - 6);
                    }
                }

                // 2. Draw Row Numbers (Left Axis)
                for (int y = 0; y < MAX_Y; y++) {
                    if ((y + 1) % 5 == 0 || y == 0) {
                        String rowStr = String.valueOf(y + 1);
                        int strW = g2d.getFontMetrics().stringWidth(rowStr);
                        g2d.drawString(rowStr, PADDING_LEFT - strW - 6, PADDING_TOP + y * GRID_SIZE + (GRID_SIZE + 6) / 2);
                    }
                }

                // 3. Draw Warehouse Grid
                for (int x = 0; x < MAX_X; x++) {
                    for (int y = 0; y < MAX_Y; y++) {
                        int px = PADDING_LEFT + x * GRID_SIZE;
                        int py = PADDING_TOP + y * GRID_SIZE;

                        g2d.setColor(GRID_LINE_COLOR);
                        g2d.drawRect(px, py, GRID_SIZE, GRID_SIZE);
                    }
                }

                // 4. Draw Warehouse Entrance Node (0,0)
                int startPx = PADDING_LEFT + START_NODE_X * GRID_SIZE;
                int startPy = PADDING_TOP + START_NODE_Y * GRID_SIZE;

                g2d.setColor(START_COLOR);
                g2d.fillRoundRect(startPx + 2, startPy + 2, GRID_SIZE - 4, GRID_SIZE - 4, 6, 6);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 7));
                g2d.drawString("ENTRY", startPx + 3, startPy + GRID_SIZE - 6);

                // 5. Draw Path Route (if active)
                if (!path.isEmpty()) {
                    g2d.setColor(PATH_LINE_COLOR);
                    g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    Point prev = path.get(0);
                    for (int i = 1; i < path.size(); i++) {
                        Point curr = path.get(i);
                        int x1 = PADDING_LEFT + prev.x * GRID_SIZE + GRID_SIZE / 2;
                        int y1 = PADDING_TOP + prev.y * GRID_SIZE + GRID_SIZE / 2;
                        int x2 = PADDING_LEFT + curr.x * GRID_SIZE + GRID_SIZE / 2;
                        int y2 = PADDING_TOP + curr.y * GRID_SIZE + GRID_SIZE / 2;

                        g2d.drawLine(x1, y1, x2, y2);
                        prev = curr;
                    }

                    // Waypoint dots
                    g2d.setColor(Color.WHITE);
                    for (Point pt : path) {
                        int cx = PADDING_LEFT + pt.x * GRID_SIZE + GRID_SIZE / 2;
                        int cy = PADDING_TOP + pt.y * GRID_SIZE + GRID_SIZE / 2;
                        g2d.fillOval(cx - 3, cy - 3, 6, 6);
                        g2d.setColor(PATH_LINE_COLOR);
                        g2d.drawOval(cx - 3, cy - 3, 6, 6);
                        g2d.setColor(Color.WHITE);
                    }
                }

                // 6. Draw Products
                for (Product product : products) {
                    int px = PADDING_LEFT + product.x * GRID_SIZE;
                    int py = PADDING_TOP + product.y * GRID_SIZE;
                    int centerOffset = (GRID_SIZE - PRODUCT_SIZE) / 2;

                    // Glow / Selection Ring
                    if (product.id == selectedProductId) {
                        g2d.setColor(new Color(16, 185, 129, 90));
                        g2d.fillOval(px + centerOffset - 3, py + centerOffset - 3, PRODUCT_SIZE + 6, PRODUCT_SIZE + 6);
                        g2d.setColor(PRODUCT_SOURCE);
                    } else if (product.id == targetProductId) {
                        g2d.setColor(new Color(239, 68, 68, 90));
                        g2d.fillOval(px + centerOffset - 3, py + centerOffset - 3, PRODUCT_SIZE + 6, PRODUCT_SIZE + 6);
                        g2d.setColor(PRODUCT_TARGET);
                    } else {
                        g2d.setColor(PRODUCT_NORMAL);
                    }

                    // Product Pill Circle
                    g2d.fillOval(px + centerOffset, py + centerOffset, PRODUCT_SIZE, PRODUCT_SIZE);

                    // Product Outer Ring
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(1.2f));
                    g2d.drawOval(px + centerOffset, py + centerOffset, PRODUCT_SIZE, PRODUCT_SIZE);
                }
            }
        };

        contentPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        contentPanel.setBackground(CANVAS_BG);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CANVAS_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        // Bottom Visual Legend Strip
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        legendPanel.setBackground(new Color(248, 250, 252));
        legendPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        legendPanel.add(createLegendItem("Entrance", START_COLOR, true));
        legendPanel.add(createLegendItem("Stored Product", PRODUCT_NORMAL, false));
        legendPanel.add(createLegendItem("Source (Start)", PRODUCT_SOURCE, false));
        legendPanel.add(createLegendItem("Target (Dest)", PRODUCT_TARGET, false));
        legendPanel.add(createLegendItem("Calculated Route", PATH_LINE_COLOR, true));

        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendItem(String labelText, Color color, boolean isSquare) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);

        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                if (isSquare) {
                    g2.fillRoundRect(1, 1, 10, 10, 3, 3);
                } else {
                    g2.fillOval(1, 1, 10, 10);
                }
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(12, 12));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(71, 85, 105));

        item.add(icon);
        item.add(label);
        return item;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        repaint();
    }

    public void setPath(List<Point> path, int sourceId, int targetId) {
        this.path = path;
        this.selectedProductId = sourceId;
        this.targetProductId = targetId;
        repaint();
    }

    public void clearPath() {
        this.path.clear();
        this.selectedProductId = -1;
        this.targetProductId = -1;
        repaint();
    }
}