package de.unistuttgart.fmi.graph;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;

/**
 * Simple Implementation of a KDTree with k=2 fixed to find the nearest node in
 * a graph to given coordinates.
 *
 * Distances are calculated using the great circle distance with the haversine
 * formula. The median is selected with the quickSelect algorithm.
 *
 */
class KDTree {
    private static class KDNode {
        double[] point;
        KDNode left, right;
        boolean isXAxis;

        public KDNode(double[] point, boolean isXAxis) {
            this.point = point;
            this.isXAxis = isXAxis;
        }
    }

    // Dimension of KDTree, here fixed to 2D
    private static final int k = 2;
    private KDNode root;
    private static final Random random = new Random();

    KDTree(double[][] points) {
        this.root = buildTree(points);
    }

    /**
     * Builds the tree iteratively from an array of points
     *
     * @param points the list of points
     * @return the root of the built Tree
     */
    private KDNode buildTree(double[][] points) {
        if (points == null || points.length == 0) return null;

        ArrayDeque<KDNode> queue = new ArrayDeque<>();
        KDNode rootNode = new KDNode(null, true);
        queue.add(rootNode);

        ArrayDeque<double[][]> pointQueue = new ArrayDeque<>();
        pointQueue.add(points);

        while (!queue.isEmpty()) {
            KDNode currentNode = queue.poll();
            double[][] currentPoints = pointQueue.poll();
            if (currentPoints.length == 0) continue;

            int axis = currentNode.isXAxis ? 0 : 1;
            int medianIndex = currentPoints.length / 2;

            currentNode.point = quickSelect(currentPoints, 0, currentPoints.length - 1, medianIndex, axis);

            double[][] leftPoints = Arrays.copyOfRange(currentPoints, 0, medianIndex);
            double[][] rightPoints = Arrays.copyOfRange(currentPoints, medianIndex + 1, currentPoints.length);

            if (leftPoints.length > 0) {
                currentNode.left = new KDNode(null, !currentNode.isXAxis);
                queue.add(currentNode.left);
                pointQueue.add(leftPoints);
            }
            if (rightPoints.length > 0) {
                currentNode.right = new KDNode(null, !currentNode.isXAxis);
                queue.add(currentNode.right);
                pointQueue.add(rightPoints);
            }
        }

        return rootNode;
    }

    /**
     * Algorihm to find the k-th smallest element in a list.
     *
     * @param points      the list of points
     * @param low         lower index
     * @param high        upper index (exclusive)
     * @param medianIndex the median index of the original list
     * @param the         axis of the point to find the median to
     * @return the median point between high and low
     */
    private static double[] quickSelect(double[][] points, int low, int high, int medianIndex, int axis) {
        if (low == high) return points[low];

        int pivotIndex = partition(points, low, high, axis);

        if (medianIndex == pivotIndex) {
            return points[medianIndex];
        } else if (medianIndex < pivotIndex) {
            return quickSelect(points, low, pivotIndex - 1, medianIndex, axis);
        } else {
            return quickSelect(points, pivotIndex + 1, high, medianIndex, axis);
        }
    }

    private static int partition(double[][] points, int low, int high, int axis) {
        int pivotIndex = low + random.nextInt(high - low + 1);
        double[] pivot = points[pivotIndex];
        swap(points, pivotIndex, high); // Move pivot to end
        int storeIndex = low;

        for (int i = low; i < high; i++) {
            if (points[i][axis] < pivot[axis]) {
                swap(points, i, storeIndex);
                storeIndex++;
            }
        }
        swap(points, storeIndex, high); // Move pivot to its final position
        return storeIndex;
    }

    private static void swap(double[][] points, int i, int j) {
        double[] temp = points[i];
        points[i] = points[j];
        points[j] = temp;
    }

    /**
     * Calucalate the nearest node to a point in this KDTree.
     *
     * @param target the coordinates to find the nearest node to
     * @return the nearest node contained in this Tree
     */
    double[] nearestNeighbor(double[] start) {
        return nearestNeighbor(root, start, 0, null, Double.MAX_VALUE);
    }

    private double[] nearestNeighbor(KDNode node, double[] target, int depth, double[] bestPoint, double bestDistance) {
        if (node == null) {
            return bestPoint;
        }

        double distance = getDistance(target, node.point);
        if (distance < bestDistance) {
            bestPoint = node.point;
            bestDistance = distance;
        }

        int axis = depth % k;
        KDNode nextNode = (target[axis] < node.point[axis]) ? node.left : node.right;
        KDNode otherNode = (target[axis] < node.point[axis]) ? node.right : node.left;

        bestPoint = nearestNeighbor(nextNode, target, depth + 1, bestPoint, bestDistance);
        bestDistance = getDistance(target, bestPoint);

        if (Math.abs(target[axis] - node.point[axis]) < bestDistance) {
            bestPoint = nearestNeighbor(otherNode, target, depth + 1, bestPoint, bestDistance);
        }

        return bestPoint;
    }

    /**
     * Calculates great circle distance using Haversine formula.
     *
     * @param point1 first Point
     * @param point2 second point
     * @return the great circle distance between the two points
     */
    private double getDistance(double[] point1, double[] point2) {
        // Earth's radius in kilometers
        final double R = 6371.0;

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
