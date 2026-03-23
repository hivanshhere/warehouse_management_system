# RapidRack - Warehouse Management System

> **Status: In active development**

RapidRack is a Java-based warehouse management system designed to simplify and optimize storage and retrieval operations. It uses smart pathfinding algorithms, efficient sorting, and a clean Swing GUI to simulate how a real warehouse works.

---

## Features

| Feature | Details |
|---|---|
| Pathfinding | Dijkstra's Algorithm and A* (A-Star) for shortest-path routing |
| Sorting | QuickSort for organizing items by priority or category |
| Data structures | HashMaps, Queues, and Stacks for inventory management |
| GUI | Java Swing interface for admins and warehouse staff |
| Database | DBManager for persistent product storage |

---

## Prerequisites

- **Java JDK 17** or later (https://adoptium.net/)
- **Git** (https://git-scm.com/)
- **VS Code** with the Extension Pack for Java (recommended)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/hivanshhere/warehouse_management_system.git
cd warehouse_management_system
```

### 2. Compile the source files

```bash
javac *.java
```

### 3. Run the application

```bash
java WarehouseApp
```

---

## Project Structure

```
warehouse_management_system/
├── WarehouseApp.java           # Entry point
├── WarehouseGUI.java           # Main Swing GUI
├── Product.java                # Product model
├── ProductGrid.java            # Grid display of products
├── DBManager.java              # Database / persistence layer
├── PathFinder.java             # Base pathfinding interface
├── AStarPathFinder.java        # A* pathfinding implementation
├── Dijkstra.java               # Dijkstra's pathfinding implementation
├── PathVisualizationPanel.java # Visual path display panel
├── QuickSort.java              # QuickSort utility
└── README.md
```

---

## GitHub Workflow for Regular Development (VS Code)

Because this project is in active development, here is the recommended workflow for making and tracking changes using **VS Code** and **GitHub**.

### One-time setup in VS Code

1. Open VS Code and open the cloned project folder with **File -> Open Folder**.
2. Click the **Source Control** icon in the left sidebar (or press `Ctrl+Shift+G`).
3. Sign in to GitHub inside VS Code when prompted.

### Every time you work on the project

```
Edit code in VS Code
      |
      v
Stage your changes  (git add)
      |
      v
Commit with a message  (git commit)
      |
      v
Push to GitHub  (git push)
```

**Using the VS Code Source Control panel:**

1. Make your code changes and save the files.
2. Open the **Source Control** panel - changed files appear under "Changes".
3. Click the **+** icon next to each file (or next to "Changes") to *stage* them.
4. Type a short commit message in the text box, e.g. `Add product search feature`.
5. Click the **checkmark (Commit)** button.
6. Click **Sync Changes** (the cloud icon) to push to GitHub.

**Using the VS Code integrated terminal (`Ctrl+` `):**

```bash
git add .                             # Stage all changed files
git commit -m "Describe your change"  # Save a snapshot locally
git push                              # Upload to GitHub
```

### Good commit message examples

| Good | Avoid |
|---|---|
| `Add A* pathfinding for aisle navigation` | `update` |
| `Fix null pointer in DBManager.loadProducts` | `changes` |
| `Improve QuickSort for large product grids` | `asdfgh` |

### Tips for working on an in-progress project

- **Commit often** - even small or incomplete changes. You can always improve them later.
- **Pull before you start** each session to get the latest version:
  ```bash
  git pull
  ```
- **Use branches** for big new features so the `main` branch stays stable:
  ```bash
  git checkout -b feature/new-gui-panel   # create and switch to new branch
  # ...make changes and commit...
  git push -u origin feature/new-gui-panel
  ```
- **Never commit compiled files** - the `.gitignore` in this repo already excludes `.class` files, IDE folders, and build output directories.

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full contribution guide.

---

## License

This project is open source and available for educational use.
