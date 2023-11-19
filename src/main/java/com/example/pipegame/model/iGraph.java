package com.example.pipegame.model;

import java.util.ArrayList;

public interface iGraph<T> {
    void addVertex(Vertex<T> vertex);
    Vertex<T> findVertex(T data);
    void removeVertex(Vertex<T>  vertex);
    void addEdge(Vertex<T> source, Vertex<T> destination, int weight);
    void removeEdge(Vertex<T> source, Vertex<T> destination);
    ArrayList<Vertex<T>> dfs(Vertex<T> startVertex);
    ArrayList<Vertex<T>> bfs(Vertex<T> startVertex);
    ArrayList<Vertex<T>> dijkstra(Vertex<T>  startVertex, Vertex<T> endVertex);
    int[][] floydWarshall();
    AdjacencyListGraph<T> primAL(Vertex<T> startVertex);
    AdjacencyListGraph<T> kruskalAL();
    AdjacencyMatrixGraph<T> primAM(Vertex<T> startVertex);
    AdjacencyMatrixGraph<T> kruskalAM();
    void removeAllEdges();
    ArrayList<Vertex<T>> getVertices();
}
