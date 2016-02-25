import com.sun.istack.internal.NotNull;
import java.util.*;
import java.util.concurrent.*;

interface myListInterface<Key, Value> {
    Value put(@NotNull Key key, Value value);
    Value remove(@NotNull Key key);
    Value get(Key key);
    Key higherKey(@NotNull Key key);
    Key lowerKey(@NotNull Key key);
    boolean containsKey(@NotNull Key key);
    Integer size();
}

public class skiplistv2<Key extends Comparable<Key>, Value> implements myListInterface<Key, Value>{
    private class node<K extends Comparable<K>, V> {
        private node up, down, next, previous;
        private K key;
        private V value;

        public node(K key, V value) {
            this.key = key;
            this.value = value;
        }


        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V prev = this.value;
            this.value = value;
            return prev;
        }

        public void setUp(node up) {
            this.up = up;
        }

        public void setDown(node down) {
            this.down = down;
        }

        public void setNext(node next) {
            this.next = next;
        }

        public void setPrevious(node previous) {
            this.previous = previous;
        }

        public node<K, V> getUp() {
            return up;
        }

        public node<K, V> getDown() {
            return down;
        }

        public node<K, V> getNext() {
            return next;
        }

        public node<K, V> getPrevious() {
            return previous;
        }

        @Override
        public String toString() {
            node<K, V> tmp1 = this;
            StringBuilder result = new StringBuilder();

            while(tmp1.getDown() != null)
                tmp1 = tmp1.getDown();
            node<K, V> tmp2 = tmp1;

            for(; tmp2 != null; tmp2 = tmp2.getUp()) {
                result.append('[').append((tmp2.getKey() == null) ? "H" : tmp2.getKey().toString()).append(']');
            }

            if (tmp1.getNext() != null)
                result.append('\n').append(tmp1.getNext().toString());

            return result.toString();
        }
    }
    private node<Key, Value> head;
    private node<Key, Value> tail;
    final Random rand;
    private Integer height;
    private Integer size;
    private Key previousKey;
    private node<Key, Value> previousNode;

    public skiplistv2() {
        head = new node<Key, Value>(null, null);
        tail = new node<Key, Value>(null, null);
        head.setNext(tail);
        tail.setPrevious(head);

        rand = new Random(1);
        height = 0;
        size = 0;
        previousKey = null;
        previousNode = null;
    }

    public Value put(@NotNull Key key, Value value) {
        node<Key, Value> p = findEntry(key);
        if(p.getKey() != null && key.compareTo(p.getKey()) == 0)
            return p.setValue(value);

        node<Key, Value> newNode1 = new node<Key, Value>(key, value);
        newNode1.setPrevious(p);
        newNode1.setNext(p.getNext());

        p.getNext().setPrevious(newNode1);
        p.setNext(newNode1);

        Integer lvl = 0;
        while(rand.nextBoolean()) {
            if(lvl >= height) {
                node<Key, Value> tmp1 = new node<Key, Value>(null, null);
                node<Key, Value> tmp2 = new node<Key, Value>(null, null);

                tmp1.setNext(tmp2);
                tmp1.setDown(head);
                tmp2.setPrevious(tmp1);
                tmp2.setDown(tail);

                head.setUp(tmp1);
                head = tmp1;
                tail.setUp(tmp2);
                tail = tmp2;
                height++;
            }

            while(p.getUp() == null)
                p = p.getPrevious();
            p = p.getUp();

            node<Key, Value> newNode2 = new node<Key, Value>(key, value);
            newNode2.setPrevious(p);
            newNode2.setNext(p.getNext());
            newNode2.setDown(newNode1);

            p.getNext().setPrevious(newNode2);
            p.setNext(newNode2);
            newNode1.setUp(newNode2);

            newNode1 = newNode2;
            lvl++;
        }

        size++;
        return null;
    }

    private node<Key, Value> findNode(@NotNull Key key) {
        if(previousKey != null && previousNode != null && previousNode.getNext() != null && previousNode.getNext().getKey() != null && previousNode.getNext().getKey().compareTo(key) == 0) {
            previousKey = key;
            previousNode = previousNode.getNext();
            return previousNode;
        }
        if(previousKey != null && previousNode != null && previousNode.getPrevious() != null && previousNode.getPrevious().getKey() != null && previousNode.getPrevious().getKey().compareTo(key) == 0) {
            previousKey = key;
            previousNode = previousNode.getPrevious();
            return previousNode;
        }

        node<Key, Value> result = findEntry(key);
        if(result != null && key != null && result.getKey() != null && key.compareTo(result.getKey()) == 0) {
            if(!(result.getKey() == null)) {
                previousKey = key;
                previousNode = result;
                return result;
            }
            else {
                previousKey = null;
                previousNode = null;
                return null;
            }
        }
        else {
            previousKey = null;
            previousNode = null;
            return null;
        }
    }

    public Value remove(@NotNull Key key) {
        node<Key, Value> toRemove = findNode(key);
        Value result = null;

        if(toRemove != null)
            result = toRemove.getValue();

        for(; toRemove!=null; toRemove=toRemove.getDown()) {
            toRemove.getPrevious().setNext(toRemove.getNext());
            if(toRemove.getNext() != null)
                toRemove.getNext().setPrevious(toRemove.getPrevious());
        }

        while(head.getNext() == null){
            head = head.getDown();
            head.setUp(null);
        }

        size--;
        return result;
    }

    private node<Key, Value> findEntry(Key key) {
        node<Key, Value> p = head;

        while(true) {
            while (p.getNext() != null && p.getNext().getKey() != null && p.getNext().getKey().compareTo(key) <= 0) {
                p = p.getNext();
            }

            if(p.getDown() != null)
                p = p.getDown();
            else
                break;
        }

        return p;
    }

    public Value get(Key key) {
        return findNode(key).getValue();
    }

    public Key higherKey(@NotNull Key key) {
        node<Key, Value> found = findNode(key);

        while(found != null && found.getDown() != null)
            found = found.getDown();

        if (found != null)
            if(found.getNext() != null)
                return found.getNext().getKey();
            else
                return null;
        else
            return null;
    }

    public Key lowerKey(@NotNull Key key) {
        node<Key, Value> found = findNode(key);

        while(found != null && found.getDown() != null)
            found = found.getDown();

        if (found != null)
            if(found.getPrevious() != null)
                return found.getPrevious().getKey();
            else
                return null;
        else
            return null;
    }

    public boolean containsKey(@NotNull Key key){
        return (findNode(key) != null);
    }

    public Integer size() {
        return size;
    }

    @Override
    public String toString() {
        return head.toString();
    }


    public static void main(String[] args) {
        final Integer MAX = 100000;
        long sum1=0, sum2=0;

        List<Integer> keys = new ArrayList<Integer>();
        for(int i=0; i<MAX; i++)
            keys.add(i);
        Collections.shuffle(keys);

        skiplistv2<Integer, String> myList = new skiplistv2<Integer, String>();
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