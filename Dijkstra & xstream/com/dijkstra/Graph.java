package com.dijkstra;
import java.util.*;
import java.io.*;

interface IGraph {
    int vSize(); //Zwraca liczbe wierzcholkow
    int eSize(); //Zwraca liczbe krawedzi
    Integer getWeight(Integer w1, Integer w2);   //Zwraca wage krawedzi w1-w2
    int getDegree(Integer w);    //Zwraca stopien wierzcholka

    //boolean addVertex(Vertex w);  //Dodaje wierzcholek
    void addEdge(Integer w1, Integer w2, Integer weight);    //Dodaje krawedz
    boolean hasVertex(Integer w);    //Sprawdza, czy wierzcholek istnieje
    boolean hasEdge(Integer w1, Integer w2); //Sprawdza, czy krawedz istnieje
    boolean removeVertex(Integer w);   //Usuwa wierzcholek
    boolean removeEdge(Integer w1, Integer w2);  //Usuwa krawedz w1-w2
    List<Integer> graphV();    //Zwraca zbior wierzcholkow grafu
    List<Edgee> adjTo(Integer w);   //Zwraca zbior sasiadow wierzcholka
    Vertex findV(int idx);  //Zwraca szukany wierzcholek

    String toString();  //Wyswietla graf
    String dijkstra(Integer w1, Integer w2); //Znajduje najkrotsza sciezke w1-w2
    String bFord(Integer w1, Integer w2); //Znajduje najkrotsza sciezke w1-w2
}

public class Graph implements IGraph, Externalizable {
    private PriorityQueue<Vertex> vertices;
    private int edges;  //Liczba krawedzi

    public Graph() {
        vertices = new PriorityQueue<>();
        edges = 0;
    }

    public Graph(String file_name) throws IOException {
        vertices = new PriorityQueue<>();
        edges = 0;

        try (FileReader input = new FileReader(file_name))
        {
            String tmp = new String();
            int c;
            int counter = 1;
            int w1=0, weight=0, w2=0;

            while((c = input.read()) != -1) {
                if((c==40) || (c==44) || (c==41) || (c==10) || (c==13)) {
                    if((c == 44) && (counter == 1)) {
                        w1 = Integer.parseInt(tmp);
                        tmp = "";
                        counter++;
                        continue;
                    }

                    if(counter == 2) {
                        weight = Integer.parseInt(tmp);
                        tmp = "";
                        counter++;
                        continue;
                    }

                    if(counter == 3) {
                        w2 = Integer.parseInt(tmp);
                        addEdge(w1, w2, weight);
                        tmp = "";
                        counter = 1;
                    }
                }
                else
                    tmp += (char)c;
            }
            input.close();
        }
    }

    public void generator() {
        vertices = new PriorityQueue<>();
        edges = 0;

        int w = 2000;
        int range = 30;
        int multiplier = 20;

        edge[] tab = new edge[w*multiplier];
        for(int i=0; i<w*multiplier; i++)
            tab[i] = new edge();

        Random rand = new Random();
        for(int i=0; i<w; i++) {
            tab[i] = new edge(i, 0, i+1);
            addEdge(i, i + 1, 1 + rand.nextInt(range));
        }

        int tmp1, tmp2;
        boolean check = true;
        for(int i=w; i<w*multiplier; i++)
        {
            tmp1 = rand.nextInt(w);
            tmp2 = rand.nextInt(w-tmp1+1) + tmp1;

            while(tmp2 == tmp1)
                tmp2 = rand.nextInt(w-tmp1+1) + tmp1;

            for(edge x : tab)
                if(((tmp1 == x.begin_w) && (tmp2 == x.end_w)) || ((tmp1 == x.end_w) && (tmp2 == x.begin_w)))
                {
                    i--;
                    check = false;
                    break;
                }
            if(check)
            {
                tab[i] = new edge(tmp1, 0, tmp2);
                addEdge(tmp1, tmp2, 1 + rand.nextInt(range));
            }
            check = true;
        }
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        edge[] edg = new edge[edges];
        int i = 0;
        for(Vertex tmp1 : vertices)
            for (Edgee tmp2 : tmp1.adjTo) {
                edg[i] = new edge(tmp1.index, tmp2.weight, tmp2.index2.index);
                i++;
            }
        out.writeObject(edg);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        vertices = new PriorityQueue<>();
        edges = 0;
        edge[] edg = (edge[])in.readObject();

        for(edge current : edg) {
            addEdge(current.begin_w, current.end_w, current.weight);
        }
    }


    @Override
    public int vSize() {
        return vertices.size();
    }

    @Override
    public int eSize() {
        return edges;
    }

    @Override
    public Integer getWeight(Integer w1, Integer w2) {
        for(Edgee current : findV(w1).adjTo) {
            if(current.index2.index.equals(w2))
                return current.weight;
        }
        return -1;
    }

    @Override
    public int getDegree(Integer w) {
        if(hasVertex(w))
            return findV(w).adjTo.size();
        else
            return 0;
    }

    @Override
    public void addEdge(Integer w1, Integer w2, Integer weight) {
        Vertex v1 = findV(w1);
        Vertex v2 = findV(w2);

        if(v1 == null) {
            v1 = new Vertex(w1);
            vertices.offer(v1);
        }
        if(v2 == null) {
            v2 = new Vertex(w2);
            vertices.offer(v2);
        }

        v1.adjTo.add(new Edgee(v2, weight));
        edges++;
    }

    @Override
    public boolean hasVertex(Integer w) {
        for(Vertex current : vertices) {
            if(current.index.equals(w))
                return true;
        }
        return false;
    }

    @Override
    public boolean hasEdge(Integer w1, Integer w2) {
        if(findV(w1).adjTo.contains(new Vertex(w2)))
            return true;
        else
            return false;
    }

    @Override
    public boolean removeVertex(Integer w) {
        return vertices.remove(findV(w));
    }

    @Override
    public boolean removeEdge(Integer w1, Integer w2) {
        if(hasVertex(w1)) {
            Vertex v2 = new Vertex(w2);
            findV(w1).adjTo.remove(v2);
            return true;
        }
        else
            return false;
    }

    @Override
    public List<Integer> graphV() {
        List<Integer> verts = new ArrayList<>();
        for(Vertex current : vertices)
            verts.add(current.index);
        return verts;
    }

    @Override
    public List<Edgee> adjTo(Integer w) {
        return findV(w).adjTo;
    }

    @Override
    public Vertex findV(int idx) {
        for(Vertex current : vertices) {
            if(current.index.equals(idx))
                return current;
        }
        return null;
    }

    @Override
    public String toString() {
        String out = new String();

        for(Vertex tmp1 : vertices)
            for (Edgee tmp2 : tmp1.adjTo)
                out += tmp1.index + " -> " + tmp2.index2.index + " Waga: " + tmp2.weight + "\n";

        return out;
    }

    @Override
    public String dijkstra(Integer w1, Integer w2) {
        System.out.println("Algorytm Dijkstry:");
        if(!(w1>=0) || !(w2<vertices.size()))
            return "Nie ma takiej sciezki!\n";

        PriorityQueue<Vertex> verts = new PriorityQueue<>();
        Vertex v1 = findV(w1);
        Vertex v2 = findV(w2);
        v1.distance = 0;
        String result = new String();
        Deque<Integer> stack = new ArrayDeque<>();
        Vertex smallestW;

        while(!vertices.isEmpty()) {
            smallestW = vertices.peek();
            for(Edgee curE : smallestW.adjTo) {
                if(curE == null)
                    continue;
                if(curE.index2.distance > (smallestW.distance + curE.weight)) {
                    curE.index2.distance = smallestW.distance + curE.weight;
                    curE.index2.parent = smallestW;
                    vertices.remove(curE.index2);
                    vertices.offer(curE.index2);
                }
            }
            verts.offer(smallestW);
            vertices.remove(smallestW);
        }
        vertices = verts;

        if(v2.distance == Integer.MAX_VALUE - 50)
            return "Nie ma takiej sciezki!\n";
        result += w1 + " -> " + w2 + " Minimalny koszt: " + v2.distance + " \n";
        result += "Sciezka prowadzi przez wierzcholki: ";

        while(v2 != null) {
            stack.push(v2.index);
            v2 = v2.parent;
        }
        while(!stack.isEmpty()) {
            if(stack.peek().equals(w2))
                result += stack.poll();
            else
                result += stack.poll() + "->";
        }
        result += "\n";

        return result;
    }

    @Override
    public String bFord(Integer w1, Integer w2) {
        System.out.println("Algorytm Bellmana-Forda:");
        if(!(w1>=0) || !(w2<vertices.size()))
            return "Nie ma takiej œcie¿ki!\n";

        Vertex v1 = findV(w1);
        Vertex v2 = findV(w2);
        v1.distance = 0;
        v1.parent = null;
        String result = new String();
        Deque<Integer> stack = new ArrayDeque<>();

        for(Vertex counter : vertices) {
            for(Vertex current : vertices) {
                for (Edgee next : current.adjTo) {
                    if (next.index2.distance > (current.distance + next.weight)) {
                        next.index2.distance = current.distance + next.weight;
                        next.index2.parent = current;
                    }
                }
            }
        }

        if(v2.distance == Integer.MAX_VALUE - 50)
            return "Nie ma takiej sciezki!\n";
        result += w1 + " -> " + w2 + " Minimalny koszt: " + v2.distance + " \n";
        result += "Sciezka prowadzi przez wierzcholki: ";

        while(v2 != null) {
            stack.push(v2.index);
            v2 = v2.parent;
        }
        while(!stack.isEmpty()) {
            if(stack.peek().equals(w2))
                result += stack.poll();
            else
                result += stack.poll() + "->";
        }
        result += "\n";

        return result;
    }
}