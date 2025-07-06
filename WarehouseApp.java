import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.awt.Point;

public class WarehouseApp {
    private static Connection conn;
    private static WarehouseGUI gui;
    private static List<Product> products;

    // Define grid limits
    private static final int MAX_X = 30; // Maximum X coordinate (30 grid cells)
    private static final int MAX_Y = 30; // Maximum Y coordinate (30 grid cells)

    // Warehouse start node (entrance)
    private static final int START_NODE_X = 0;
    private static final int START_NODE_Y = 0;

    public static void main(String[] args) {
        // Init GUI
        SwingUtilities.invokeLater(() -> {
            try {
                conn = DBManager.getConnection();
                gui = new WarehouseGUI();
                products = new ArrayList<>();
                setupEventHandlers();
                refreshProductTable();
                gui.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
                System.exit(1);
            }
        });
    }

    private static void setupEventHandlers() {
        // Events
        gui.addButton.addActionListener(e -> addProduct());
        gui.orderButton.addActionListener(e -> orderProduct());
        gui.dijkstraButton.addActionListener(e -> findPath(false));
        gui.aStarButton.addActionListener(e -> findPath(true));
        gui.refreshButton.addActionListener(e -> refreshProductTable());
        gui.searchButton.addActionListener(e -> searchProducts());
        gui.lowStockButton.addActionListener(e -> findLowStockProducts());
    }

    private static void addProduct() {
        try {
            // Get input
            String name = gui.getProductName().trim();
            int x = gui.getShelfRow() - 1; // Convert from 1-based to 0-based
            int y = gui.getShelfColumn() - 1; // Convert from 1-based to 0-based
            String quantityText = gui.getQuantityField().getText().trim();

            if (name.isEmpty() || quantityText.isEmpty()) {
                gui.setStatus("Please fill all fields", true);
                return;
            }

            int quantity = Integer.parseInt(quantityText);

            // Validate coordinates
            if (x < 0 || x >= MAX_X || y < 0 || y >= MAX_Y) {
                gui.setStatus(String.format("Coordinates must be between (1,1) and (%d,%d)", MAX_X, MAX_Y),
                        true);
                return;
            }

            if (quantity < 0) {
                gui.setStatus("Quantity cannot be negative", true);
                return;
            }

            // Save
            String sql = "INSERT INTO products(name, x, y, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, x);
                pstmt.setInt(3, y);
                pstmt.setInt(4, quantity);
                pstmt.executeUpdate();
                gui.setStatus("Product added successfully", false);
                gui.clearInputFields();
                refreshProductTable();
            }
        } catch (NumberFormatException e) {
            gui.setStatus("Please enter valid numbers", true);
        } catch (SQLException e) {
            gui.setStatus("Error adding product: " + e.getMessage(), true);
        }
    }

    private static void orderProduct() {
        try {
            // Get selected product from dropdown
            String productName = gui.getOrderProductName();
            if (productName == null || productName.trim().isEmpty()) {
                gui.setStatus("Please select a product to order", true);
                return;
            }

            // Get order quantity
            String quantityStr = JOptionPane.showInputDialog(gui,
                    "Enter quantity to order:",
                    "Order Quantity",
                    JOptionPane.QUESTION_MESSAGE);

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                return;
            }

            int orderQuantity;
            try {
                orderQuantity = Integer.parseInt(quantityStr.trim());
                if (orderQuantity <= 0) {
                    gui.setStatus("Order quantity must be positive", true);
                    return;
                }
            } catch (NumberFormatException e) {
                gui.setStatus("Please enter a valid number for quantity", true);
                return;
            }

            // Find all nodes with this product
            List<Product> productNodes = new ArrayList<>();
            for (Product p : products) {
                if (p.name.equalsIgnoreCase(productName)) {
                    productNodes.add(p);
                }
            }

            if (productNodes.isEmpty()) {
                gui.setStatus("Product not found in warehouse", true);
                return;
            }

            // Calculate total available quantity
            int totalAvailable = 0;
            for (Product p : productNodes) {
                totalAvailable += p.quantity;
            }

            if (totalAvailable < orderQuantity) {
                gui.setStatus(String.format("Not enough stock. Available: %d, Requested: %d",
                        totalAvailable, orderQuantity), true);
                return;
            }

            // Sort nodes by distance from warehouse start node (0,0)
            productNodes.sort((p1, p2) -> {
                int dist1 = Math.abs(p1.x - START_NODE_X) + Math.abs(p1.y - START_NODE_Y);
                int dist2 = Math.abs(p2.x - START_NODE_X) + Math.abs(p2.y - START_NODE_Y);
                return Integer.compare(dist1, dist2);
            });

            // Process order
            int remainingOrder = orderQuantity;
            List<Integer> nodesToDelete = new ArrayList<>();
            StringBuilder orderInfo = new StringBuilder();
            orderInfo.append("=== ORDER DETAILS ===\n\n");
            orderInfo.append(String.format("Product: %s\n", productName));
            orderInfo.append(String.format("Total Quantity Ordered: %d\n", orderQuantity));
            orderInfo.append(String.format("Start Point: Row 0, Column 0\n\n"));
            orderInfo.append("=== PICKUP ROUTE (Nearest to Start) ===\n\n");

            for (Product node : productNodes) {
                if (remainingOrder <= 0)
                    break;

                int available = node.quantity;
                int toTake = Math.min(available, remainingOrder);
                remainingOrder -= toTake;

                // Calculate distance from start
                int distanceFromStart = Math.abs(node.x - START_NODE_X) + Math.abs(node.y - START_NODE_Y);

                // Add to order info with more detailed location information
                orderInfo.append(String.format("Stop #%d:\n", productNodes.indexOf(node) + 1));
                orderInfo.append(String.format("  Location: Row %d, Column %d\n", node.x + 1, node.y + 1));
                orderInfo.append(String.format("  Distance from Start: %d units\n", distanceFromStart));
                orderInfo.append(String.format("  Quantity to Pick: %d\n", toTake));
                orderInfo.append(String.format("  Remaining at Location: %d\n", available - toTake));

                // Update database
                String updateSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, toTake);
                    pstmt.setInt(2, node.id);
                    pstmt.executeUpdate();
                }

                // If quantity becomes 0, mark for deletion
                if (available - toTake == 0) {
                    nodesToDelete.add(node.id);
                    orderInfo.append("  Status: Location cleared (no items remaining)\n");
                } else {
                    orderInfo.append("  Status: Location still has stock\n");
                }
                orderInfo.append("\n");
            }

            // Delete nodes with zero quantity
            if (!nodesToDelete.isEmpty()) {
                String deleteSql = "DELETE FROM products WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    for (int nodeId : nodesToDelete) {
                        pstmt.setInt(1, nodeId);
                        pstmt.executeUpdate();
                    }
                }
            }

            // Add summary
            orderInfo.append("=== ORDER SUMMARY ===\n");
            orderInfo.append(String.format("Total stops needed: %d\n", productNodes.size()));
            orderInfo.append(String.format("Locations cleared: %d\n", nodesToDelete.size()));
            orderInfo.append(String.format("Locations with remaining stock: %d\n",
                    productNodes.size() - nodesToDelete.size()));
            orderInfo.append(String.format("Total distance to cover: %d units\n",
                    productNodes.stream()
                            .mapToInt(p -> Math.abs(p.x - START_NODE_X) + Math.abs(p.y - START_NODE_Y))
                            .sum()));

            // Show order info in the path info area
            gui.setPathInfo(orderInfo.toString());
            gui.setStatus(String.format("Order processed successfully. %d units of %s ordered.",
                    orderQuantity, productName), false);
            refreshProductTable();

        } catch (SQLException e) {
            gui.setStatus("Error processing order: " + e.getMessage(), true);
        }
    }

    private static void findPath(boolean useAStar) {
        try {
            int row = gui.getProductTable().getSelectedRow();
            if (row == -1) {
                gui.setStatus("Please select a source product", true);
                return;
            }

            String targetName = JOptionPane.showInputDialog(gui, "Enter target product name:");
            if (targetName == null || targetName.trim().isEmpty()) {
                return;
            }

            int sourceId = (int) gui.getTableModel().getValueAt(row, 0);

            // Validate source coordinates
            Product source = null;
            for (Product p : products) {
                if (p.id == sourceId) {
                    source = p;
                    break;
                }
            }

            if (source == null || source.x < 0 || source.x >= MAX_X || source.y < 0 || source.y >= MAX_Y) {
                gui.setStatus("Source product coordinates are invalid", true);
                return;
            }

            PathFinder.PathResult result = PathFinder.findShortestPath(products, sourceId, targetName, useAStar);

            if (result.path.isEmpty()) {
                gui.setStatus("No path found to target product", true);
                return;
            }

            // Validate path coordinates
            for (Point p : result.path) {
                if (p.x < 0 || p.x >= MAX_X || p.y < 0 || p.y >= MAX_Y) {
                    gui.setStatus("Invalid path generated - coordinates out of bounds", true);
                    return;
                }
            }

            // Find the target ID for visualization
            int targetId = -1;
            for (Product p : products) {
                if (p.name.equalsIgnoreCase(targetName)) {
                    Point lastPoint = result.path.get(result.path.size() - 1);
                    if (p.x == lastPoint.x && p.y == lastPoint.y) {
                        targetId = p.id;
                        break;
                    }
                }
            }

            // Update visualization
            gui.getVisualizationPanel().setProducts(products);
            gui.getVisualizationPanel().setPath(result.path, sourceId, targetId);

            // Update path info
            StringBuilder info = new StringBuilder();
            info.append(String.format("Algorithm: %s\n", result.algorithm));
            info.append(String.format("Path found in %.3f seconds\n", result.timeTaken / 1000.0));
            info.append(String.format("Total distance: %d units\n", result.distance));
            info.append("Path: ");
            for (int i = 0; i < result.path.size(); i++) {
                Point p = result.path.get(i);
                info.append(String.format("(%d,%d)", p.x, p.y));
                if (i < result.path.size() - 1)
                    info.append(" → ");
            }
            gui.setPathInfo(info.toString());
            gui.setStatus("Path found successfully using " + result.algorithm, false);
        } catch (Exception e) {
            gui.setStatus("Error finding path: " + e.getMessage(), true);
        }
    }

    private static void searchProducts() {
        try {
            String searchTerm = gui.getSearchField().getText().trim().toLowerCase();
            if (searchTerm.isEmpty()) {
                refreshProductTable();
                return;
            }

            // Clear existing data
            products.clear();
            gui.getTableModel().setRowCount(0);

            String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + searchTerm + "%");
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int quantity = rs.getInt("quantity");
                    Product product = new Product(id, name, x, y, quantity);
                    products.add(product);
                    gui.getTableModel().addRow(new Object[] { id, name, x, y, quantity });
                }
            }

            // Update visualization
            gui.getVisualizationPanel().setProducts(products);
            gui.getVisualizationPanel().clearPath();
            gui.setPathInfo("");

            if (products.isEmpty()) {
                gui.setStatus("No products found matching: " + searchTerm, true);
            } else {
                gui.setStatus("Found " + products.size() + " products matching: " + searchTerm, false);
            }
        } catch (SQLException e) {
            gui.setStatus("Error searching products: " + e.getMessage(), true);
        }
    }

    private static void findLowStockProducts() {
        try {
            // Ask user for the threshold
            String thresholdStr = JOptionPane.showInputDialog(gui,
                    "Enter quantity threshold for low stock alert (products with quantity <= threshold will be shown):",
                    "Low Stock Threshold",
                    JOptionPane.QUESTION_MESSAGE);

            if (thresholdStr == null || thresholdStr.trim().isEmpty()) {
                return;
            }

            int threshold;
            try {
                threshold = Integer.parseInt(thresholdStr.trim());
                if (threshold < 0) {
                    gui.setStatus("Threshold cannot be negative", true);
                    return;
                }
            } catch (NumberFormatException e) {
                gui.setStatus("Please enter a valid number for threshold", true);
                return;
            }

            // Clear existing data
            products.clear();
            gui.getTableModel().setRowCount(0);

            // Query products with quantity less than or equal to threshold
            String sql = "SELECT * FROM products WHERE quantity <= ? ORDER BY quantity ASC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, threshold);
                ResultSet rs = pstmt.executeQuery();

                boolean foundLowStock = false;
                while (rs.next()) {
                    foundLowStock = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int quantity = rs.getInt("quantity");

                    Product product = new Product(id, name, x, y, quantity);
                    products.add(product);
                    gui.getTableModel().addRow(new Object[] {
                            id, name, x, y, quantity
                    });

                    // If quantity is 0, highlight it with a warning
                    if (quantity == 0) {
                        gui.setStatus("WARNING: " + name + " is out of stock!", true);
                    }
                }

                if (!foundLowStock) {
                    gui.setStatus("No products found with quantity <= " + threshold, false);
                } else {
                    // Update visualization
                    gui.getVisualizationPanel().setProducts(products);

                    // Show summary in path info area
                    StringBuilder summary = new StringBuilder();
                    summary.append("Low Stock Summary (Threshold: ").append(threshold).append(")\n");
                    summary.append("Total low stock items: ").append(products.size()).append("\n");
                    summary.append("Products needing restock:\n");

                    for (Product p : products) {
                        summary.append(String.format("- %s (ID: %d): %d units\n",
                                p.name, p.id, p.quantity));
                    }

                    gui.setPathInfo(summary.toString());
                    gui.setStatus("Found " + products.size() + " products with low stock", false);
                }
            }
        } catch (SQLException e) {
            gui.setStatus("Error searching low stock products: " + e.getMessage(), true);
        }
    }

    private static void refreshProductTable() {
        try {
            products.clear();
            gui.getTableModel().setRowCount(0);
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int quantity = rs.getInt("quantity");
                    Product product = new Product(id, name, x, y, quantity);
                    products.add(product);
                }
            }

            // Sort products by quantity
            QuickSort.sort(products);

            // Update table with sorted products
            for (Product p : products) {
                gui.getTableModel().addRow(new Object[] {
                        p.id, p.name, p.x, p.y, p.quantity
                });
            }

            // Update both product dropdowns
            gui.updateProductDropdowns(products);

            gui.getVisualizationPanel().setProducts(products);
            gui.getVisualizationPanel().clearPath();
            gui.setPathInfo("");
        } catch (SQLException e) {
            gui.setStatus("Error refreshing table: " + e.getMessage(), true);
        }
    }
}
