package com.example.pipegame.model;

import java.util.ArrayList;

public class AdjacencyMatrixGraph<T> implements iGraph<T> {

    private final int[][] adjacencyMatrix;

    public AdjacencyMatrixGraph(int vertices) {
        this.adjacencyMatrix = new int[vertices][vertices];
    }

    @Override
    public void addVertex(Vertex<T> vertex) {

    }

    @Override
    public void addEdge(Vertex<T> source, Vertex<T> destination, int weight) {

    }

    @Override
    public boolean removeVertex(Vertex<T> vertex) {
        return false;
    }

    @Override
    public void removeEdge(Vertex<T> source, Vertex<T> destination) {
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
    public ArrayList<Vertex<T>> dfs(Vertex<T> startVertex) {
        return null;
    }

    @Override
    public ArrayList<Vertex<T>>  bfs(Vertex<T> startVertex) {
        return null;
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
