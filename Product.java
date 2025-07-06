public class Product implements Comparable<Product> {
    public int id;
    public String name;
    public int x, y;
    public int quantity;

    public Product(int id, String name, int x, int y, int quantity) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.quantity = quantity;
    }

    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.quantity, other.quantity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
