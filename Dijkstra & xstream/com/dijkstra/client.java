package com.dijkstra;
import com.thoughtworks.xstream.XStream;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        Integer w1, w2;
        Scanner in = new Scanner(System.in);
        System.out.println("Podaj pierwszy wierzcholek:\n");
        w1 = in.nextInt();
        System.out.println("Podaj drugi wierzcholek:\n");
        w2 = in.nextInt();

        try(Socket socket1 = new Socket("localhost", server1.port)) {
            ObjectInputStream input1 = new ObjectInputStream(socket1.getInputStream());
            //ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Graph graph1 = (Graph) input1.readObject();
            socket1.close();

            System.out.println("Pierwszy graf:");
            System.out.print(graph1.dijkstra(w1, w2));
            System.out.print(graph1.bFord(w1, w2));

        }catch(ClassNotFoundException e){
            System.err.println("1: Class not found");
            System.exit(1);
        } catch(UnknownHostException e) {
            System.err.println("1: Don't know about host: localhost");
            System.exit(1);
        } catch(IOException e) {
            System.err.println("1: Couldn't get I/O connection to the localhost");
            System.exit(1);
        }
        try(Socket socket2 = new Socket("localhost", server2.port);) {
            ObjectInputStream input2 = new ObjectInputStream(socket2.getInputStream());
            //ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            XStream in_xml = new XStream();
            String xml_graph = (String) input2.readObject();
            socket2.close();
            Graph graph2 = (Graph) in_xml.fromXML(xml_graph);

            System.out.println("\n");
            System.out.println("Drugi graf:");
            System.out.print(graph2.dijkstra(w1, w2));
            System.out.print(graph2.bFord(w1, w2));
        }catch(ClassNotFoundException e){
            System.err.println("2: Class not found");
            System.exit(1);
        } catch(UnknownHostException e) {
            System.err.println("2: Don't know about host: localhost");
            System.exit(1);
        } catch(IOException e) {
            System.err.println("2: Couldn't get I/O connection to the localhost");
            System.exit(1);
        } finally {
            System.exit(0);
        }
    }
}
