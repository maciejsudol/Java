package com.dijkstra;

public class Edgee {
    Vertex index2;
    Integer weight;

    Edgee(int idx, int weight) {
        index2 = new Vertex(idx);
        this.weight = weight;
    }

    Edgee(Vertex other, int weight) {
        index2 = other;
        this.weight = weight;
    }

    @Override
    public String toString() {
        String result = new String();
        result = "Index 2: " + index2.index + " Waga: " + weight;
        return result;
    }
}
