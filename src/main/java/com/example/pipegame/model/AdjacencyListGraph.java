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
    public void addEdge(Vertex<T> source, Vertex<T> destination, int weight) {
        if (!vertices.contains(source) || !vertices.contains(destination)) {
            // Verificar si ambos vértices existen en el grafo
            throw new IllegalArgumentException("Los vértices deben estar en el grafo.");
        }
        source.addNeighbor(destination);
        destination.addNeighbor(source);
        Edge<T> edge = new Edge<>(source, destination, weight);
        edges.add(edge);
    }

    @Override
    public void removeVertex(Vertex<T> vertex) {
        if (!vertices.contains(vertex)) {
            throw new IllegalArgumentException("El vértice no está en el grafo.");
        }

        // Eliminar el vértice y todas las aristas asociadas
        vertices.remove(vertex);
        edges.removeIf(edge -> edge.getSource().equals(vertex) || edge.getDestination().equals(vertex));

        for (Vertex<T> v : vertices) {
            v.removeNeighbor(vertex);
        }
    }

    @Override
    public void removeEdge(Vertex<T> source, Vertex<T> destination) {
        if (!vertices.contains(source) || !vertices.contains(destination)) {
            // Verificar si ambos vértices existen en el grafo
            throw new IllegalArgumentException("Los vértices deben estar en el grafo.");
        }
        source.removeNeighbor(destination);
        destination.removeNeighbor(source);
        Edge<T> edgeToRemove = findEdge(source, destination);
        if (edgeToRemove != null) {
            edges.remove(edgeToRemove);
        }
    }

    @Override
    public ArrayList<Vertex<T>> getNeighbors(Vertex<T> vertex) {
        return null;
    }

    @Override
    public ArrayList<Vertex<T>> getVertices() {
        return vertices;
    }

    @Override
    public ArrayList<Vertex<T>> dfs(Vertex<T> startVertex) {
        ArrayList<Vertex<T>> dfsOrder = new ArrayList<>();
        if (vertices.size() > 0) {
            for (Vertex<T> v : vertices) {
                v.setColor(Color.WHITE);
            }
            time = 0;
            dfs(startVertex, dfsOrder);
        }
        return dfsOrder;
    }

    private void dfs(Vertex<T> v, ArrayList<Vertex<T>> dfsOrder) {
        time += 1;
        v.setDiscoveryTime(time);
        v.setColor(Color.GRAY);
        dfsOrder.add(v); // Agregar el vértice al resultado de DFS
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
    public ArrayList<Vertex<T>> bfs(Vertex<T> startVertex) {
        ArrayList<Vertex<T>> bfsOrder = new ArrayList<>();

        for (Vertex<T> u : vertices) {
            u.setColor(Color.WHITE);
            u.setDistance(Integer.MAX_VALUE);
            u.setPredecessor(null);
        }

        // Inicialización del vértice de inicio
        startVertex.setColor(Color.GRAY);
        startVertex.setDistance(0);
        startVertex.setPredecessor(null);

        // Cola para realizar el recorrido BFS
        Queue<Vertex<T>> queue = new LinkedList<>();
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            Vertex<T> u = queue.poll();
            bfsOrder.add(u); // Agregar el vértice al resultado de BFS
            // Iteración sobre los vecinos del vértice actual
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
    public ArrayList<Vertex<T>> dijkstra(Vertex<T> source, Vertex<T> drain) {
        Map<Vertex<T>, Integer> distances = new HashMap<>();
        Map<Vertex<T>, Vertex<T>> previousVertices = new HashMap<>();
        Set<Vertex<T>> S = new HashSet<>();
        PriorityQueue<Vertex<T>> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        // Inicializar las distancias
        for (Vertex<T> vertex : vertices) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        priorityQueue.add(source);

        while (!priorityQueue.isEmpty()) {
            Vertex<T> u = priorityQueue.poll();
            S.add(u);
            if (u.equals(drain)) {
                break; // Salir del bucle si alcanzamos el vértice de drenaje
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

        // Reconstruir el camino desde el drenaje hasta la fuente
        ArrayList<Vertex<T>> shortestPath = new ArrayList<>();
        Vertex<T> currentVertex = drain;
        while (currentVertex != null) {
            shortestPath.add(currentVertex);
            currentVertex = previousVertices.get(currentVertex);
        }
        return shortestPath;
    }

    @Override
    public int[][] floydWarshall() {
        return new int[0][];
    }

    @Override
    public ArrayList<T> prim() {
        return null;
    }

    @Override
    public ArrayList<T> kruskal() {
        return null;
    }

    //Auxiliars

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
}
