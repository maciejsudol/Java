package com.dijkstra;

import java.io.*;
import java.util.*;

public class dijkstra {
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        Integer w1, w2;
        Graph graph = new Graph("data1.txt");
        Scanner input = new Scanner(System.in);
        System.out.println("Podaj pierwszy wierzcholek:\n");
        w1 = input.nextInt();
        System.out.println("Podaj drugi wierzcholek:\n");
        w2 = input.nextInt();
        //System.out.print(graph);

        System.out.print(graph.dijkstra(w1, w2));
        //System.out.print(graph.bFord(w1, w2));

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("test.out"));
        out.writeObject(graph);
        out.close();

        //ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.out"));
        Graph test = new Graph();
        test.generator();
        //test = (Graph)in.readObject();
        //System.out.print(test.dijkstra(w1, w2));
        //System.out.print(test.bFord(w1, w2));
    }
}