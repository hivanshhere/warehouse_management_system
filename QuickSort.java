import java.util.List;

public class QuickSort {
    public static void sort(List<Product> products) {
        if (products == null || products.size() <= 1)
            return;
        quickSort(products, 0, products.size() - 1);
    }

    private static void quickSort(List<Product> products, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(products, low, high);
            quickSort(products, low, pivotIndex - 1);
            quickSort(products, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Product> products, int low, int high) {
        Product pivot = products.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (products.get(j).compareTo(pivot) <= 0) {
                i++;
                swap(products, i, j);
            }
        }

        swap(products, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Product> products, int i, int j) {
        Product temp = products.get(i);
        products.set(i, products.get(j));
        products.set(j, temp);
    }
}