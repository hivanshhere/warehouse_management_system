-- RapidRack Warehouse Management System Database Schema & Seed Data

CREATE DATABASE IF NOT EXISTS warehouse_db;
USE warehouse_db;

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    x INT,
    y INT,
    quantity INT DEFAULT 0,
    UNIQUE KEY coordinate_unique (x, y)
);

-- Sample Seed Products across 30x30 Warehouse Grid
INSERT INTO products (name, x, y, quantity) VALUES
('Laptop', 2, 3, 15),
('Laptop', 8, 12, 10),
('Laptop', 22, 18, 5),
('Smartphone', 3, 7, 28),
('Smartphone', 14, 9, 12),
('Headphones', 5, 2, 35),
('Headphones', 18, 22, 8),
('Monitor', 10, 4, 18),
('Monitor', 25, 25, 4),
('Keyboard', 4, 15, 45),
('Mouse', 1, 10, 50),
('Gaming PC', 20, 10, 6),
('Gaming Console', 12, 15, 14),
('Tablet', 6, 20, 22),
('VR Headset', 16, 5, 3),
('External Hard Drive', 7, 14, 30),
('Router', 11, 24, 16),
('Smart Watch', 3, 22, 19),
('Webcam', 9, 8, 2),
('Smart Speaker', 24, 7, 11)
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);
