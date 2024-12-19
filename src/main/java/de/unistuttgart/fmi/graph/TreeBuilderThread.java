package de.unistuttgart.fmi.graph;

public class TreeBuilderThread extends Thread {
    KDTree tree;
    private double[][] nodes;

    TreeBuilderThread(double[][] nodes) {
        this.nodes = nodes;
    }

    @Override
    public void run() {
        this.tree = new KDTree(nodes);
    }
}
