package com.dijkstra;

import java.util.*;
import java.io.*;

interface sGraph {
    public int vSize(); //Zwraca liczbe wierzcholkow
    public int eSize(); //Zwraca liczbe krawedzi
    public Integer getWeight(Integer w1, Integer w2);   //Zwraca wage krawedzi w1-w2
    public int getDegree(Integer w);    //Zwraca stopien wierzcholka

    public boolean addVertex(Integer w);  //Dodaje wierzcholek
    public void addEdge(Integer w1, Integer w2, Integer weight);    //Dodaje krawedz
    public boolean hasVertex(Integer w);    //Sprawdza, czy wierzcholek istnieje
    public boolean hasEdge(Integer w1, Integer w2); //Sprawdza, czy krawedz istnieje
    public TreeMap<Integer,Integer> removeVertex(Integer w);   //Usuwa wierzcholek
    public boolean removeEdge(Integer w1, Integer w2);  //Usuwa krawedz w1-w2
    public List<Integer> graphV();    //Zwraca zbior wierzcholkow grafu
    public List<Integer> adjTo(Integer w);   //Zwraca zbior sasiadow wierzcholka

    public String toString();  //Wyswietla graf
    public String dijkstra(Integer w1, Integer w2); //Znajduje najkrotsza sciezke w1-w2
    public String bFord(Integer w1, Integer w2); //Znajduje najkrotsza sciezke w1-w2
}

public class slowG implements sGraph {
    private final TreeMap<Integer,TreeMap<Integer,Integer>> graph;    //Struktura grafu
    private int edges;  //Liczba krawedzi

    public slowG() {
        graph = new TreeMap<>();
        edges = 0;
    }

    public slowG(String file_name) throws IOException {
        graph = new TreeMap<>();
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


    @Override
    public int vSize() {
        return graph.size();
    }

    @Override
    public int eSize() {
        return edges;
    }

    @Override
    public Integer getWeight(Integer w1, Integer w2) {
        for(Map.Entry<Integer, TreeMap<Integer, Integer>> tmp1 : graph.entrySet())
            for(Map.Entry<Integer,Integer> tmp2 : tmp1.getValue().entrySet())
                if((tmp1.getKey().intValue()==w1) && (tmp2.getKey().intValue()==w2))
                    return tmp2.getValue();

        return -1;
    }

    @Override
    public int getDegree(Integer w) {
        if(graph.containsKey(w))
            return graph.get(w).size();
        else
            return 0;
    }

    @Override
    public boolean addVertex(Integer w) {
        TreeMap<Integer,Integer> tmp = new TreeMap<>();
        graph.put(w, tmp);

        return true;
    }

    @Override
    public void addEdge(Integer w1, Integer w2, Integer weight) {
        TreeMap<Integer,Integer> tmp;

        if(!hasVertex(w1))
            addVertex(w1);
        if(!hasVertex(w2))
            addVertex(w2);

        if(getDegree(w1) == 0) {
            tmp = new TreeMap<>();
            tmp.put(w2, weight);
            graph.put(w1, tmp);
        }
        else {
            tmp = graph.get(w1);
            tmp.put(w2, weight);
            graph.put(w1, tmp);
        }
        edges++;
    }

    @Override
    public boolean hasVertex(Integer w) {
        return graphV().contains(w);
    }

    @Override
    public boolean hasEdge(Integer w1, Integer w2) {
        for(Map.Entry<Integer,TreeMap<Integer,Integer>> tmp1 : graph.entrySet())
            for(Map.Entry<Integer,Integer> tmp2 : tmp1.getValue().entrySet())
                if((tmp1.getKey().intValue()==w1) && (tmp2.getKey().intValue()==w2))
                    return true;
        return false;
    }

    @Override
    public TreeMap<Integer,Integer> removeVertex(Integer w) {
        return graph.remove(w);
    }

    @Override
    public boolean removeEdge(Integer w1, Integer w2) {
        if(hasEdge(w1,w2)) {
            graph.get(w1).remove(w2);
            return true;
        }
        else
            return false;
    }

    @Override
    public List<Integer> graphV() {
        List<Integer> vertices = new LinkedList<>();

        for(Map.Entry<Integer,TreeMap<Integer,Integer>> tmp : graph.entrySet())
            vertices.add(tmp.getKey());
        return vertices;
    }

    @Override
    public List<Integer> adjTo(Integer w) {
        List<Integer> adj = new LinkedList<>();

        for(Map.Entry<Integer,TreeMap<Integer,Integer>> tmp1 : graph.entrySet())
            for(Map.Entry<Integer,Integer> tmp2 : tmp1.getValue().entrySet())
                if(tmp1.getKey().intValue()==w)
                    adj.add(tmp2.getKey());
        return adj;
    }

    @Override
    public String toString() {
        String output = new String();

        for(Map.Entry<Integer,TreeMap<Integer,Integer>> tmp1 : graph.entrySet())
            for(Map.Entry<Integer,Integer> tmp2 : tmp1.getValue().entrySet())
                output += tmp1.getKey()+ " -> " + tmp2.getKey() + " Waga: " + tmp2.getValue() + "\n";

        return output;
    }

    @Override
    public String dijkstra(Integer w1, Integer w2) {
        System.out.println("Algorytm Dijkstry:");
        if((!hasVertex(w1)) || (!hasVertex(w2)))
            return "Nie ma takiej œcie¿ki!\n";

        TreeMap <Integer,Integer> distance = new TreeMap<>();
        TreeMap <Integer,Integer> queue = new TreeMap<>();
        TreeMap<Integer,Integer> prevV = new TreeMap<>();
        Integer inf = Integer.MAX_VALUE-50;    //Nieskonczonosc
        Map.Entry<Integer,Integer> item;

        String result = new String();
        distance.put(w1, 0);
        queue.put(w1, 0);

        for(Map.Entry<Integer,TreeMap<Integer,Integer>> tmp1 : graph.entrySet()) {
            for(Map.Entry<Integer,Integer> tmp2 : tmp1.getValue().entrySet()) {
                if(tmp1.getKey().intValue() == w1) {    //Poczatkowe uzupelnienie "najblizszymi" wierzcholkami
                    distance.put(tmp2.getKey(), tmp2.getValue());
                    queue.put(tmp2.getKey(), tmp2.getValue());
                }
                else {  //Nastepnie utworzenie tablicy dystansow
                    if(!distance.containsKey(tmp2.getKey())) {
                        distance.put(tmp2.getKey(), inf);
                        queue.put(tmp2.getKey(), inf);
                    }
                }
            }
        }

        Integer disW1, disW2, tmpWeight, tmpSum;    //Zmienne pomocnicze: dystans od poczatku do W1, -||- W2, waga miedzy item a W2, suma wag od poczatku do W2
        while(queue.size() > 0) { //Dopoki kolejka nie jest pusta
            item = queue.firstEntry();  //Ustawienie item na poczatek kolejki
            for(Map.Entry<Integer,Integer> tmp : queue.entrySet())
                if(tmp.getValue() < item.getValue())    //Znalezienie najmniejszej wagi
                    item = tmp;
            queue.remove(item.getKey());    //I usuniecie tego elementu z kolejki

            for(Map.Entry<Integer,TreeMap<Integer,Integer>> tmp1 : graph.entrySet())
                for(Map.Entry<Integer,Integer> tmp2 : tmp1.getValue().entrySet())
                    if(tmp1.getKey().intValue() == item.getKey()) { //Jezeli znajdziemy element o najnizszej wadze w grafie, sprawdzamy jego sasiedztwo
                        disW1 = distance.get(item.getKey());    //Nadpisujemy zmienna distance do aktualnego elementu od zrodla
                        disW2 = distance.get(tmp2.getKey());    //-||- tmp2
                        tmpWeight = getWeight(item.getKey(), tmp2.getKey());    //Znalezienie wagi krawedzi item-tmp2
                        tmpSum = disW1 + tmpWeight;

                        if(tmpSum < disW2) { //Jezeli disW2 = inf lub jest wieksze
                            distance.put(tmp2.getKey(), tmpSum);    //Nadpisanie wlasciwej wagi
                            prevV.put(tmp2.getKey(), item.getKey());    //Dopisanie poprzednika dla tmp2
                        }
                    }
        }

        if(distance.get(w2).intValue() == inf)
            return "Nie ma takiej sciezki!\n";
        result += w1 + " -> " + w2 + " Minimalny koszt: " + distance.get(w2) + " \n";
        result += "Sciezka prowadzi przez wierzcholki: " + w1;

        Integer current = w2;
        String tmp = new String();
        while(true) {
            if(prevV.get(current) != null) {
                current = prevV.get(current);
                tmp = " -> " + current + tmp;
            }
            else
                break;
        }
        result += tmp + " -> " + w2 + "\n";

        return result;
    }

    @Override
    public String bFord(Integer w1, Integer w2) {
        System.out.println("Algorytm Bellmana-Forda:");
        if((!hasVertex(w1)) || (!hasVertex(w2)))
            return "Nie ma takiej œcie¿ki!\n";

        TreeMap <Integer,Integer> distance = new TreeMap<>();
        TreeMap<Integer,Integer> prevV = new TreeMap<>();
        Integer inf = Integer.MAX_VALUE-50;    //Nieskonczonosc

        List<Integer> tmp;
        Map.Entry<Integer,Integer> item;
        String result = new String();
        boolean check;

        tmp = graphV();
        for(int i=0; i<vSize(); i++) {
            if(tmp.get(i) == w1) {
                distance.put(w1, 0);
                prevV.put(tmp.get(i), -1);
                continue;
            }
            distance.put(tmp.get(i), inf);
            prevV.put(tmp.get(i), -1);
        }

        Integer current, next;
        for(int i=1; i<vSize(); i++) {
            check = true;
            for(int j=0; j<vSize(); j++) {
                current = graphV().get(j);
                tmp = adjTo(current);
                for(int k=0; k<tmp.size(); k++) {
                    next = tmp.get(k);
                    if(distance.get(next) > (distance.get(current) + getWeight(current, next))) {
                        check = false;
                        distance.remove(next);
                        distance.put(next, (distance.get(current) + getWeight(current, next)));
                        prevV.remove(next);
                        prevV.put(next, current);
                    }
                }
            }
            if(check)
                break;
        }

        if(distance.get(w2).intValue() == inf)
            return "Nie ma takiej sciezki!\n";
        result += w1 + " -> " + w2 + " Minimalny koszt: " + distance.get(w2) + " \n";
        result += "Sciezka prowadzi przez wierzcholki: ";

        Integer currentV = w2;
        String rev = new String();
        while(currentV != w1) {
            currentV = prevV.get(currentV);
            rev = currentV + " -> " + rev;
        }
        result += rev + w2 + "\n";

        return result;
    }
}