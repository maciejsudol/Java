package com.dijkstra;
import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;

public class server2 {
    public static final int port = 6161;


    public void runServer(String graph) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server up & ready for connection...");
            Socket socket = serverSocket.accept();
            //ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending information...");
            output.writeObject(graph);
            System.out.println("Closing the connection...");
            socket.close();
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
        } finally {
            System.exit(0);
        }
    }

    public static Graph generator() {
        Graph result = new Graph();
        System.out.println("Generator start");
        result.generator();
        System.out.println("Generator end");
        return result;
    }

    public static String serializer() {
        Graph result = generator();
        XStream out = new XStream();
        String xml = out.toXML(result);
        return xml;
    }

    public static void main(String[] args) throws IOException {
        server2 serv = new server2();
        serv.runServer(serializer());
    }
}
