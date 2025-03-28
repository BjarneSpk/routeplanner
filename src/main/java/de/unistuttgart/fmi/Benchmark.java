package de.unistuttgart.fmi;

import de.unistuttgart.fmi.graph.Graph;
import de.unistuttgart.fmi.graph.InvalidGraphException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Benchmark {

    public static void main(String[] args) throws InvalidGraphException {
        // read parameters (parameters are expected in exactly this order)
        String graphPath = args[1];
        double lat = Double.parseDouble(args[3]);
        double lon = Double.parseDouble(args[5]);
        String quePath = args[7];
        int sourceNodeId = Integer.parseInt(args[9]);

        // run benchmarks
        System.out.println("Reading graph file and creating graph data structure (" + graphPath + ")");
        long graphReadStart = System.currentTimeMillis();

        Graph graph = Graph.from(graphPath);

        long graphReadEnd = System.currentTimeMillis();
        System.out.println("\tgraph read took " + (graphReadEnd - graphReadStart) + "ms");

        System.out.println("Finding closest node to coordinates " + lat + " " + lon);
        long nodeFindStart = System.currentTimeMillis();

        double[] coords = graph.getNearestNeighbour(new double[] {lat, lon});

        long nodeFindEnd = System.currentTimeMillis();
        System.out.println("\tfinding node took " + (nodeFindEnd - nodeFindStart) + "ms: " + coords[0] + ", "
                + coords[1] + " id: " + (int) coords[2]);

        System.out.println("Running one-to-one Dijkstras for queries in .que file " + quePath);
        long queStart = System.currentTimeMillis();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(quePath))) {
            String currLine;
            while ((currLine = bufferedReader.readLine()) != null) {
                int oneToOneSourceNodeId = Integer.parseInt(currLine.substring(0, currLine.indexOf(" ")));
                int oneToOneTargetNodeId = Integer.parseInt(currLine.substring(currLine.indexOf(" ") + 1));

                int oneToOneDistance =
                        graph.getClosestPathFinder().getShortestPath(oneToOneSourceNodeId, oneToOneTargetNodeId);

                System.out.println(oneToOneDistance);
            }
        } catch (Exception e) {
            System.out.println("Exception...");
            e.printStackTrace();
        }
        long queEnd = System.currentTimeMillis();
        System.out.println("\tprocessing .que file took " + (queEnd - queStart) + "ms");

        System.out.println("Computing one-to-all Dijkstra from node id " + sourceNodeId);
        long oneToAllStart = System.currentTimeMillis();

        var closesPath = graph.getClosestPathFinder();
        closesPath.getShortestPath(sourceNodeId);

        long oneToAllEnd = System.currentTimeMillis();
        System.out.println("\tone-to-all Dijkstra took " + (oneToAllEnd - oneToAllStart) + "ms");

        // ask user for a target node id
        System.out.print("Enter target node id... ");
        try (var in = new Scanner(System.in)) {
            int targetNodeId = in.nextInt();

            int oneToAllDistance = closesPath.getDistance(targetNodeId);

            System.out.println("Distance from " + sourceNodeId + " to " + targetNodeId + " is " + oneToAllDistance);
        }
    }
}
