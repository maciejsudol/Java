package com.dijkstra;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import com.thoughtworks.xstream.*;

public class client_xml {
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException{
        Socket socket = new Socket("localhost", server2.port);
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        //ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        XStream in_xml = new XStream();
        String st_graph = (String)input.readObject();
        socket.close();
        Graph graph = (Graph)in_xml.fromXML(st_graph);

        Integer w1, w2;
        Scanner in = new Scanner(System.in);
        System.out.println("Podaj pierwszy wierzcholek:\n");
        w1 = in.nextInt();
        System.out.println("Podaj drugi wierzcholek:\n");
        w2 = in.nextInt();
        //System.out.print(graph);

        System.out.print(graph.dijkstra(w1, w2));
        System.out.print(graph.bFord(w1, w2));
    }
}
