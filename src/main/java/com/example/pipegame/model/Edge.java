package com.example.pipegame.model;

public class Edge<T> {

    private Vertex<T> source;
    private Vertex<T> destination;
    private int weight;

    public Edge(Vertex<T> source, Vertex<T> destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex<T> getSource() {
        return source;
    }

    public void setSource(Vertex<T> source) {
        this.source = source;
    }

    public Vertex<T> getDestination() {
        return destination;
    }

    public void setDestination(Vertex<T> destination) {
        this.destination = destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}