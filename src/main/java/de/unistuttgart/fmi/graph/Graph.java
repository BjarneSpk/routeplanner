package de.unistuttgart.fmi.graph;

/**
 * Graph represented by an Adjacency-Array
 */
public class Graph {

    final int[][] adjacencyArray;
    final double[][] nodes;
    final int[] offsetArray;
    private KDTree tree;

    public Graph(int[][] adjacencyArray, int[] offsetArray, double[][] nodes) {
        this.adjacencyArray = adjacencyArray;
        this.nodes = nodes;
        this.offsetArray = offsetArray;
    }

    public static Graph from(String filePath) throws InvalidGraphException {
        return new GraphParser(filePath).parse();
    }

    public void initClosestNodeDS() {
        this.tree = new KDTree(nodes);
    }

    public double[] getNearestNeighbour(double[] start) {
        return this.tree.nearestNeighbor(start);
    }
}
