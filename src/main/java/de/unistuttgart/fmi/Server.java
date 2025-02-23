package de.unistuttgart.fmi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.unistuttgart.fmi.graph.Graph;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Server {
    private final Graph graph;
    private final HttpServer server;

    public Server(Graph graph) {
        this.graph = graph;
        try {
            this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080), -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", new RootHandler());
        server.createContext("/coords", new CoordinateHandler());
        server.createContext("/path", new PathHandler());

        server.start();
        System.out.println("Server listening on http://127.0.0.1:8080/");
    }

    public Map<String, String> parseGetQuery(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = this.getClass()
                    .getResourceAsStream("/de/unistuttgart/fmi/index.html")
                    .readAllBytes();
            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response);
            }
        }
    }

    private class CoordinateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var nodes = parseGetQuery(exchange.getRequestURI().getQuery());

            double lat = Double.parseDouble(nodes.get("lat"));
            double lon = Double.parseDouble(nodes.get("lon"));

            double[] neighbour = graph.getNearestNeighbour(new double[] {lat, lon});

            String response = String.format(
                    Locale.US, "{\"lat\":%f,\"lon\":%f,\"id\":%d}", neighbour[0], neighbour[1], (int) neighbour[2]);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public class PathHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var nodes = parseGetQuery(exchange.getRequestURI().getQuery());

            var pathfinder = graph.getClosestPathFinder();
            int distance = pathfinder.getShortestPath(
                    Integer.parseInt(nodes.get("start")), Integer.parseInt(nodes.get("end")));
            if (distance == -1) {
                exchange.sendResponseHeaders(404, 0);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(new byte[0]);
                }
                return;
            }
            String jsonResponse = convertPathToJson(pathfinder.getPath());

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            }
        }

        private String convertPathToJson(List<double[]> path) {
            StringBuilder jsonBuilder = new StringBuilder("{\"geojson\":");
            var last = path.removeLast();

            jsonBuilder.append(
                    "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[");
            for (double[] node : path) {
                jsonBuilder.append(String.format(Locale.US, "[%f,%f],", node[1], node[0]));
            }
            jsonBuilder.append(String.format(Locale.US, "[%f,%f]", last[1], last[0]));
            jsonBuilder.append("]}}]}}");
            return jsonBuilder.toString();
        }
    }
}
