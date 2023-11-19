package com.example.pipegame.model;

import java.util.ArrayList;

public interface iGraph<T> {
    void addVertex(Vertex<T> vertex);
    void addEdge(Vertex<T> source, Vertex<T> destination, int weight);
    void removeVertex(Vertex<T>  vertex);
    void removeEdge(Vertex<T> source, Vertex<T> destination);
    ArrayList<Vertex<T>> getNeighbors(Vertex<T> vertex);
    ArrayList<Vertex<T>> getVertices();
    ArrayList<Vertex<T>> dfs(Vertex<T> startVertex);
    ArrayList<Vertex<T>> bfs(Vertex<T> startVertex);
    ArrayList<Vertex<T>> dijkstra(Vertex<T>  startVertex, Vertex<T> endVertex);
    int[][] floydWarshall();
    ArrayList<T> prim();
    ArrayList<T> kruskal();
    void removeAllEdges();
}
