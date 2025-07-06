import java.awt.Point;
import java.util.*;

public class PathFinder {
    private static class Node implements Comparable<Node> {
        int id;
        int distance;

        Node(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static class PathResult {
        public List<Point> path;
        public long timeTaken;
        public int distance;
        public String algorithm;
        public String errorMessage;

        public PathResult(List<Point> path, long timeTaken, int distance, String algorithm) {
            this.path = path;
            this.timeTaken = timeTaken;
            this.distance = distance;
            this.algorithm = algorithm;
            this.errorMessage = "";
        }

        public PathResult(List<Point> path, long timeTaken, int distance, String algorithm, String errorMessage) {
            this.path = path;
            this.timeTaken = timeTaken;
            this.distance = distance;
            this.algorithm = algorithm;
            this.errorMessage = errorMessage;
        }
    }

    private static int getDistance(Point p1, Point p2) {
        // Using Manhattan distance for grid-based movement
        return Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y);
    }

    private static boolean isValidCoordinate(Point p) {
        boolean valid = p.x >= 0 && p.x < 50 && p.y >= 0 && p.y < 50;
        if (!valid) {
            System.out.println("Invalid coordinate: (" + p.x + "," + p.y + ")");
            System.out.println("Valid range: x[0-49], y[0-49]");
        }
        return valid;
    }

    private static int[][] buildProductGraph(List<Product> products) {
        int n = products.size();
        int[][] graph = new int[n][n];

        // Build graph with distances between all products
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Product p1 = products.get(i);
                    Product p2 = products.get(j);
                    Point point1 = new Point(p1.x, p1.y);
                    Point point2 = new Point(p2.x, p2.y);

                    // Only add edge if both points are within grid limits
                    if (isValidCoordinate(point1) && isValidCoordinate(point2)) {
                        graph[i][j] = getDistance(point1, point2);
                    } else {
                        graph[i][j] = Integer.MAX_VALUE; // No direct path if out of bounds
                    }
                }
            }
        }
        return graph;
    }

    private static List<Point> buildPathThroughProducts(List<Product> products, List<Integer> productPath) {
        List<Point> path = new ArrayList<>();

        if (productPath.isEmpty()) {
            return path;
        }

        for (int i = 0; i < productPath.size(); i++) {
            Product current = products.get(productPath.get(i));
            Point currentPoint = new Point(current.x, current.y);

            if (!isValidCoordinate(currentPoint)) {
                return new ArrayList<>(); // Return empty path if any point is invalid
            }

            if (i == 0) {
                path.add(currentPoint);
                continue;
            }

            Product prev = products.get(productPath.get(i - 1));
           

            // Calculate the direct path between points
            int dx = current.x - prev.x;
            int dy = current.y - prev.y;

            // Move horizontally first, then vertically
            if (dx != 0) {
                int step = dx > 0 ? 1 : -1;
                for (int x = prev.x; x != current.x; x += step) {
                    Point p = new Point(x, prev.y);
                    if (!isValidCoordinate(p)) {
                        return new ArrayList<>(); // Return empty path if any point is invalid
                    }
                    path.add(p);
                }
            }

            if (dy != 0) {
                int step = dy > 0 ? 1 : -1;
                for (int y = prev.y; y != current.y; y += step) {
                    Point p = new Point(current.x, y);
                    if (!isValidCoordinate(p)) {
                        return new ArrayList<>(); // Return empty path if any point is invalid
                    }
                    path.add(p);
                }
            }

            path.add(currentPoint);
        }

        return path;
    }

    public static PathResult findShortestPath(List<Product> products, int sourceId, String targetName,
            boolean useAStar) {
        if (useAStar) {
            return AStarPathFinder.findShortestPath(products, sourceId, targetName);
        }

        long startTime = System.currentTimeMillis();

        // Find source product and potential targets
        Product source = null;
        int sourceIndex = -1;
        List<Integer> targetIndices = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.id == sourceId) {
                source = p;
                sourceIndex = i;
                System.out.println("Found source product: ID=" + p.id + ", Name=" + p.name +
                        ", Coordinates=(" + p.x + "," + p.y + ")");
            }
            if (p.name.equalsIgnoreCase(targetName)) {
                targetIndices.add(i);
                System.out.println("Found target product: ID=" + p.id + ", Name=" + p.name +
                        ", Coordinates=(" + p.x + "," + p.y + ")");
            }
        }

        if (source == null || targetIndices.isEmpty()) {
            String error = source == null ? "Source product not found" : "No target products found";
            return new PathResult(new ArrayList<>(), 0, 0, "Dijkstra", error);
        }

        // Validate source coordinates
        Point sourcePoint = new Point(source.x, source.y);
        if (!isValidCoordinate(sourcePoint)) {
            return new PathResult(new ArrayList<>(), 0, 0, "Dijkstra",
                    "Source coordinates (" + source.x + "," + source.y + ") are invalid");
        }

        // Validate target coordinates
        boolean hasValidTarget = false;
        for (int targetIndex : targetIndices) {
            Product target = products.get(targetIndex);
            Point targetPoint = new Point(target.x, target.y);
            if (isValidCoordinate(targetPoint)) {
                hasValidTarget = true;
                break;
            }
        }

        if (!hasValidTarget) {
            return new PathResult(new ArrayList<>(), 0, 0, "Dijkstra",
                    "No target products with valid coordinates found");
        }

        // Build the graph
        int[][] graph = buildProductGraph(products);

        // Use Dijkstra's algorithm
        int n = products.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[sourceIndex] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.offer(new Node(sourceIndex, 0));

        // Find shortest paths to all nodes
        while (!pq.isEmpty()) {
            Node current = pq.poll();

            for (int i = 0; i < n; i++) {
                if (graph[current.id][i] > 0 && graph[current.id][i] != Integer.MAX_VALUE) {
                    int newDist = dist[current.id] + graph[current.id][i];
                    if (newDist < dist[i]) {
                        dist[i] = newDist;
                        prev[i] = current.id;
                        pq.offer(new Node(i, newDist));
                    }
                }
            }
        }

        // Find the nearest target
        int nearestTargetIndex = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int targetIndex : targetIndices) {
            if (dist[targetIndex] < minDistance) {
                minDistance = dist[targetIndex];
                nearestTargetIndex = targetIndex;
            }
        }

        if (nearestTargetIndex == -1) {
            return new PathResult(new ArrayList<>(), 0, 0, "Dijkstra",
                    "No path found to any target product");
        }

        // Build path to nearest target
        List<Integer> productPath = new ArrayList<>();
        int current = nearestTargetIndex;
        while (current != -1) {
            productPath.add(0, current);
            current = prev[current];
        }

        List<Point> path = buildPathThroughProducts(products, productPath);
        long timeTaken = System.currentTimeMillis() - startTime;

        return new PathResult(path, timeTaken, minDistance, "Dijkstra");
    }
}