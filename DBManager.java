import java.sql.*;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/warehouse_db";
    private static final String USER = "root";
    private static final String PASSWORD = "mittalsql@1234"; // Set your password here

    public static Connection getConnection() throws SQLException {
        Connection connect = DriverManager.getConnection(URL, USER, PASSWORD);
        createTableIfNotExists(connect);
        addQuantityColumnIfNotExists(connect);
        return connect;
    }

    private static void createTableIfNotExists(Connection connect) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "x INT, " +
                "y INT, " +
                "UNIQUE KEY coordinate_unique (x, y))";
        try (Statement stmt = connect.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void addQuantityColumnIfNotExists(Connection connect) throws SQLException {
        // Check if quantity column exists
        boolean columnExists = false;
        try (ResultSet rs = connect.getMetaData().getColumns(null, null, "products", "quantity")) {
            columnExists = rs.next();
        }

        // Add quantity column if it doesn't exist
        if (!columnExists) {
            String sql = "ALTER TABLE products ADD COLUMN quantity INT DEFAULT 0";
            try (Statement stmt = connect.createStatement()) {
                stmt.execute(sql);
                System.out.println("Added quantity column to products table");
            }
        }
    }
}
