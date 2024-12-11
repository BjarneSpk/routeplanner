package de.unistuttgart.fmi.graph;

import java.util.Arrays;

public class ClosestPathFinder {

    private final Graph graph;
    private int[] distances;
    // private int[] predecessors;

    public ClosestPathFinder(Graph graph) {
        this.graph = graph;
    }

    public int getDistance(int target) {
        return distances[target];
    }

    public void getShortestPath(int start) {
        int numNodes = graph.nodes.length;
        distances = new int[numNodes];
        // predecessors = new int[numNodes];
        boolean[] visited = new boolean[numNodes];

        Arrays.fill(distances, Integer.MAX_VALUE);
        // Arrays.fill(predecessors, -1);
        Arrays.fill(visited, false);

        distances[start] = 0;
        // predecessors[start] = start;

        PriorityQueue queue = new PriorityQueue(numNodes);

        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            visited[current] = true;

            int firstEdgeIdx = graph.offsetArray[current];
            int lastEdgeIdx = graph.offsetArray[current + 1];
            for (int i = firstEdgeIdx; i < lastEdgeIdx; i++) {
                int[] edge = graph.adjacencyArray[i];

                if (visited[edge[1]]) {
                    continue;
                }
                int distance = distances[current] + edge[2];

                if (distance < distances[edge[1]]) {
                    distances[edge[1]] = distance;
                    queue.decreaseKey(edge[1]);
                    // predecessors[edge[1]] = current;
                }
            }
        }
    }

    public int getShortestPath(int start, int target) {
        int numNodes = graph.nodes.length;
        distances = new int[numNodes];
        // predecessors = new int[numNodes];
        boolean[] visited = new boolean[numNodes];

        Arrays.fill(distances, Integer.MAX_VALUE);
        // Arrays.fill(predecessors, -1);
        Arrays.fill(visited, false);

        distances[start] = 0;
        // predecessors[start] = start;

        PriorityQueue queue = new PriorityQueue(numNodes);
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (current == target) {
                return distances[current];
            }

            visited[current] = true;

            int firstEdgeIdx = graph.offsetArray[current];
            int lastEdgeIdx = graph.offsetArray[current + 1];
            for (int i = firstEdgeIdx; i < lastEdgeIdx; i++) {
                int[] edge = graph.adjacencyArray[i];

                if (visited[edge[1]]) {
                    continue;
                }
                int distance = distances[current] + edge[2];

                if (distance < distances[edge[1]]) {
                    distances[edge[1]] = distance;
                    queue.decreaseKey(edge[1]);
                    // predecessors[edge[1]] = current;
                }
            }
        }

        return -1;
    }

    private class PriorityQueue {

        private int[] heap;
        private int size;
        private int[] indices;

        public PriorityQueue(int initialCapacity) {
            if (initialCapacity <= 0) {
                throw new IllegalArgumentException("Initial capacity must be greater than 0");
            }
            this.heap = new int[initialCapacity];
            this.indices = new int[initialCapacity];
            Arrays.fill(indices, -1);
            this.size = 0;
        }

        public void add(int value) {
            heap[size] = value;
            indices[value] = size;
            size++;
            bubbleUp(size - 1);
        }

        public void decreaseKey(int value) {
            int idx = indices[value];
            if (idx == -1) {
                add(value);
                return;
            }
            bubbleDown(idx);
            bubbleUp(idx);
        }

        public int poll() {
            if (size == 0) {
                throw new IllegalStateException("PriorityQueue is empty");
            }

            int result = heap[0];

            heap[0] = heap[size - 1];
            indices[result] = -1;
            size--;
            bubbleDown(0);

            return result;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void bubbleUp(int index) {
            while (index > 0) {
                int parentIndex = (index - 1) / 2;
                if (Integer.compare(distances[heap[index]], distances[heap[parentIndex]]) >= 0) {
                    break;
                }
                swap(index, parentIndex);
                index = parentIndex;
            }
        }

        private void bubbleDown(int index) {
            while (true) {
                int leftChild = 2 * index + 1;
                int rightChild = 2 * index + 2;
                int smallest = index;

                if (leftChild < size && Integer.compare(distances[heap[leftChild]], distances[heap[smallest]]) < 0) {
                    smallest = leftChild;
                }

                if (rightChild < size && Integer.compare(distances[heap[rightChild]], distances[heap[smallest]]) < 0) {
                    smallest = rightChild;
                }

                if (smallest == index) {
                    break;
                }

                swap(index, smallest);
                index = smallest;
            }
        }

        private void swap(int i, int j) {
            int temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
            indices[heap[i]] = i;
            indices[heap[j]] = j;
        }
    }
}
