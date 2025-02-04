package de.unistuttgart.fmi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.unistuttgart.fmi.graph.Graph;
import java.io.IOException;
import java.io.InputStream;
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
        server.createContext("/coordinates", new CoordinateHandler());
        server.createContext("/path", new ArrayHandler());

        server.start();
        System.out.println("Server listening on http://127.0.0.1:8080/");
    }

    public class ArrayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            var nodes = parseNodesFromJson(requestBody);

            var pathfinder = graph.getClosestPathFinder();
            pathfinder.getShortestPath(nodes.get("start"), nodes.get("end"));
            List<double[]> path = pathfinder.getPath();

            String jsonResponse = convertPathToJson(path);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            }
        }

        private Map<String, Integer> parseNodesFromJson(String json) {
            json = json.trim();
            json = json.substring(1, json.length() - 1);
            var lines = json.split(",");

            var map = new HashMap<String, Integer>();
            map.put(lines[0].split(":")[0].replace("\"", ""), Integer.parseInt(lines[0].split(":")[1].trim()));
            map.put(lines[1].split(":")[0].replace("\"", ""), Integer.parseInt(lines[1].split(":")[1].trim()));

            return map;
        }

        private String convertPathToJson(List<double[]> path) {
            StringBuilder jsonBuilder = new StringBuilder("{\"path\":[");
            var last = path.removeLast();
            for (double[] node : path) {
                jsonBuilder.append(String.format(Locale.US, "{\"lat\":%f,\"lng\":%f},", node[0], node[1]));
            }
            jsonBuilder.append(String.format(Locale.US, "{\"lat\":%f,\"lng\":%f}]}", last[0], last[1]));
            return jsonBuilder.toString();
        }
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
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            double lat = 0;
            double lng = 0;
            try {
                lat = extractValue(requestBody, "lat");
                lng = extractValue(requestBody, "lng");

                double[] neighbour = graph.getNearestNeighbour(new double[] {lat, lng});

                String response = String.format(
                        Locale.US, "{\"lat\":%f,\"lng\":%f,\"id\":%d}", neighbour[0], neighbour[1], (int) neighbour[2]);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IllegalArgumentException e) {
                String errorResponse = "{\"error\":\"Invalid input\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, errorResponse.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(errorResponse.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        private double extractValue(String json, String key) {
            String searchKey = "\"" + key + "\":";
            int startIndex = json.indexOf(searchKey);

            if (startIndex == -1) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            startIndex += searchKey.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }
            if (endIndex == -1) {
                throw new IllegalArgumentException("Invalid json");
            }
            String valueStr = json.substring(startIndex, endIndex).trim();
            return Double.parseDouble(valueStr);
        }
    }
}
