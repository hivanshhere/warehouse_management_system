# Contributing to RapidRack

Thank you for working on this project! This guide explains how to contribute changes
regularly while the project is in active development.

---

## Setting Up Your Local Environment

1. **Install Java JDK 17+** – https://adoptium.net/
2. **Install Git** – https://git-scm.com/
3. **Install VS Code** with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
4. **Clone the repository:**
   ```bash
   git clone https://github.com/hivanshhere/warehouse_management_system.git
   cd warehouse_management_system
   ```

---

## Day-to-Day Workflow

### Before you start coding

Always pull the latest changes from GitHub first:

```bash
git pull
```

### Making changes

1. Edit your `.java` files in VS Code.
2. Compile and test locally:
   ```bash
   javac *.java
   java WarehouseApp
   ```
3. Fix any compilation errors before committing.

### Saving and uploading your changes

```bash
git add .                              # Stage all changed files
git commit -m "Short description"      # Commit locally
git push                               # Push to GitHub
```

Or use the **Source Control panel** in VS Code (see README for details).

---

## Branch Strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, working code only |
| `feature/<name>` | New features under development |
| `fix/<name>` | Bug fixes |

**Create a new branch for significant features:**

```bash
git checkout -b feature/product-search
# ...make and commit your changes...
git push -u origin feature/product-search
```

When the feature is ready, open a **Pull Request** on GitHub to merge it into `main`.

---

## Commit Message Guidelines

Write commit messages in the present tense and keep them short (under 72 characters).

```
Add QuickSort integration for product list
Fix crash when warehouse grid is empty
Update README with setup instructions
Refactor DBManager to use connection pooling
```

Avoid vague messages like `fix`, `update`, `changes`, or `wip`.

---

## What NOT to Commit

The `.gitignore` file already excludes these, but be aware of them:

- Compiled files: `*.class`
- IDE configuration folders: `.idea/`, `.vscode/`, `.settings/`
- Build output: `bin/`, `out/`, `build/`, `target/`
- OS-generated files: `.DS_Store`, `Thumbs.db`

Run `git status` before committing to confirm you are only staging source files.

---

## Getting Help

If you run into problems with Git or GitHub, refer to:

- [GitHub Docs: Getting Started](https://docs.github.com/en/get-started)
- [VS Code Source Control Guide](https://code.visualstudio.com/docs/sourcecontrol/overview)
