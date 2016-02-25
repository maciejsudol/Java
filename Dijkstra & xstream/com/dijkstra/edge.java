package com.dijkstra;

import java.io.Serializable;

public class edge implements Serializable {
    int begin_w;
    int weight;
    int end_w;

    edge()
    {
        begin_w = 0;
        weight = 0;
        end_w = 0;
    }

    edge(int w1, int weight, int w2)
    {
        begin_w = w1;
        this.weight = weight;
        end_w = w2;
    }
}
