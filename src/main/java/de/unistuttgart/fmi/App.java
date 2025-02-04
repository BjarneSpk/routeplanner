package de.unistuttgart.fmi;

import de.unistuttgart.fmi.graph.Graph;
import de.unistuttgart.fmi.graph.InvalidGraphException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws InvalidGraphException {
        String graphPath = args[1];

        long graphReadStart = System.currentTimeMillis();

        Graph graph = Graph.from(graphPath);

        long graphReadEnd = System.currentTimeMillis();
        System.out.println("\tgraph read took " + (graphReadEnd - graphReadStart) + "ms");

        Server server = new Server(graph);

        System.out.println("[Q] to quit:");
        try (var in = new Scanner(System.in)) {
            in.next();
        }

        System.exit(0);
    }
}
