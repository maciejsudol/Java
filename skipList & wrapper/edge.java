import java.io.Serializable;

public class edge implements Serializable {
    Integer begin_w;
    Integer weight;
    Integer end_w;

    edge()
    {
        begin_w = 0;
        weight = 0;
        end_w = 0;
    }

    edge(Integer w1, Integer weight, Integer w2)
    {
        begin_w = w1;
        this.weight = weight;
        end_w = w2;
    }
}
