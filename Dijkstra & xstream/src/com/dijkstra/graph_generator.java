package com.dijkstra;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class graph_generator {
    public static void main(String[] args) throws FileNotFoundException
    {
        int w = 1000;
        int range = 30;
        int multiplier = 20;
        String file_name = "s.txt";

        edge[] tab = new edge[w*multiplier];
        for(int i=0; i<w*multiplier; i++)
            tab[i] = new edge();

        Random rand = new Random();
        for(int i=0; i<w; i++)
        {
            tab[i].begin_w = i;
            tab[i].end_w = i+1;
            tab[i].weight = 1 + rand.nextInt(range);
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
                tab[i].begin_w = tmp1;
                tab[i].end_w = tmp2;
                tab[i].weight = 1 + rand.nextInt(range);
                //System.out.println(tmp1 + " " + tmp2 + "\n");
            }
            check = true;
        }

        PrintWriter output;
        output = new PrintWriter(file_name);
        //output.println(w + "\n");
        for(edge x : tab)
        {
            //System.out.println(x.begin_w + " " + x.weight + " " + x.end_w + "\n");
            output.println("(" + x.begin_w + "," + x.weight + "," + x.end_w + ")\n");
        }
        output.close();
    }
}

