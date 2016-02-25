package com.dijkstra;
import java.util.*;

public class Vertex implements Comparable<Vertex> {
    Integer index;
    Integer distance;
    Vertex parent;
    List<Edgee> adjTo;

    Vertex(int u) {
        index = u;
        distance = Integer.MAX_VALUE - 50;
        parent = null;
        adjTo = new ArrayList<Edgee>();
    }

    @Override
    public int compareTo(Vertex other) {
        if(this.distance < other.distance)
            return -1;
        if(this.distance > other.distance)
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        String result = new String();
        result = "Index: " + index + " Dystans: " + distance + " Sassiedztwo:\n" + adjTo + "\n";
        return result;
    }
}
