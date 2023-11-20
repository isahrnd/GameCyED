package com.example.pipegame.model;

import java.util.*;

public class AdjacencyListGraph<T> implements iGraph<T> {

    private ArrayList<Vertex<T>> vertices;
    private ArrayList<Edge<T>> edges;
    private int time;

    public AdjacencyListGraph(){
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
        time = 0;
    }

    @Override
    public void addVertex(Vertex<T> vertex) {
        vertices.add(vertex);
    }

    @Override
    public Vertex<T> findVertex(T data) {
        for (Vertex<T> vertex : vertices) {
            if (vertex.getData().equals(data)) {
                return vertex;
            }
        }
        return null;
    }

    @Override
    public void removeVertex(Vertex<T> vertex) {
        if (!vertices.contains(vertex)) {
            throw new IllegalArgumentException("The vertex is not in the graph.");
        }
        // delete the vertex and all associated edges
        vertices.remove(vertex);
        edges.removeIf(edge -> edge.getSource().equals(vertex) || edge.getDestination().equals(vertex));
        for (Vertex<T> v : vertices) {
            v.removeNeighbor(vertex);
        }
    }

    @Override
    public void addEdge(Vertex<T> source, Vertex<T> destination, int weight) {
        if (!vertices.contains(source) || !vertices.contains(destination)) {
            throw new IllegalArgumentException("The vertices must be in the graph.");
        }
        source.addNeighbor(destination);
        destination.addNeighbor(source);
        Edge<T> edge = new Edge<>(source, destination, weight);
        edges.add(edge);
    }

    @Override
    public void removeEdge(Vertex<T> source, Vertex<T> destination) {
        if (!vertices.contains(source) || !vertices.contains(destination)) {
            throw new IllegalArgumentException("The vertices must be in the graph.");
        }
        source.removeNeighbor(destination);
        destination.removeNeighbor(source);
        Edge<T> edgeToRemove = findEdge(source, destination);
        if (edgeToRemove != null) {
            edges.remove(edgeToRemove);
        }
    }

    @Override
    public ArrayList<Vertex<T>> dfs(Vertex<T> source) {
        ArrayList<Vertex<T>> dfsOrder = new ArrayList<>();
        if (vertices.size() > 0) {
            for (Vertex<T> v : vertices) {
                v.setColor(Color.WHITE);
            }
            time = 0;
            dfs(source, dfsOrder);
        }
        return dfsOrder;
    }

    private void dfs(Vertex<T> v, ArrayList<Vertex<T>> dfsOrder) {
        time += 1;
        v.setDiscoveryTime(time);
        v.setColor(Color.GRAY);
        dfsOrder.add(v);
        for (Vertex<T> u : v.getNeighbors()) {
            if (u.getColor() == Color.WHITE) {
                dfs(u, dfsOrder);
            }
        }
        v.setColor(Color.BLACK);
        time += 1;
        v.setFinishTime(time);
    }

    @Override
    public ArrayList<Vertex<T>> bfs(Vertex<T> source) {
        ArrayList<Vertex<T>> bfsOrder = new ArrayList<>();

        for (Vertex<T> u : vertices) {
            u.setColor(Color.WHITE);
            u.setDistance(Integer.MAX_VALUE);
            u.setPredecessor(null);
        }

        // initialization of the source vertex
        source.setColor(Color.GRAY);
        source.setDistance(0);
        source.setPredecessor(null);

        // queue to take the BFS route
        Queue<Vertex<T>> queue = new LinkedList<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            Vertex<T> u = queue.poll();
            bfsOrder.add(u); // add vertex to BFS result
            // iteration over the neighbors of the current vertex.
            for (Vertex<T> v : u.getNeighbors()) {
                if (v.getColor() == Color.WHITE) {
                    v.setColor(Color.GRAY);
                    v.setDistance(u.getDistance() + 1);
                    v.setPredecessor(u);
                    queue.offer(v);
                }
            }
            u.setColor(Color.BLACK);
        }

        return bfsOrder;
    }

    @Override
    public ArrayList<Vertex<T>> dijkstra(Vertex<T> source, Vertex<T> destination) {
        Map<Vertex<T>, Integer> distances = new HashMap<>();
        Map<Vertex<T>, Vertex<T>> previousVertices = new HashMap<>();
        Set<Vertex<T>> S = new HashSet<>();
        PriorityQueue<Vertex<T>> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        // initialize distances
        for (Vertex<T> vertex : vertices) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        priorityQueue.add(source);

        while (!priorityQueue.isEmpty()) {
            Vertex<T> u = priorityQueue.poll();
            S.add(u);
            if (u.equals(destination)) {
                break; // break the loop if the target vertex is reached
            }

            for (Edge<T> edge : getEdges(u)) {
                Vertex<T> v = edge.getDestination();
                int newDistance = distances.get(u) + edge.getWeight();
                if (!S.contains(v) && newDistance < distances.get(v)) {
                    distances.put(v, newDistance);
                    previousVertices.put(v, u);
                    priorityQueue.add(v);
                }
            }
        }

        // reconstructing the path from the destination to the source
        ArrayList<Vertex<T>> shortestPath = new ArrayList<>();
        Vertex<T> currentVertex = destination;
        while (currentVertex != null) {
            shortestPath.add(currentVertex);
            currentVertex = previousVertices.get(currentVertex);
        }
        return shortestPath;
    }

    @Override
    public int[][] floydWarshall() {
        int size = vertices.size();
        int[][] dist = new int[size][size];

        // initialize dist matrix with edge weights
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    Edge<T> edge = findEdge(vertices.get(i), vertices.get(j));
                    dist[i][j] = (edge != null) ? edge.getWeight() : Integer.MAX_VALUE;
                }
            }
        }

        // apply Floyd-Warshall algorithm
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return dist;
    }

    @Override
    public AdjacencyListGraph<T> primAL(Vertex<T> startVertex) {
        AdjacencyListGraph<T> mstGraph = new AdjacencyListGraph<>();

        // Initialize key, color, and pred arrays
        for (Vertex<T> u : vertices) {
            u.setDistance(Integer.MAX_VALUE);
            u.setColor(Color.WHITE);
            u.setPredecessor(null);
            mstGraph.addVertex(new Vertex<>(u.getData()));
        }

        // Start with the given start vertex
        startVertex.setDistance(0);

        PriorityQueue<Vertex<T>> priorityQueue = new PriorityQueue<>(vertices.size(), Comparator.comparingInt(Vertex::getDistance));
        priorityQueue.addAll(vertices);

        while (!priorityQueue.isEmpty()) {
            Vertex<T> u = priorityQueue.poll();
            u.setColor(Color.BLACK);

            for (Vertex<T> v : u.getNeighbors()) {
                int weight = findEdge(u,v) != null ? Objects.requireNonNull(findEdge(u, v)).getWeight() : Integer.MAX_VALUE;
                if (v.getColor() == Color.WHITE && weight < v.getDistance()) {
                    v.setDistance(weight);
                    priorityQueue.remove(v); // Remove and re-add to update the priority queue
                    priorityQueue.add(v);
                    v.setPredecessor(u);

                    Vertex<T> uInMST = mstGraph.findVertex(u.getData());
                    Vertex<T> vInMST = mstGraph.findVertex(v.getData());

                    if (uInMST != null && vInMST != null) {
                        mstGraph.addEdge(uInMST, vInMST, weight);
                    }
                }
            }
        }

        return mstGraph;
    }

    @Override
    public AdjacencyListGraph<T> kruskalAL() {
        AdjacencyListGraph<T> minimumSpanningTree = new AdjacencyListGraph<>();
        edges.sort(Comparator.comparingInt(Edge::getWeight));

        DisjointSet<T> disjointSet = new DisjointSet<>(vertices);

        for (Vertex<T> vertex : vertices) {
            minimumSpanningTree.addVertex(new Vertex<>(vertex.getData()));
        }

        for (Edge<T> edge : edges) {
            Vertex<T> sourceVertex = edge.getSource();
            Vertex<T> destinationVertex = edge.getDestination();

            if (!disjointSet.find(sourceVertex.getData()).equals(disjointSet.find(destinationVertex.getData()))) {
                disjointSet.union(sourceVertex.getData(), destinationVertex.getData());
                minimumSpanningTree.addEdge(new Vertex<>(sourceVertex.getData()), new Vertex<>(destinationVertex.getData()), edge.getWeight());
            }
        }

        return minimumSpanningTree;
    }

    @Override
    public AdjacencyMatrixGraph<T> primAM(Vertex<T> startVertex) {
        return null;
    }

    @Override
    public AdjacencyMatrixGraph<T> kruskalAM() {
        return null;
    }

    @Override
    public ArrayList<Vertex<T>> getVertices() {
        return vertices;
    }

    //auxiliars
    public void removeAllEdges() {
        for (Vertex<T> vertex : vertices) {
            removeAllEdgesFromVertex(vertex);
        }
    }

    private void removeAllEdgesFromVertex(Vertex<T> vertex) {
        ArrayList<Vertex<T>> neighbors = new ArrayList<>(vertex.getNeighbors());
        for (Vertex<T> neighbor : neighbors) {
            removeEdge(vertex, neighbor);
        }
    }

    private Edge<T> findEdge(Vertex<T> source, Vertex<T> destination) {
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(source) && edge.getDestination().equals(destination)) {
                return edge;
            }
        }
        return null;
    }

    public ArrayList<Edge<T>> getEdges(Vertex<T> vertex) {
        ArrayList<Edge<T>> vertexEdges = new ArrayList<>();
        for (Edge<T> edge : edges) {
            if (edge.getSource().equals(vertex) || edge.getDestination().equals(vertex)) {
                vertexEdges.add(edge);
            }
        }
        return vertexEdges;
    }

    private static class DisjointSet<T> {

        private final Map<T, T> parentMap;

        public DisjointSet(Collection<Vertex<T>> vertices) {
            parentMap = new HashMap<>();
            for (Vertex<T> vertex : vertices) {
                parentMap.put(vertex.getData(), vertex.getData());
            }
        }

        public T find(T element) {
            if (!parentMap.containsKey(element)) {
                throw new IllegalArgumentException("Element not found in the disjoint set");
            }

            if (element.equals(parentMap.get(element))) {
                return element;
            } else {
                T root = find(parentMap.get(element));
                parentMap.put(element, root);
                return root;
            }
        }

        public void union(T set1, T set2) {
            T root1 = find(set1);
            T root2 = find(set2);

            if (!root1.equals(root2)) {
                parentMap.put(root1, root2);
            }
        }
    }

}
