import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class WarehouseGUI extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField, quantityField;
    private JComboBox<String> productDropdown, rowDropdown, colDropdown, orderProductDropdown;
    private JLabel statusLabel, statusIcon;
    public JButton addButton, orderButton, dijkstraButton, aStarButton, refreshButton, searchButton, lowStockButton;
    private PathVisualizationPanel visualizationPanel;
    private JPanel rightPanel;
    private JTextArea pathInfoArea;

    // Modern Color Palette
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color HEADER_BG = new Color(15, 23, 42);
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);

    // Button Colors
    private static final Color COLOR_PRIMARY = new Color(37, 99, 235);
    private static final Color COLOR_ADD = new Color(16, 185, 129);
    private static final Color COLOR_ORDER = new Color(245, 158, 11);
    private static final Color COLOR_DIJKSTRA = new Color(79, 70, 229);
    private static final Color COLOR_ASTAR = new Color(139, 92, 246);
    private static final Color COLOR_ALERT = new Color(239, 68, 68);
    private static final Color COLOR_REFRESH = new Color(71, 85, 105);

    // Common electronics products
    private static final String[] ELECTRONICS_PRODUCTS = {
            "Laptop",
            "Desktop PC",
            "Gaming PC",
            "Monitor",
            "Keyboard",
            "Mouse",
            "Smartphone",
            "Tablet",
            "Smart Watch",
            "Headphones",
            "Router",
            "External Hard Drive",
            "USB Flash Drive",
            "Printer",
            "Webcam",
            "Speakers",
            "Gaming Console",
            "Gaming Controller",
            "VR Headset",
            "Smart Bulb",
            "Smart Speaker"
    };

    public WarehouseGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("RapidRack — Smart Warehouse Management & Route Optimizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 880);
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);

        // Main Layout
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBackground(BG_COLOR);
        setContentPane(contentPane);

        // Top App Header Bar
        createHeaderBar(contentPane);

        // Main Center Area (Split Left Control Center / Right Warehouse Blueprint)
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(new EmptyBorder(12, 14, 8, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Panel (Width ~45%)
        gbc.gridx = 0;
        gbc.weightx = 0.48;
        mainContainer.add(createLeftPanel(), gbc);

        // Right Panel (Width ~55%)
        gbc.gridx = 1;
        gbc.weightx = 0.52;
        gbc.insets = new Insets(0, 14, 0, 0);
        mainContainer.add(createRightPanel(), gbc);

        contentPane.add(mainContainer, BorderLayout.CENTER);

        // Bottom Status Bar
        createStatusBar(contentPane);
    }

    private void createHeaderBar(JPanel parent) {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left Title & Subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel brandLabel = new JLabel("RapidRack");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Smart Warehouse Inventory Management & Path Optimization");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));

        titlePanel.add(brandLabel);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(subtitleLabel);

        // Right Badges / Metadata
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgePanel.setOpaque(false);

        JLabel gridBadge = createBadge("Grid: 30 x 30 Racks", new Color(51, 65, 85), Color.WHITE);
        JLabel algoBadge = createBadge("Dijkstra & A* Enabled", new Color(30, 41, 59), new Color(147, 197, 253));

        badgePanel.add(gridBadge);
        badgePanel.add(algoBadge);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(badgePanel, BorderLayout.EAST);

        parent.add(header, BorderLayout.NORTH);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(BG_COLOR);

        // Top Form & Operations Card
        JPanel topOperationsCard = createCardPanel("Inventory Operations");
        topOperationsCard.setLayout(new BoxLayout(topOperationsCard, BoxLayout.Y_AXIS));

        // 1. Search & Filter Bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        searchField = createStyledTextField();
        searchField.setToolTipText("Search product name...");

        JPanel searchBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchBtnGroup.setOpaque(false);
        searchButton = createStyledButton("Search", COLOR_PRIMARY);
        searchButton.setPreferredSize(new Dimension(85, 32));
        lowStockButton = createStyledButton("Low Stock Alert", COLOR_ALERT);
        lowStockButton.setPreferredSize(new Dimension(125, 32));

        searchBtnGroup.add(searchButton);
        searchBtnGroup.add(lowStockButton);

        searchBar.add(new JLabel("Search: "), BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(searchBtnGroup, BorderLayout.EAST);

        topOperationsCard.add(searchBar);
        topOperationsCard.add(Box.createVerticalStrut(12));

        // Divider
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(BORDER_COLOR);
        topOperationsCard.add(sep1);
        topOperationsCard.add(Box.createVerticalStrut(10));

        // 2. Input Fields Grid
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // Predefined dropdowns
        productDropdown = new JComboBox<>(ELECTRONICS_PRODUCTS);
        productDropdown.setEditable(true);
        styleComboBox(productDropdown);

        orderProductDropdown = new JComboBox<>(ELECTRONICS_PRODUCTS);
        orderProductDropdown.setEditable(true);
        styleComboBox(orderProductDropdown);

        String[] numbers = new String[30];
        for (int i = 0; i < 30; i++) {
            numbers[i] = String.valueOf(i + 1);
        }
        rowDropdown = new JComboBox<>(numbers);
        colDropdown = new JComboBox<>(numbers);
        styleComboBox(rowDropdown);
        styleComboBox(colDropdown);

        quantityField = createStyledTextField();

        // Row 1: Product Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        formGrid.add(createFieldLabel("Product Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.75; gbc.gridwidth = 3;
        formGrid.add(productDropdown, gbc);

        // Row 2: Shelf Row & Column side by side
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25; gbc.gridwidth = 1;
        formGrid.add(createFieldLabel("Shelf Row (1-30):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.25;
        formGrid.add(rowDropdown, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.25;
        formGrid.add(createFieldLabel("Shelf Col (1-30):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.25;
        formGrid.add(colDropdown, gbc);

        // Row 3: Quantity & Order Product
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        formGrid.add(createFieldLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.25;
        formGrid.add(quantityField, gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.25;
        formGrid.add(createFieldLabel("Order Product:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 0.25;
        formGrid.add(orderProductDropdown, gbc);

        topOperationsCard.add(formGrid);
        topOperationsCard.add(Box.createVerticalStrut(10));

        // 3. Action Buttons Row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        buttonRow.setOpaque(false);

        addButton = createStyledButton("Add Product", COLOR_ADD);
        orderButton = createStyledButton("Order Product", COLOR_ORDER);
        dijkstraButton = createStyledButton("Find Path (Dijkstra)", COLOR_DIJKSTRA);
        aStarButton = createStyledButton("Find Path (A*)", COLOR_ASTAR);
        refreshButton = createStyledButton("Refresh", COLOR_REFRESH);

        buttonRow.add(addButton);
        buttonRow.add(orderButton);
        buttonRow.add(dijkstraButton);
        buttonRow.add(aStarButton);
        buttonRow.add(refreshButton);

        topOperationsCard.add(buttonRow);

        leftPanel.add(topOperationsCard, BorderLayout.NORTH);

        // Bottom Table Card
        JPanel tableCard = createCardPanel("Warehouse Inventory Table (Sorted by Quantity)");
        tableCard.setLayout(new BorderLayout(0, 8));

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Shelf Row", "Shelf Column", "Quantity" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        styleTable(productTable);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(tableCard, BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(BG_COLOR);

        // Blueprint Card
        JPanel mapCard = createCardPanel("Warehouse Layout & Route Blueprint");
        mapCard.setLayout(new BorderLayout(0, 8));

        visualizationPanel = new PathVisualizationPanel();
        mapCard.add(visualizationPanel, BorderLayout.CENTER);

        // Path Information Card
        JPanel infoCard = createCardPanel("Route & Execution Details");
        infoCard.setLayout(new BorderLayout());
        infoCard.setPreferredSize(new Dimension(100, 140));

        pathInfoArea = new JTextArea();
        pathInfoArea.setEditable(false);
        pathInfoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        pathInfoArea.setForeground(TEXT_PRIMARY);
        pathInfoArea.setBackground(new Color(248, 250, 252));
        pathInfoArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane infoScroll = new JScrollPane(pathInfoArea);
        infoScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        infoCard.add(infoScroll, BorderLayout.CENTER);

        rightPanel.add(mapCard, BorderLayout.CENTER);
        rightPanel.add(infoCard, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void createStatusBar(JPanel parent) {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        statusBar.setBackground(CARD_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(4, 10, 4, 10)));

        statusIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.fillOval(2, 2, 8, 8);
                g2.dispose();
            }
        };
        statusIcon.setPreferredSize(new Dimension(12, 12));
        statusIcon.setForeground(COLOR_ADD);

        statusLabel = new JLabel("System Ready — Connected to database");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);

        statusBar.add(statusIcon);
        statusBar.add(statusLabel);

        parent.add(statusBar, BorderLayout.SOUTH);
    }

    // Helper UI Factories
    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(0, 0, 4, 0),
                                title,
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                new Font("Segoe UI", Font.BOLD, 13),
                                TEXT_PRIMARY),
                        new EmptyBorder(6, 10, 10, 10))));
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(fg);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        return badge;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(12);
        field.setBackground(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(5, 8, 5, 8)));
        return field;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setBackground(Color.WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.setForeground(TEXT_PRIMARY);
        box.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(135, 34));
        return button;
    }

    private void styleTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(new Color(30, 27, 75));

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(71, 85, 105));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Alternating row renderer with padding
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                if (column == 0 || column >= 2) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        });
    }

    // Getters & Public API for WarehouseApp
    public JTable getProductTable() {
        return productTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JTextField getQuantityField() {
        return quantityField;
    }

    public PathVisualizationPanel getVisualizationPanel() {
        return visualizationPanel;
    }

    public void setPathInfo(String info) {
        pathInfoArea.setText(info);
    }

    public void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusIcon.setForeground(COLOR_ALERT);
            statusLabel.setForeground(new Color(185, 28, 28));
        } else {
            statusIcon.setForeground(COLOR_ADD);
            statusLabel.setForeground(new Color(21, 128, 61));
        }
    }

    public void clearInputFields() {
        if (productDropdown.getItemCount() > 0) productDropdown.setSelectedIndex(0);
        if (rowDropdown.getItemCount() > 0) rowDropdown.setSelectedIndex(0);
        if (colDropdown.getItemCount() > 0) colDropdown.setSelectedIndex(0);
        quantityField.setText("");
    }

    public String getProductName() {
        Object item = productDropdown.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    public int getShelfRow() {
        return Integer.parseInt(rowDropdown.getSelectedItem().toString());
    }

    public int getShelfColumn() {
        return Integer.parseInt(colDropdown.getSelectedItem().toString());
    }

    public String getOrderProductName() {
        return (String) orderProductDropdown.getSelectedItem();
    }

    public void updateProductDropdowns(List<Product> products) {
        Set<String> uniqueNames = products.stream()
                .map(p -> p.name)
                .collect(Collectors.toSet());

        List<String> sortedNames = new ArrayList<>(uniqueNames);
        Collections.sort(sortedNames);

        productDropdown.removeAllItems();
        orderProductDropdown.removeAllItems();

        productDropdown.addItem("");
        orderProductDropdown.addItem("");

        for (String name : ELECTRONICS_PRODUCTS) {
            productDropdown.addItem(name);
            orderProductDropdown.addItem(name);
        }

        for (String name : sortedNames) {
            if (!Arrays.asList(ELECTRONICS_PRODUCTS).contains(name)) {
                productDropdown.addItem(name);
                orderProductDropdown.addItem(name);
            }
        }
    }
}