import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class PathVisualizationPanel extends JPanel {
    private List<Product> products;
    private List<Point> path;
    private int selectedProductId = -1;
    private int targetProductId = -1;
    private static final int GRID_SIZE = 20;
    private static final int PRODUCT_SIZE = 16;
    private static final int PADDING = 20;
    private JScrollPane scrollPane;
    private static final int MAX_X = 30;
    private static final int MAX_Y = 30;
    private static final int START_NODE_X = 0;
    private static final int START_NODE_Y = 0;

    public PathVisualizationPanel() {
        products = new ArrayList<>();
        path = new ArrayList<>();

        // Calculate preferred size based on grid dimensions
        int preferredWidth = MAX_X * GRID_SIZE + 2 * PADDING;
        int preferredHeight = MAX_Y * GRID_SIZE + 2 * PADDING;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Create a panel for the actual content
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw grid
                g2d.setColor(new Color(240, 240, 240));
                for (int x = 0; x < MAX_X; x++) {
                    for (int y = 0; y < MAX_Y; y++) {
                        g2d.drawRect(x * GRID_SIZE + PADDING, y * GRID_SIZE + PADDING, GRID_SIZE, GRID_SIZE);
                    }
                }

                // Draw warehouse start node
                g2d.setColor(new Color(255, 140, 0)); // Orange color for start node
                g2d.fillRect(
                        START_NODE_X * GRID_SIZE + PADDING + 2,
                        START_NODE_Y * GRID_SIZE + PADDING + 2,
                        GRID_SIZE - 4,
                        GRID_SIZE - 4);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 8));
                g2d.drawString("START",
                        START_NODE_X * GRID_SIZE + PADDING + 2,
                        START_NODE_Y * GRID_SIZE + PADDING + GRID_SIZE - 2);

                // Draw path
                if (!path.isEmpty()) {
                    g2d.setColor(new Color(70, 130, 180));
                    g2d.setStroke(new BasicStroke(2));
                    Point prev = path.get(0);
                    for (int i = 1; i < path.size(); i++) {
                        Point curr = path.get(i);
                        g2d.drawLine(
                                prev.x * GRID_SIZE + PADDING + GRID_SIZE / 2,
                                prev.y * GRID_SIZE + PADDING + GRID_SIZE / 2,
                                curr.x * GRID_SIZE + PADDING + GRID_SIZE / 2,
                                curr.y * GRID_SIZE + PADDING + GRID_SIZE / 2);
                        prev = curr;
                    }
                }

                // Draw products
                for (Product product : products) {
                    int x = product.x * GRID_SIZE + PADDING;
                    int y = product.y * GRID_SIZE + PADDING;

                    // Set color based on product state
                    if (product.id == selectedProductId) {
                        g2d.setColor(new Color(46, 139, 87)); // Green for selected
                    } else if (product.id == targetProductId) {
                        g2d.setColor(new Color(178, 34, 34)); // Red for target
                    } else {
                        g2d.setColor(new Color(70, 130, 180)); // Blue for normal
                    }

                    g2d.fillOval(x + (GRID_SIZE - PRODUCT_SIZE) / 2,
                            y + (GRID_SIZE - PRODUCT_SIZE) / 2,
                            PRODUCT_SIZE, PRODUCT_SIZE);

                    // Draw product name
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                    String name = product.name.length() > 8 ? product.name.substring(0, 8) + "..." : product.name;
                    g2d.drawString(name, x + 2, y + GRID_SIZE - 2);
                }
            }
        };

        contentPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        contentPanel.setBackground(Color.WHITE);

        // Create scroll pane
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add scroll pane to the panel
        add(scrollPane, BorderLayout.CENTER);
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