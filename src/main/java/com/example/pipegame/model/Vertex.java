package com.example.pipegame.model;


public class Vertex<T> {
    private T data;
    private Color color;
    private int distance;
    private Vertex<T> predecessor;

    public Vertex(T data) {
        this.data = data;
        this.color = Color.WHITE;
        this.distance = Integer.MAX_VALUE;
        this.predecessor = null;
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
