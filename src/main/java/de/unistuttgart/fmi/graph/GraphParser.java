package de.unistuttgart.fmi.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class GraphParser {

    private final String filePath;

    GraphParser(String filePath) {
        this.filePath = filePath;
    }

    Graph parse() throws InvalidGraphException {
        try (var reader = new BufferedReader(new FileReader(filePath))) {
            return buildGraph(reader);
        } catch (IOException | RuntimeException e) {
            var message = String.format("Could not parse file %s", filePath);
            throw new InvalidGraphException(message, e);
        }
    }

    /**
     * @param reader
     * @return
     * @throws IOException
     */
    private Graph buildGraph(BufferedReader reader) throws IOException {
        // Skip header
        for (int i = 0; i < 5; i++) {
            reader.readLine();
        }

        int numNodes = Integer.parseInt(reader.readLine());
        int numEdges = Integer.parseInt(reader.readLine());

        int[][] adjacencyArray = new int[numEdges][3];
        double[][] nodes = new double[numNodes][3];
        int[] offsetArray = new int[numNodes + 1];

        Arrays.fill(offsetArray, 0);

        for (int i = 0; i < numNodes; i++) {
            String[] node = reader.readLine().split(" ");
            nodes[i][0] = Double.parseDouble(node[2]);
            nodes[i][1] = Double.parseDouble(node[3]);
            nodes[i][2] = Double.parseDouble(node[0]);
        }

        var t = new TreeBuilderThread(nodes);
        t.start();

        for (int i = 0; i < numEdges; i++) {
            String[] edge = reader.readLine().split(" ");
            adjacencyArray[i][0] = Integer.parseInt(edge[0]);
            adjacencyArray[i][1] = Integer.parseInt(edge[1]);
            adjacencyArray[i][2] = Integer.parseInt(edge[2]);
            offsetArray[Integer.parseInt(edge[0])]++;
        }

        int prevEdges = offsetArray[0];
        offsetArray[0] = 0;
        for (int i = 1; i < numNodes; i++) {
            int temp = offsetArray[i];
            offsetArray[i] = offsetArray[i - 1] + prevEdges;
            prevEdges = temp;
        }
        offsetArray[numNodes] = numEdges;

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return new Graph(adjacencyArray, offsetArray, nodes, t.tree);
    }
}
