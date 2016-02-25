package com.dijkstra;

import java.io.*;
import java.util.*;

public class file_reader
{
    String file_name;

    file_reader(String file)
    {
        file_name = file;
    }

    List<edge> get_data() throws IOException
    {
        List<edge> edges = new LinkedList<>();

        try (FileReader input = new FileReader(file_name))
        {
            String tmp = new String();
            int c;
            int counter = 1;
            int w1=0, weight=0, w2=0;

            while((c = input.read()) != -1)
            {
                if((c==40) || (c==44) || (c==41) || (c==10) || (c==13))
                {
                    if((c == 44) && (counter == 1))
                    {
                        w1 = Integer.parseInt(tmp);
                        tmp = "";
                        counter++;
                        continue;
                    }

                    if(counter == 2)
                    {
                        weight = Integer.parseInt(tmp);
                        tmp = "";
                        counter++;
                        continue;
                    }

                    if(counter == 3)
                    {
                        w2 = Integer.parseInt(tmp);
                        edges.add(new edge(w1, weight, w2));
                        tmp = "";
                        counter = 1;
                    }
                }
                else
                    tmp += (char)c;
            }
            input.close();
        }
        return edges;
    }
}

