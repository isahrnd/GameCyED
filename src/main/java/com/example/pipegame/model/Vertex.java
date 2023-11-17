package com.example.pipegame.model;


import java.util.ArrayList;

public class Vertex<T> {
    private T data;
    private Color color;
    private int distance;
    private Vertex<T> predecessor;
    private final ArrayList<Vertex<T>> neighbors;

    public Vertex(T data) {
        this.data = data;
        this.color = Color.WHITE;
        this.distance = Integer.MAX_VALUE;
        this.predecessor = null;
        neighbors = new ArrayList<>();
    }

    public ArrayList<Vertex<T>> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Vertex<T> neighbor) {
        neighbors.add(neighbor);
    }

    public T getData() {
        return data;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Vertex<T> getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Vertex<T> predecessor) {
        this.predecessor = predecessor;
    }
}
