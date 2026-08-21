# RapidRack – Warehouse Management System

**RapidRack: Fast, Smart, and Scalable Warehouse Management System** is a Java-based warehouse simulation and management application designed to optimize inventory organization, product retrieval, and movement within a warehouse.

The system applies **Data Structures and Algorithms** to real-world warehouse operations. It uses **Dijkstra’s Algorithm** and **A* (A-Star) Algorithm** to find efficient paths between warehouse locations and **QuickSort** to organize products efficiently.

A user-friendly **Java Swing GUI** allows warehouse staff and administrators to interact with the system through a simple graphical interface.

---

## Features

### Intelligent Pathfinding

* **Dijkstra’s Algorithm** for shortest-path calculation.
* **A* Algorithm** for efficient heuristic-based pathfinding.
* Visual representation of calculated warehouse paths.

### Inventory Management

* Add and manage warehouse products.
* Search and organize product information.
* Database-backed inventory management using MySQL.

### Product Sorting

* Uses **QuickSort** to organize products based on relevant attributes such as priority or category.

### Warehouse Operations

* Simulates product storage and retrieval.
* Uses data structures such as **Queues and Stacks** for warehouse operations.
* Represents warehouse locations and product positions through a structured grid.

### Graphical User Interface

* Built using **Java Swing**.
* Simple and user-friendly interface.
* Path visualization for better understanding of warehouse movement.
* Designed for both warehouse staff and administrators.

---

## Technologies Used

| Technology               | Purpose                        |
| ------------------------ | ------------------------------ |
| **Java**                 | Core application development   |
| **Java Swing**           | Graphical User Interface       |
| **MySQL**                | Inventory and product database |
| **JDBC**                 | Java–MySQL connectivity        |
| **Dijkstra’s Algorithm** | Shortest-path calculation      |
| **A* Algorithm**         | Heuristic pathfinding          |
| **QuickSort**            | Product sorting                |
| **Queues & Stacks**      | Warehouse operation management |

---

## Project Structure

```text
warehouse_management_system-main/
│
├── src/                                  # Java source files
│   ├── WarehouseApp.java                 # Application entry point
│   ├── WarehouseGUI.java                 # Main Swing GUI
│   ├── PathVisualizationPanel.java       # Warehouse path visualization
│   ├── Product.java                      # Product model
│   ├── DBManager.java                    # MySQL database management
│   ├── PathFinder.java                   # Pathfinding interface/base logic
│   ├── AStarPathFinder.java              # A* pathfinding implementation
│   ├── Dijkstra.java                     # Dijkstra's algorithm
│   ├── QuickSort.java                    # Product sorting algorithm
│   └── ProductGrid.java                  # Warehouse/product grid
│
├── lib/                                  # External JAR dependencies
│   └── mysql-connector-j-8.3.0.jar       # MySQL JDBC driver
│
├── bin/                                  # Generated .class bytecode
│
├── sql/                                  # Database scripts
│   └── schema.sql                        # Database schema & sample data
│
├── run.bat                               # Windows one-click launcher
├── run.ps1                               # PowerShell launcher
├── .gitignore                            # Git ignored files
└── README.md                             # Project documentation
```

---

## Requirements

Before running RapidRack, make sure the following are installed:

* **Java JDK 8 or later**
* **MySQL Server**
* **MySQL Workbench** *(recommended for database setup)*
* Windows operating system

The project already includes the required **MySQL Connector/J 8.3.0** JAR inside the `lib` folder.

---

## Database Setup

RapidRack uses MySQL to store and manage product information.

### 1. Start MySQL

Make sure your MySQL server is running.

### 2. Create the Database

Open **MySQL Workbench** or the MySQL command line and execute:

```sql
CREATE DATABASE warehouse_management_system;
```

### 3. Run the Schema

Open:

```text
sql/schema.sql
```

Execute the SQL script to create the required tables and insert the sample data.

### 4. Configure Database Credentials

Open:

```text
src/DBManager.java
```

Make sure the database configuration matches your local MySQL setup, including:

* Database URL
* MySQL username
* MySQL password
* Database name

---

## Running the Project

### Option 1 – Windows Launcher

The easiest way to run the project on Windows is:

```text
run.bat
```

Double-click `run.bat` from the project folder.

It will compile the Java source files and launch the application.

### Option 2 – PowerShell

Open PowerShell inside the project directory and run:

```powershell
.\run.ps1
```

### Option 3 – Manual Compilation

You can also compile and run the project manually.

Compile:

```powershell
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d bin src/*.java
```

Run:

```powershell
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" WarehouseApp
```

> The commands above are intended for **Windows**, since the project uses Windows launcher scripts and Windows classpath separators.

---

## How the System Works

RapidRack represents the warehouse as a structured grid containing different product locations.

When a product needs to be retrieved or stored, the system can calculate a suitable path between the required locations.

### Dijkstra's Algorithm

Dijkstra's Algorithm calculates the shortest path between warehouse locations based on the available paths and their costs.

### A* Algorithm

A* improves pathfinding by using a heuristic to estimate the distance to the destination, allowing it to focus the search toward the target.

### QuickSort

QuickSort is used to efficiently organize products according to the required sorting criteria.

### Database Management

The `DBManager.java` class handles communication between the Java application and the MySQL database using **JDBC**.

---

## Main Components

### `WarehouseApp.java`

The main entry point of the application. It starts the RapidRack system.

### `WarehouseGUI.java`

Contains the primary Java Swing interface through which users interact with the warehouse system.

### `PathVisualizationPanel.java`

Responsible for visually displaying warehouse paths and movement.

### `Product.java`

Represents product-related information used throughout the application.

### `DBManager.java`

Handles database connectivity and database-related operations using MySQL and JDBC.

### `PathFinder.java`

Provides the base structure for pathfinding functionality.

### `Dijkstra.java`

Contains the implementation of Dijkstra's shortest-path algorithm.

### `AStarPathFinder.java`

Contains the implementation of the A* pathfinding algorithm.

### `QuickSort.java`

Implements QuickSort for efficient product organization.

### `ProductGrid.java`

Manages the warehouse/product grid used for representing product locations.

---

## Project Objective

The main objective of RapidRack is to demonstrate the practical use of **Data Structures, Algorithms, Database Management, and GUI Development** in a warehouse management scenario.

The system is designed to:

* Optimize warehouse navigation.
* Improve product organization.
* Reduce manual effort.
* Provide efficient product retrieval paths.
* Demonstrate real-world applications of DSA concepts.
* Provide a simple interface for warehouse operations.

---

## Key DSA Concepts Used

| Concept                  | Application                           |
| ------------------------ | ------------------------------------- |
| **Dijkstra's Algorithm** | Shortest warehouse path               |
| **A* Algorithm**         | Heuristic-based pathfinding           |
| **QuickSort**            | Product sorting                       |
| **Queue**                | Sequential warehouse/order operations |
| **Stack**                | LIFO-based operations                 |
| **Graph/Grid**           | Warehouse layout representation       |

---

## Future Scope

Possible future improvements include:

* Real-time inventory tracking.
* Barcode or QR-code integration.
* Automated warehouse robot integration.
* Advanced inventory analytics.
* Role-based authentication.
* More advanced warehouse visualization.
* Web or mobile-based access.

---

## Conclusion

**RapidRack** combines Java, MySQL, Java Swing, and fundamental Data Structures and Algorithms to create a practical warehouse management simulation.

By using intelligent pathfinding algorithms, efficient sorting techniques, database management, and a graphical interface, the project demonstrates how core computer science concepts can be applied to solve real-world warehouse management problems.
