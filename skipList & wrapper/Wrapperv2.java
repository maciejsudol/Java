import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.*;

public class Wrapperv2 implements InvocationHandler {
    private static Object obj;
    private static Logger logger;
    private static FileHandler fh;

    static {
        logger = Logger.getLogger(Wrapperv2.class.getName());
        try {
            fh=new FileHandler("logger.log", false);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
        }
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        logger.setLevel(Level.CONFIG);
    }

    private Wrapperv2(Object obj) {
        this.obj = obj;
    }

    public static Object newInstance(Object obj) {
        return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new Wrapperv2(obj));
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws
            Throwable {
        Object result;
        try {
            logger.log(Level.INFO,m.getName());
            long time_a = System.nanoTime();
            result = m.invoke(obj, args);
            long time_b = System.nanoTime();
            long execution = time_b - time_a;
            logger.log(Level.INFO, execution + " ns");
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        } finally {
            //
        }
        return result;
    }


    public static void main(String... args) {

        myListInterface<Integer, String> myList = (myListInterface<Integer, String>)Wrapperv2.newInstance(new skiplistv2<Integer, String>());
        final Integer MAX = 100000;
        long sum1=0, sum2=0;

        List<Integer> keys = new ArrayList<Integer>();
        for(int i=0; i<MAX; i++)
            keys.add(i);
        Collections.shuffle(keys);

        //skiplistv2<Integer, String> myList = new skiplistv2<Integer, String>();
        ConcurrentSkipListMap<Integer,String> conList = new ConcurrentSkipListMap<Integer,String>();

        System.out.println("---------------------------");
        System.out.println("Wyniki:");
        System.out.println("---------------------------");

        long startTime1 = System.currentTimeMillis();
        for(Integer tmp : keys)
            myList.put(tmp, "X");
        long endTime1 = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();
        for(Integer tmp : keys)
            conList.put(tmp, "X");
        long endTime2 = System.currentTimeMillis();
        sum1 += (endTime1-startTime1);
        sum2 += (endTime2-startTime2);

        System.out.println("Metoda put:");
        System.out.println("myList: " + (endTime1-startTime1) + "ms");
        System.out.println("conList: " + (endTime2-startTime2) + "ms");
        System.out.println("---------------------------");

        boolean check1 = true;
        startTime1 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            if (i+1 != myList.higherKey(i))
                check1 = false;
        }
        endTime1 = System.currentTimeMillis();

        boolean check2 = true;
        startTime2 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            if (i+1 != conList.higherKey(i))
                check2 = false;
        }
        endTime2 = System.currentTimeMillis();
        sum1 += (endTime1-startTime1);
        sum2 += (endTime2-startTime2);

        System.out.println("Metoda higherKey:");
        System.out.println("myList: " + (endTime1-startTime1) + "ms, check: " + check1);
        System.out.println("conList: " + (endTime2-startTime2) + "ms, check: " + check2);
        System.out.println("---------------------------");

        check1 = true;
        startTime1 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            if (!myList.containsKey(i)) {
                check1 = false;
                break;
            }
        }
        endTime1 = System.currentTimeMillis();

        check2 = true;
        startTime2 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            if (!conList.containsKey(i)) {
                check2 = false;
                break;
            }
        }
        endTime2 = System.currentTimeMillis();
        sum1 += (endTime1-startTime1);
        sum2 += (endTime2-startTime2);

        System.out.println("Metoda containsKey:");
        System.out.println("myList: " + (endTime1-startTime1) + "ms, check: " + check1);
        System.out.println("conList: " + (endTime2-startTime2) + "ms, check: " + check2);
        System.out.println("---------------------------");

        startTime1 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            myList.get(i);
        }
        endTime1 = System.currentTimeMillis();

        startTime2 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            conList.get(i);
        }
        endTime2 = System.currentTimeMillis();
        sum1 += (endTime1-startTime1);
        sum2 += (endTime2-startTime2);

        System.out.println("Metoda get:");
        System.out.println("myList: " + (endTime1-startTime1) + "ms");
        System.out.println("conList: " + (endTime2-startTime2) + "ms");
        System.out.println("---------------------------");

        startTime1 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            myList.remove(i);
        }
        endTime1 = System.currentTimeMillis();

        startTime2 = System.currentTimeMillis();
        for(int i=0; i<(MAX-1); i++) {
            conList.remove(i);
        }
        endTime2 = System.currentTimeMillis();
        sum1 += (endTime1-startTime1);
        sum2 += (endTime2-startTime2);

        System.out.println("Metoda remove:");
        System.out.println("myList: " + (endTime1-startTime1) + "ms");
        System.out.println("conList: " + (endTime2-startTime2) + "ms");
        System.out.println("---------------------------");
        System.out.println("Sumarycznie:");
        System.out.println("myList: " + sum1 + "ms");
        System.out.println("conList: " + sum2 + "ms");
        System.out.println("---------------------------");
    }

}
