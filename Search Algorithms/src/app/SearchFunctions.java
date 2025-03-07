package app;

import java.util.*;

public class SearchFunctions {

    // Represents a town node in the graph
    static class Town {
        String name;
        List<Town> adjacentTowns;

        Town(String name) {
            this.name = name;
            this.adjacentTowns = new ArrayList<>();
        }
    }

    // Main BFS function
    public static List<String> bfs(Map<String, Town> graph, String start, String end) {
        Town startTown = graph.get(start);
        Town endTown = graph.get(end);

        Queue<Town> queue = new LinkedList<>();
        Map<Town, Town> parentMap = new HashMap<>();
        Set<Town> visited = new HashSet<>();

        queue.offer(startTown);
        visited.add(startTown);

        while (!queue.isEmpty()) {
            Town current = queue.poll();

            if (current == endTown) {
                return reconstructPath(parentMap, startTown, endTown);
            }

            for (Town neighbor : current.adjacentTowns) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return null; // No path found
    }

    // Helper function to reconstruct the path
    private static List<String> reconstructPath(Map<Town, Town> parentMap, Town start, Town end) {
        List<String> path = new ArrayList<>();
        Town current = end;

        while (current != start) {
            path.add(current.name);
            current = parentMap.get(current);
        }
        path.add(start.name);

        Collections.reverse(path);
        return path;
    }

    // Helper function to build the graph from adjacency list
    public static Map<String, Town> buildGraph(List<String[]> adjacencies) {
        Map<String, Town> graph = new HashMap<>();

        for (String[] adjacency : adjacencies) {
            String town1 = adjacency[0];
            String town2 = adjacency[1];

            Town t1 = graph.computeIfAbsent(town1, Town::new);
            Town t2 = graph.computeIfAbsent(town2, Town::new);

            t1.adjacentTowns.add(t2);
            t2.adjacentTowns.add(t1);
        }

        return graph;
    }

    // Main DFS function
    public static List<String> dfs(Map<String, Town> graph, String start, String end) {
        Town startTown = graph.get(start);
        Town endTown = graph.get(end);

        Set<Town> visited = new HashSet<>();
        Map<Town, Town> parentMap = new HashMap<>();

        if (dfsRecursive(startTown, endTown, visited, parentMap)) {
            return reconstructPath(parentMap, startTown, endTown);
        }

        return null; // No path found
    }

    // Recursive helper function for DFS
    private static boolean dfsRecursive(Town current, Town end, Set<Town> visited, Map<Town, Town> parentMap) {
        if (current == end) {
            return true;
        }

        visited.add(current);

        for (Town neighbor : current.adjacentTowns) {
            if (!visited.contains(neighbor)) {
                parentMap.put(neighbor, current);
                if (dfsRecursive(neighbor, end, visited, parentMap)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Main ID-DFS function
    public static List<String> idDfs(Map<String, Town> graph, String start, String end) {
        Town startTown = graph.get(start);
        Town endTown = graph.get(end);

        for (int depth = 0; depth < graph.size(); depth++) {
            Map<Town, Town> parentMap = new HashMap<>();
            if (idDfsRecursive(startTown, endTown, depth, parentMap)) {
                return reconstructPath(parentMap, startTown, endTown);
            }
        }

        return null; // No path found
    }

    // Recursive helper function for ID-DFS
    private static boolean idDfsRecursive(Town current, Town end, int depth, Map<Town, Town> parentMap) {
        if (depth < 0) {
            return false;
        }
        if (current == end) {
            return true;
        }

        for (Town neighbor : current.adjacentTowns) {
            if (!parentMap.containsKey(neighbor)) {
                parentMap.put(neighbor, current);
                if (idDfsRecursive(neighbor, end, depth - 1, parentMap)) {
                    return true;
                }
                parentMap.remove(neighbor);
            }
        }

        return false;
    }

    // Function to calculate the total distance of a path
    public static double calculateTotalDistance(List<String> path, Map<String, double[]> coordinates) {
        double totalDistance = 0.0;
        if (path != null && path.size() > 1) {
            for (int i = 0; i < path.size() - 1; i++) {
                String city1 = path.get(i);
                String city2 = path.get(i + 1);
                if (coordinates.containsKey(city1) && coordinates.containsKey(city2)) {
                    double[] coord1 = coordinates.get(city1);
                    double[] coord2 = coordinates.get(city2);
                    totalDistance += distance(coord1[0], coord1[1], coord2[0], coord2[1]);
                } else {
                    System.out.println("Coordinates not found for one or more cities in the path.");
                    return -1.0; // Indicate an error
                }
            }
        }
        return totalDistance;
    }

    // Helper function to calculate the distance between two coordinates using Haversine formula
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    // Best-First Search implementation
    public static List<String> bestFirstSearch(Map<String, Town> graph, String start, String end, Map<String, double[]> coordinates) {
        Town startTown = graph.get(start);
        Town endTown = graph.get(end);

        if (startTown == null || endTown == null || !coordinates.containsKey(start) || !coordinates.containsKey(end)) {
            System.out.println("Start, end, or coordinate data is missing.");
            return null;
        }

        PriorityQueue<Town> queue = new PriorityQueue<>(Comparator.comparingDouble(town -> heuristic(town, end, coordinates)));
        Map<Town, Town> parentMap = new HashMap<>();
        Set<Town> visited = new HashSet<>();

        queue.offer(startTown);
        visited.add(startTown);

        while (!queue.isEmpty()) {
            Town current = queue.poll();

            if (current == endTown) {
                return reconstructPath(parentMap, startTown, endTown);
            }

            for (Town neighbor : current.adjacentTowns) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return null; // No path found
    }

    // Heuristic function (Haversine distance)
    private static double heuristic(Town town, String end, Map<String, double[]> coordinates) {
        if (!coordinates.containsKey(town.name) || !coordinates.containsKey(end)) {
            return Double.MAX_VALUE; // Return a large value if coordinates are missing
        }

        double[] coord1 = coordinates.get(town.name);
        double[] coord2 = coordinates.get(end);
        return distance(coord1[0], coord1[1], coord2[0], coord2[1]);
    }

    // A* Search implementation
    public static List<String> aStarSearch(Map<String, Town> graph, String start, String end, Map<String, double[]> coordinates) {
        Town startTown = graph.get(start);
        Town endTown = graph.get(end);

        if (startTown == null || endTown == null || !coordinates.containsKey(start) || !coordinates.containsKey(end)) {
            System.out.println("Start, end, or coordinate data is missing.");
            return null;
        }

        PriorityQueue<Town> queue = new PriorityQueue<>(Comparator.comparingDouble(town -> fScore(town, end, coordinates, start, graph)));
        Map<Town, Town> parentMap = new HashMap<>();
        Map<Town, Double> gScore = new HashMap<>();
        gScore.put(startTown, 0.0);
        Set<Town> visited = new HashSet<>();

        queue.offer(startTown);
        visited.add(startTown);

        while (!queue.isEmpty()) {
            Town current = queue.poll();

            if (current == endTown) {
                return reconstructPath(parentMap, startTown, endTown);
            }

            for (Town neighbor : current.adjacentTowns) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    gScore.put(neighbor, gScore.get(current) + distance(coordinates.get(current.name)[0], coordinates.get(current.name)[1], coordinates.get(neighbor.name)[0], coordinates.get(neighbor.name)[1]));
                    queue.offer(neighbor);
                } else {
                    double newGScore = gScore.get(current) + distance(coordinates.get(current.name)[0], coordinates.get(current.name)[1], coordinates.get(neighbor.name)[0], coordinates.get(neighbor.name)[1]);
                    if (newGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                        gScore.put(neighbor, newGScore);
                        parentMap.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return null; // No path found
    }

    // Helper function to calculate f-score for A*
    private static double fScore(Town town, String end, Map<String, double[]> coordinates, String start, Map<String, Town> graph) {
        double g = gScore(town, coordinates, start, graph);
        double h = heuristic(town, end, coordinates);
        return g + h;
    }

    // Helper function to calculate g-score for A*
    private static double gScore(Town town, Map<String, double[]> coordinates, String start, Map<String, Town> graph) {
        double g = 0.0;
        Town current = town;
        Map<Town, Town> parentMap = new HashMap<>();
        buildParentMap(current, start, graph, parentMap);
        while (parentMap.containsKey(current)) {
            Town parent = parentMap.get(current);
            g += distance(coordinates.get(current.name)[0], coordinates.get(current.name)[1], coordinates.get(parent.name)[0], coordinates.get(parent.name)[1]);
            current = parent;
        }
        return g;
    }

    // Helper function to build the parent map
    private static void buildParentMap(Town town, String start, Map<String, Town> graph, Map<Town, Town> parentMap) {
        Town current = town;
        while (current.name.equals(start)) {
            for (Town neighbor : current.adjacentTowns) {
                parentMap.put(current, neighbor);
                buildParentMap(neighbor, start, graph, parentMap);
            }
        }
    }
}