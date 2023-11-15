package com.example.pipegame.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AdjacencyListGraph<T> implements iGraph<T> {

    private ArrayList<Vertex<T>> vertices;

    public AdjacencyListGraph(){
        vertices = new ArrayList<>();
    }

    @Override
    public void addVertex(Vertex<T> vertex) {

    }

    @Override
    public void addEdge(Vertex<T> source, Vertex<T> destination) {

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
        return null;
    }

    @Override
    public void dfs(Vertex<T> startVertex) {

    }

    @Override
    public void bfs(Vertex<T> startVertex) {
        for (Vertex u : vertices) {
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
            // Iteración sobre los vecinos del vértice actual
            for (Vertex v : getNeighbors(u)) {
                if (v.getColor() == Color.WHITE) {
                    v.setColor(Color.GRAY);
                    v.setDistance(u.getDistance() + 1);
                    v.setPredecessor(u);
                    queue.offer(v);
                }
            }

            u.setColor(Color.BLACK);
        }
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
