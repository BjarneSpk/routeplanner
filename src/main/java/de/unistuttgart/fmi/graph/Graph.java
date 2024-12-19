package de.unistuttgart.fmi.graph;

/**
 * Graph represented by an Adjacency-Array
 */
public class Graph {

    final int[][] adjacencyArray;
    final double[][] nodes;
    final int[] offsetArray;
    private final KDTree tree;

    public Graph(int[][] adjacencyArray, int[] offsetArray, double[][] nodes, KDTree tree) {
        this.adjacencyArray = adjacencyArray;
        this.nodes = nodes;
        this.offsetArray = offsetArray;
        this.tree = tree;
    }

    public static Graph from(String filePath) throws InvalidGraphException {
        return new GraphParser(filePath).parse();
    }

    public double[] getNearestNeighbour(double[] start) {
        return this.tree.nearestNeighbor(start);
    }

    public ClosestPathFinder getClosestPathFinder() {
        return new ClosestPathFinder(this);
    }
}
