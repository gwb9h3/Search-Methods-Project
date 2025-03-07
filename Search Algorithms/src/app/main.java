package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load data
        List<String[]> adjacencies = loadAdjacencies("./Adjacencies.txt");
        Map<String, SearchFunctions.Town> graph = SearchFunctions.buildGraph(adjacencies);
        Map<String, double[]> coordinates = loadCoordinates("./coordinates.csv"); // Load coordinates

        // City input validation
        String startCity = getValidCity(scanner, graph, "Enter the starting city: ");
        String endCity = getValidCity(scanner, graph, "Enter the destination city: ");
        System.out.println("");

        // Main loop to allow multiple searches
        boolean continueSearching = true;
        while (continueSearching) {
            // Search method selection
            int searchMethod = getValidSearchMethod(scanner);

            // Perform search
            List<String> path = null;
            long startTime = System.nanoTime();
            switch (searchMethod) {
                case 1:
                    path = SearchFunctions.bfs(graph, startCity, endCity);
                    break;
                case 2:
                    path = SearchFunctions.dfs(graph, startCity, endCity);
                    break;
                case 3:
                    path = SearchFunctions.idDfs(graph, startCity, endCity);
                    break;
                case 4:
                    path = SearchFunctions.bestFirstSearch(graph, startCity, endCity, coordinates);
                    break;
                case 5:
                    path = SearchFunctions.aStarSearch(graph, startCity, endCity, coordinates);
                    break;
                default:
                    System.out.println("Invalid search method selected.");
                    return;
            }
            long endTime = System.nanoTime();
            long durationNano = (endTime - startTime);
            double durationMillis = durationNano / 1000000.0; // Convert nanoseconds to milliseconds

            // Print the results
            if (path != null) {
                System.out.println("Path found: " + path);
                System.out.printf("Time: %.3f ms%n", durationMillis); // Output in milliseconds

                // Calculate and print the total distance
                double totalDistance = SearchFunctions.calculateTotalDistance(path, coordinates);
                if (totalDistance >= 0) {
                    System.out.printf("Total distance: %.2f km%n", totalDistance);
                } else {
                    System.out.println("Could not calculate total distance due to missing coordinates.");
                }
            } else {
                System.out.println("No path found.");
            }
            
            // Ask if the user wants to try another search
            System.out.println("");
            System.out.print("Do you want to try another search with the same cities? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            continueSearching = answer.equals("yes");
            System.out.println("");
        }

        System.out.println("Exiting program.");
        scanner.close();
    }

    // Helper method to load adjacencies from Adjacencies.txt
    private static List<String[]> loadAdjacencies(String filename) {
        List<String[]> adjacencies = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] towns = line.split(" ");
                adjacencies.add(towns);
            }
        } catch (IOException e) {
            System.err.println("Error reading adjacencies file: " + e.getMessage());
        }
        return adjacencies;
    }

     // Helper method to load coordinates from coordinates-1.csv
    private static Map<String, double[]> loadCoordinates(String filename) {
        Map<String, double[]> coordinates = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String city = parts[0];
                    double latitude = Double.parseDouble(parts[1]);
                    double longitude = Double.parseDouble(parts[2]);
                    coordinates.put(city, new double[]{latitude, longitude});
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading coordinates file: " + e.getMessage());
        }
        return coordinates;
    }

    private static String getValidCity(Scanner scanner, Map<String, SearchFunctions.Town> graph, String prompt) {
        String city;
        while (true) {
            System.out.print(prompt);
            city = scanner.nextLine().trim();
            if (graph.containsKey(city)) {
                break;
            } else {
                System.out.println("Invalid city. Please enter a city from the available list.");
            }
        }
        return city;
    }

    private static int getValidSearchMethod(Scanner scanner) {
        int method;
        while (true) {
            System.out.println("Choose a search method:");
            System.out.println("1. Breadth-First Search");
            System.out.println("2. Depth-First Search");
            System.out.println("3. Iterative Deepening Depth-First Search");
            System.out.println("4. Best-First Search");
            System.out.println("5. A* Search\n");
            System.out.print("Enter the number of your choice (1-5): ");

            try {
                method = Integer.parseInt(scanner.nextLine().trim());
                if (method >= 1 && method <= 5) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return method;
    }

}
