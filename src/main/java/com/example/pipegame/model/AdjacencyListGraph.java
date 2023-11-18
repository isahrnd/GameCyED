package com.example.pipegame.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AdjacencyListGraph<T> implements iGraph<T> {

    private ArrayList<Vertex<T>> vertices;
    private int time;

    public AdjacencyListGraph(){
        vertices = new ArrayList<>();
        time = 0;
    }

    @Override
    public void addVertex(Vertex<T> vertex) {
        vertices.add(vertex);
    }

    @Override
    public void addEdge(Vertex<T> source, Vertex<T> destination) {
        if (!vertices.contains(source) || !vertices.contains(destination)) {
            // Verificar si ambos vértices existen en el grafo
            throw new IllegalArgumentException("Los vértices deben estar en el grafo.");
        }
        source.addNeighbor(destination);
        destination.addNeighbor(source);
    }

    @Override
    public boolean removeVertex(Vertex<T> vertex) {
        return false;
    }

    @Override
    public boolean removeEdge(Vertex<T> source, Vertex<T> destination) {
        return false;
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
    public ArrayList<Vertex<T>> dijkstra(Vertex<T> startVertex, Vertex<T> endVertex) {
        return null;
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
}
