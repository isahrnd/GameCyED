package com.example.pipegame.model;

import java.util.ArrayList;

public interface iGraph<T> {
    void addVertex(Vertex<T> vertex);
    void addEdge(Vertex<T> source, Vertex<T> destination);
    boolean removeVertex(Vertex<T>  vertex);
    boolean removeEdge(Vertex<T> source, Vertex<T> destination);
    ArrayList<Vertex<T>> getNeighbors(Vertex<T> vertex);
    ArrayList<Vertex<T>> getVertices();
    void dfs(Vertex<T>  startVertex);
    ArrayList<Vertex<T>> bfs(Vertex<T> startVertex);
    ArrayList<Vertex<T>> dijkstra(Vertex<T>  startVertex, Vertex<T> endVertex);
    int[][] floydWarshall();
    ArrayList<T> prim();
    ArrayList<T> kruskal();
}
