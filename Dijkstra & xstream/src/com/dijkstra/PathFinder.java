import java.io.*;
import java.util.*;

enum Kolor{
    BIALY, SZARY, CZARNY
}
//----------------------------------------------------------------------------------------------
class Edge{
    Vert w2;
    int waga;
    Edge(int y, int z){
        w2 = new Vert(y);
        waga = z;
    }
    Edge(Vert ref, int len){
        w2 = ref;
        waga = len;
    }
}
//----------------------------------------------------------------------------------------------		
class Vert implements Comparable<Vert>{
    int numer,		//nr wierzcho³ka
            d;		//odleg³oœæ od wierzcho³ka pocz.
    Kolor kolor;
    Vert parent;	//poprzednik
    List<Edge> adjList;
    Vert(int x){
        numer = x;
        d=Integer.MAX_VALUE;
        kolor = Kolor.BIALY;
        parent = null;
        adjList  = new ArrayList<Edge>();
    }

    public int compareTo(Vert x){
        if (this.d < x.d) return -1;
        if (this.d > x.d) return +1;
        return 0;
    }
    String wypisz(){
        return this.numer + "," + this.d;
    }
}
//----------------------------------------------------------------------------------------------
class TmpVert{
    int w1,w2,d;
    TmpVert(String x, String y, String z){
        w1=Integer.parseInt(x);
        d=Integer.parseInt(y);
        w2=Integer.parseInt(z);
    }
    String wypisz(){
        //System.out.println(x.w1 + " " + x.w2 + " " + x.length);
        return this.w1 + "," + this.w2 + "," + this.d;
    }
}
//----------------------------------------------------------------------------------------------

class Graf{
    static PriorityQueue<Vert> vertsQueue;
    Vert pocz, konc;

    static Vert findVert(int nr){
        for(Vert szukany:vertsQueue){
            if (szukany.numer == nr)
                return szukany;
        }
        return null;
    }
    //---------------------
    Graf(String filename) throws IOException{
        vertsQueue = new PriorityQueue<Vert>();
        FileReader fr = new FileReader(filename);
        BufferedReader in = new BufferedReader(fr);
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = in.readLine() ) != null)
            sb.append(s + ",");
        sb.delete(sb.length()-1, sb.length());	//usuwam ostatni znak nowej linii
        in.close();
        String [] items = sb.toString().split(",");	//usuwam przecinki ze Stringów

        //for (int i=0; i<10; i++)
        //	System.out.println(" item= " + items[i]);
        //System.out.println(Arrays.toString(items));
        //List<int> items = (int) (Arrays.asList(sb.toString().split(",")));

        List<TmpVert> tmpListV = new ArrayList<TmpVert>();			//tworzê listê krawêdzi przekszta³caj¹c Stringi z tablicy
        for(int i=0; i<items.length; i=i+3){
            tmpListV.add(new TmpVert(items[i],items[i+1],items[i+2]));
        }
        Vert v1, v2;
        for(TmpVert nowa:tmpListV){	//nowa krawedz z listy
            v1 = findVert(nowa.w1);		//1. wierzcho³ek bie¿¹cej krawêdzi
            v2 = findVert(nowa.w2);		//2. wierzcho³ek bie¿¹cej krawêdzi
            if (v1==null){
                v1 = new Vert(nowa.w1);
                vertsQueue.offer(v1);	//dodajê aktualny wierzcho³ek do kolejki
            }
            if (v2==null){
                v2 = new Vert(nowa.w2);
                vertsQueue.offer(v2);	//dodajê aktualny wierzcho³ek do kolejki
            }
            v1.adjList.add(new Edge(v2, nowa.d));	//dodajê aktualn¹ krawêdŸ do listy s¹siedztwa
            v2.adjList.add(new Edge(v1, nowa.d));	//dodajê aktualn¹ krawêdŸ do listy s¹siedztwa
        }
		/*		System.out.println("kolejka wierzcho³ków:");
		//wypisz kolejke i listê s¹siedztwa
		for(Vert v:vertsQueue){
			System.out.print(v.wypisz());		
			for(Edge e:v.adjList)
				System.out.print(" -> " + e.w2.w1 + "," + e.waga);
			System.out.println();
		}
		System.out.println("size: " + vertsQueue.size());
		*/
    }
    //--------------------
    int ileWierzch(){
        return vertsQueue.size();
    }

    void initialize(int iniVert, int finVert){
        if(iniVert >=ileWierzch() || finVert >=ileWierzch()){
            System.out.println("Któryœ z podanych wierzcho³ków nie istnieje.");
            return;
        }
        for(Vert biezacy:vertsQueue)
            if (biezacy.numer == iniVert){
                pocz = biezacy;
                biezacy.d = 0;
                biezacy.parent = null;
                vertsQueue.remove(biezacy);
                vertsQueue.offer(biezacy);
                break;
            }
        for(Vert v2:vertsQueue) if(v2.numer == finVert) konc = v2;
    }

    //---------------------
    void Dijkstra(){
        List<Vert> done = new ArrayList<Vert>();	//lista z wierzcho³kami przetworzonymi przez algorytm
        while (vertsQueue.isEmpty() == false){
            Vert NajblizszyWierz=vertsQueue.peek();	// u -wierzcholek o najmniejszej odl. od zrodla
            done.add(NajblizszyWierz);
            for(Edge biezKraw:NajblizszyWierz.adjList){
                if(biezKraw.w2 == null) continue;
                if(biezKraw.w2.d > NajblizszyWierz.d+biezKraw.waga){
                    biezKraw.w2.d=NajblizszyWierz.d+biezKraw.waga;
                    biezKraw.w2.parent = NajblizszyWierz;
                    vertsQueue.remove(biezKraw.w2);
                    vertsQueue.offer(biezKraw.w2);
                }
            }
            vertsQueue.remove(NajblizszyWierz);
        }
		/*	
		for(Vert v11:vertsQueue)		//test, czy wszystkie wierzch przeniesione do listy
			System.out.print("queue:" + v11.w1+", ");
		System.out.println();
		for(Vert v12:done)
			System.out.print("done:" + v12.w1+", ");
		*/
        Deque<Integer> stos = new ArrayDeque<Integer>();
        Vert tmpV = konc;
        while (tmpV != null){
            stos.push(tmpV.numer);
            tmpV=tmpV.parent;
        }
		/*	
		for(Vert v11:vertsQueue)		//test, czy wszystkie wierzch przeniesione do listy
			System.out.print("queue:" + v11.w1+", ");
		System.out.println();
		for(Vert v12:done)
			System.out.print("done:" + v12.w1+", ");
		*/

        //wypisanie wyników na ekran
        System.out.println("Metoda Dijkstry. \nD³ugoœæ najkrótszej œcie¿ki z w. "+pocz.numer +" do "+konc.numer+" wynosi: "+ konc.d +"\n");
        System.out.print(stos.poll());
        while(stos.isEmpty() == false)
            System.out.print( "->" + stos.poll());
        System.out.println();
    }

    void print_path(Vert pocz, Vert konc){
        if (pocz.numer == konc.numer)
            System.out.print("D³ugoœæ œcie¿ki: "+ pocz.d + ". " + pocz.numer + "->");
        else if(konc.parent==null)
            System.out.println("œcie¿ka nie istnieje");
        else{
            print_path(pocz,konc.parent);
            System.out.print(konc.numer+ "->");
        }
    }

    void BSF(){
        pocz.kolor = Kolor.SZARY;
        Queue<Vert> greyVerts = new LinkedList<Vert>();
        greyVerts.offer(pocz);
        Vert biezacy;
        while(greyVerts.isEmpty() == false){
            biezacy = greyVerts.poll();
            for(Edge v:biezacy.adjList)
                if (v.w2.kolor == Kolor.BIALY){
                    v.w2.kolor = Kolor.SZARY;
                    v.w2.d = biezacy.d+v.waga;
                    v.w2.parent = biezacy;
                    greyVerts.offer(v.w2);
                }
            //greyVerts.remove()
            biezacy.kolor = Kolor.CZARNY;
        }
        //	Vert tmp1=pocz, tmp2=konc;
        //print_path(tmp1,tmp2);

        Deque<Integer> stos = new ArrayDeque<Integer>();
        Vert tmpV = konc;
        while (tmpV != null){
            stos.push(tmpV.numer);
            tmpV=tmpV.parent;
        }

        //wypisanie wyników na ekran
        System.out.println("Metoda przesukiwania wszerz. \nD³ugoœæ najkrótszej œcie¿ki z w. "+pocz.numer +" do "+konc.numer+" wynosi: "+ konc.d +"\n");
        System.out.print(stos.poll());
        while(stos.isEmpty() == false)
            System.out.print( "->" + stos.poll());
        System.out.println();
    }
}

//----------------------------------------------------------------------------------------------
public class PathFinder {
    public static void main(String[] args) throws IOException{
        long startTime = System.nanoTime();
        String plik = "graf.txt";
        int iniVert = 3, finVert = 1992;
        Graf graf = new Graf(plik);
        graf.initialize(iniVert, finVert);
        graf.Dijkstra();
        //graf.BSF();
        //System.out.println(Buffer.czytaj(plik));
        long endTime = System.nanoTime();
        System.out.println("\nTook "+(endTime - startTime)/10e-9 + " s");
    }
}