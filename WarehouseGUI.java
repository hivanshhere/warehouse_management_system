import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JLabel statusLabel;
    public JButton addButton, orderButton, dijkstraButton, aStarButton, refreshButton, searchButton, lowStockButton;
    private PathVisualizationPanel visualizationPanel;
    private JPanel rightPanel;
    private JTextArea pathInfoArea;

    // Colors
    private Color backgroundColor = new Color(240, 248, 255);
    private Color buttonColor = new Color(70, 130, 180);
    private Color addButtonColor = new Color(46, 139, 87);
    private Color headerColor = new Color(25, 25, 112);

    // Common electronics products
    private static final String[] ELECTRONICS_PRODUCTS = {
            // Computers and Components
            "Laptop",
            "Desktop PC",
            "Gaming PC",
            "Monitor",
            "Keyboard",
            "Mouse",

            // Mobile Devices
            "Smartphone",
            "Tablet",
            "Smart Watch",
            "Headphones",

            // Networking
            "Router",
            "External Hard Drive",
            "USB Flash Drive",

            // Peripherals
            "Printer",
            "Webcam",
            "Speakers",

            // Gaming
            "Gaming Console",
            "Gaming Controller",
            "VR Headset",

            // Smart Home
            "Smart Bulb",
            "Smart Speaker"
    };

    public WarehouseGUI() {
        setTitle("Warehouse Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Init layout
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(backgroundColor);

        // Create main panels
        createLeftPanel();
        createRightPanel();

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(backgroundColor);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(backgroundColor);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(backgroundColor);
        searchField = createStyledTextField();
        searchButton = createStyledButton("Search Products", buttonColor);
        lowStockButton = createStyledButton("Low Stock Alert", new Color(255, 69, 0));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(lowStockButton);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // Changed to 5 rows to accommodate order dropdown
        inputPanel.setBackground(backgroundColor);

        // Create product dropdown
        productDropdown = new JComboBox<>(ELECTRONICS_PRODUCTS);
        productDropdown.setEditable(true);
        productDropdown.setBackground(Color.WHITE);
        productDropdown.setFont(new Font("Arial", Font.PLAIN, 12));

        // Create order product dropdown
        orderProductDropdown = new JComboBox<>(ELECTRONICS_PRODUCTS);
        orderProductDropdown.setEditable(true);
        orderProductDropdown.setBackground(Color.WHITE);
        orderProductDropdown.setFont(new Font("Arial", Font.PLAIN, 12));

        // Create row and column dropdowns
        String[] numbers = new String[30];
        for (int i = 0; i < 30; i++) {
            numbers[i] = String.valueOf(i + 1);
        }
        rowDropdown = new JComboBox<>(numbers);
        colDropdown = new JComboBox<>(numbers);
        rowDropdown.setBackground(Color.WHITE);
        colDropdown.setBackground(Color.WHITE);
        rowDropdown.setFont(new Font("Arial", Font.PLAIN, 12));
        colDropdown.setFont(new Font("Arial", Font.PLAIN, 12));

        quantityField = createStyledTextField();

        JLabel[] labels = {
                new JLabel("Product Name:"),
                new JLabel("Shelf Row:"),
                new JLabel("Shelf Column:"),
                new JLabel("Quantity:"),
                new JLabel("Order Product:") // Added label for order dropdown
        };

        for (JLabel label : labels) {
            label.setForeground(headerColor);
            label.setFont(new Font("Arial", Font.BOLD, 12));
        }

        inputPanel.add(labels[0]);
        inputPanel.add(productDropdown);
        inputPanel.add(labels[1]);
        inputPanel.add(rowDropdown);
        inputPanel.add(labels[2]);
        inputPanel.add(colDropdown);
        inputPanel.add(labels[3]);
        inputPanel.add(quantityField);
        inputPanel.add(labels[4]); // Add order product label
        inputPanel.add(orderProductDropdown); // Add order product dropdown

        // Table
        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Shelf Row", "Shelf Column", "Quantity" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setBackground(Color.WHITE);
        productTable.setGridColor(new Color(220, 220, 220));
        productTable.getTableHeader().setBackground(headerColor);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setRowHeight(25);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(backgroundColor);

        addButton = createStyledButton("Add Product", addButtonColor);
        orderButton = createStyledButton("Order Product", new Color(255, 140, 0)); // Orange color for order
        dijkstraButton = createStyledButton("Find Path (Dijkstra)", buttonColor);
        aStarButton = createStyledButton("Find Path (A*)", new Color(148, 0, 211)); // Purple color for A*
        refreshButton = createStyledButton("Refresh", buttonColor);

        buttonPanel.add(addButton);
        buttonPanel.add(orderButton);
        buttonPanel.add(dijkstraButton);
        buttonPanel.add(aStarButton);
        buttonPanel.add(refreshButton);

        // Combine all panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
    }

    private void createRightPanel() {
        rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(backgroundColor);

        // Visualization panel
        visualizationPanel = new PathVisualizationPanel();
        visualizationPanel.setBorder(BorderFactory.createTitledBorder("Warehouse Layout"));

        // Path info panel
        pathInfoArea = new JTextArea(5, 40);
        pathInfoArea.setEditable(false);
        pathInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pathInfoArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Path Information"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        rightPanel.add(visualizationPanel, BorderLayout.CENTER);
        rightPanel.add(pathInfoArea, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Getters
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
        statusLabel.setForeground(isError ? new Color(178, 34, 34) : new Color(46, 139, 87));
    }

    public void clearInputFields() {
        productDropdown.setSelectedIndex(0);
        rowDropdown.setSelectedIndex(0);
        colDropdown.setSelectedIndex(0);
        quantityField.setText("");
    }

    public String getProductName() {
        return productDropdown.getSelectedItem().toString();
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
        // Get unique product names from database
        Set<String> uniqueNames = products.stream()
                .map(p -> p.name)
                .collect(Collectors.toSet());

        // Convert to sorted list
        List<String> sortedNames = new ArrayList<>(uniqueNames);
        Collections.sort(sortedNames);

        // Update both dropdowns
        productDropdown.removeAllItems();
        orderProductDropdown.removeAllItems();

        // Add empty option
        productDropdown.addItem("");
        orderProductDropdown.addItem("");

        // Add predefined electronics products first
        for (String name : ELECTRONICS_PRODUCTS) {
            productDropdown.addItem(name);
            orderProductDropdown.addItem(name);
        }

        // Add database products (excluding duplicates)
        for (String name : sortedNames) {
            if (!Arrays.asList(ELECTRONICS_PRODUCTS).contains(name)) {
                productDropdown.addItem(name);
                orderProductDropdown.addItem(name);
            }
        }
    }
}