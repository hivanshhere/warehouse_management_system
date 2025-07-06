import java.awt.Point;
import java.util.*;

public class ProductGrid {
    private static final int GRID_WIDTH = 50;
    private static final int GRID_HEIGHT = 50;

    // Main grid storage using 2D array for quick coordinate lookup
    private Product[][] grid;

    // Additional data structures for efficient operations
    private Map<Integer, Product> productById;
    private Map<String, List<Product>> productsByName;
    private List<Product> allProducts;

    public ProductGrid() {
        grid = new Product[GRID_WIDTH][GRID_HEIGHT];
        productById = new HashMap<>();
        productsByName = new HashMap<>();
        allProducts = new ArrayList<>();
    }

    public boolean addProduct(Product product) {
        // Validate coordinates
        if (!isValidCoordinate(product.x, product.y)) {
            return false;
        }

        // Check if position is already occupied
        if (grid[product.x][product.y] != null) {
            return false;
        }

        // Add to grid
        grid[product.x][product.y] = product;

        // Add to other data structures
        productById.put(product.id, product);
        productsByName.computeIfAbsent(product.name.toLowerCase(), k -> new ArrayList<>()).add(product);
        allProducts.add(product);

        return true;
    }

    public boolean removeProduct(int productId) {
        Product product = productById.get(productId);
        if (product == null) {
            return false;
        }

        // Remove from grid
        grid[product.x][product.y] = null;

        // Remove from other data structures
        productById.remove(productId);
        productsByName.get(product.name.toLowerCase()).remove(product);
        allProducts.remove(product);

        return true;
    }

    public Product getProductAt(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            return null;
        }
        return grid[x][y];
    }

    public Product getProductById(int id) {
        return productById.get(id);
    }

    public List<Product> getProductsByName(String name) {
        return productsByName.getOrDefault(name.toLowerCase(), new ArrayList<>());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts);
    }

    public List<Product> getProductsInArea(int startX, int startY, int endX, int endY) {
        List<Product> products = new ArrayList<>();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (isValidCoordinate(x, y) && grid[x][y] != null) {
                    products.add(grid[x][y]);
                }
            }
        }
        return products;
    }

    public List<Product> getLowStockProducts(int threshold) {
        List<Product> lowStock = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.quantity <= threshold) {
                lowStock.add(product);
            }
        }
        return lowStock;
    }

    public boolean moveProduct(int productId, int newX, int newY) {
        Product product = productById.get(productId);
        if (product == null || !isValidCoordinate(newX, newY) || grid[newX][newY] != null) {
            return false;
        }

        // Remove from old position
        grid[product.x][product.y] = null;

        // Update coordinates
        product.x = newX;
        product.y = newY;

        // Add to new position
        grid[newX][newY] = product;

        return true;
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT;
    }

    public boolean isPositionOccupied(int x, int y) {
        return isValidCoordinate(x, y) && grid[x][y] != null;
    }

    public int getGridWidth() {
        return GRID_WIDTH;
    }

    public int getGridHeight() {
        return GRID_HEIGHT;
    }

    public void clear() {
        grid = new Product[GRID_WIDTH][GRID_HEIGHT];
        productById.clear();
        productsByName.clear();
        allProducts.clear();
    }

    // Helper method to find nearest empty position
    public Point findNearestEmptyPosition(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            return null;
        }

        // If current position is empty, return it
        if (!isPositionOccupied(x, y)) {
            return new Point(x, y);
        }

        // Search in expanding squares around the point
        for (int radius = 1; radius < Math.max(GRID_WIDTH, GRID_HEIGHT); radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    // Only check the perimeter of the square
                    if (Math.abs(dx) == radius || Math.abs(dy) == radius) {
                        int newX = x + dx;
                        int newY = y + dy;
                        if (isValidCoordinate(newX, newY) && !isPositionOccupied(newX, newY)) {
                            return new Point(newX, newY);
                        }
                    }
                }
            }
        }

        return null; // No empty position found
    }
}