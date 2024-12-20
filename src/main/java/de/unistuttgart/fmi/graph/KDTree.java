package de.unistuttgart.fmi.graph;

import java.util.ArrayDeque;

/**
 * Simple Implementation of a KDTree with k=2 fixed to find the nearest node in
 * a graph to given coordinates.
 *
 * Distances are calculated using the great circle distance with the haversine
 * formula. The median is selected with the quickSelect algorithm.
 *
 */
class KDTree {
    private static final int k = 2;
    private final double[][] tree;

    KDTree(double[][] points) {
        int n = points.length;
        int arraySize = (1 << ((int) Math.ceil(Math.log(n + 1) / Math.log(2)))) - 1;
        tree = new double[arraySize][];
        buildTree(points);
    }

    private void buildTree(double[][] points) {
        if (points == null || points.length == 0) {
            return;
        }

        ArrayDeque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[] {0, points.length - 1, 0, 0});

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int start = current[0];
            int end = current[1];
            int depth = current[2];
            int index = current[3];

            if (start > end) continue;

            int axis = depth % k;
            int medianIndex = (start + end) / 2;
            quickSelect(points, start, end, medianIndex, axis);

            tree[index] = points[medianIndex];

            stack.push(new int[] {start, medianIndex - 1, depth + 1, 2 * index + 1});
            stack.push(new int[] {medianIndex + 1, end, depth + 1, 2 * index + 2});
        }
    }

    private void quickSelect(double[][] points, int low, int high, int medianIndex, int axis) {
        if (low == high) return;

        int pivotIndex = partition(points, low, high, axis);

        if (medianIndex == pivotIndex) {
            return;
        } else if (medianIndex < pivotIndex) {
            quickSelect(points, low, pivotIndex - 1, medianIndex, axis);
        } else {
            quickSelect(points, pivotIndex + 1, high, medianIndex, axis);
        }
    }

    private int partition(double[][] points, int low, int high, int axis) {
        double[] pivot = points[high];
        int storeIndex = low;

        for (int i = low; i < high; i++) {
            if (points[i][axis] < pivot[axis]) {
                swap(points, i, storeIndex);
                storeIndex++;
            }
        }
        swap(points, storeIndex, high);
        return storeIndex;
    }

    private void swap(double[][] points, int i, int j) {
        double[] temp = points[i];
        points[i] = points[j];
        points[j] = temp;
    }

    public double[] nearestNeighbor(double[] target) {
        return nearestNeighbor(0, target, 0, null, Double.MAX_VALUE);
    }

    private double[] nearestNeighbor(int index, double[] target, int depth, double[] bestPoint, double bestDistance) {
        if (index >= tree.length || tree[index] == null) {
            return bestPoint;
        }

        double distance = getDistance(target, tree[index]);
        if (distance < bestDistance) {
            bestPoint = tree[index];
            bestDistance = distance;
        }

        int axis = depth % k;
        int leftIndex = 2 * index + 1;
        int rightIndex = 2 * index + 2;

        int nextIndex = (target[axis] < tree[index][axis]) ? leftIndex : rightIndex;
        int otherIndex = (target[axis] < tree[index][axis]) ? rightIndex : leftIndex;

        bestPoint = nearestNeighbor(nextIndex, target, depth + 1, bestPoint, bestDistance);
        bestDistance = getDistance(target, bestPoint);

        if (Math.abs(target[axis] - tree[index][axis]) < bestDistance) {
            bestPoint = nearestNeighbor(otherIndex, target, depth + 1, bestPoint, bestDistance);
        }

        return bestPoint;
    }

    private double getDistance(double[] point1, double[] point2) {
        final double R = 6371.0; // Earth radius in kilometers

        double lat1 = Math.toRadians(point1[0]);
        double lon1 = Math.toRadians(point1[1]);
        double lat2 = Math.toRadians(point2[0]);
        double lon2 = Math.toRadians(point2[1]);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
