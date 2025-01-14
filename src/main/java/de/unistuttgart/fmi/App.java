package de.unistuttgart.fmi;

import de.unistuttgart.fmi.graph.Graph;
import de.unistuttgart.fmi.graph.InvalidGraphException;

public class App {
    public static void main(String[] args) throws InvalidGraphException {
        String graphPath = args[1];

        long graphReadStart = System.currentTimeMillis();

        Graph graph = Graph.from(graphPath);

        long graphReadEnd = System.currentTimeMillis();
        System.out.println("\tgraph read took " + (graphReadEnd - graphReadStart) + "ms");

        Server server = new Server(graph);
    }
}
