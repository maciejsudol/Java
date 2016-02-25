import java.util.*;
import java.util.concurrent.*;

public class skipList<Key extends Comparable<Key>, Value> {
    private class node<K extends Comparable<K>, V> {
        private node up, down, next, previous;
        private K key;
        private V value;
        private Integer lvl;

        public node(K key, V value, Integer lvl) {
            this.key = key;
            this.value = value;
            this.lvl = lvl;
        }


        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
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

        public Integer getLvl() {
            return lvl;
        }

        public void put(K key, V value, Integer lvl, node<K, V> parent) {
            if (this.lvl <= lvl && (next == null || next.getKey().compareTo(key) > 0)) {
                node<K, V> newNode = new node<K, V>(key, value, this.lvl);

                if (next != null) {
                    next.setPrevious(newNode);
                    newNode.setNext(next);
                }
                next = newNode;
                newNode.setPrevious(this);

                if (parent != null) {
                    newNode.setUp(parent);
                    parent.setDown(newNode);
                }

                if (down != null)
                    down.put(key, value, lvl, newNode);
            }
            else if (next != null && next.getKey().compareTo(key) < 0)
                next.put(key, value, lvl, parent);
            else if (next != null && next.getKey().compareTo(key) == 0)
                return;
            else if (down != null)
                down.put(key, value, lvl, parent);
        }

        public node<K, V> find(K key) {
            if(next != null) {
                Integer comparison = next.getKey().compareTo(key);

                if(comparison == 0)
                    return next;
                else if (comparison < 0)
                    return next.find(key);
                else if (down != null)
                    return down.find(key);
                else
                    return null;
            }
            else if (down != null)
                return down.find(key);
            else
                return null;
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
    private node<Key, Value> head = new node<Key, Value>(null, null, 0);
    final static Random rand = new Random(1);
    private final Integer range;

    public skipList(Integer range) {
        this.range = range;
    }

    public Value put(Key key, Value value) {
        if(/*!containsKey(key)*/true) {
            Integer lvl = rand.nextInt(range);

            while (head.getLvl() < lvl) {
                node<Key, Value> newNode = new node<Key, Value>(null, null, head.getLvl() + 1);
                head.setUp(newNode);
                newNode.setDown(head);
                head = newNode;
            }
            head.put(key, value, lvl, null);
            return null;
        }
        node<Key, Value> found = head.find(key);
        Value result = found.getValue();
        found.setValue(value);

        return result;
    }

    public Value remove(Key key) {
        node<Key, Value> toRemove = head.find(key);
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

        return result;
    }

    public Value get(Key key) {
        return head.find(key).getValue();
    }

    public Key higherKey(Key key) {
        node<Key, Value> found = head.find(key);

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

    public Key lowerKey(Key key) {
        node<Key, Value> found = head.find(key);

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

    boolean containsKey(Key key){
        return (head.find(key) != null);
    }

    @Override
    public String toString() {
        return head.toString();
    }

    public static void main(String[] args) {
        final Integer MAX = 100000;

        List<Integer> keys = new ArrayList<Integer>();
        for(int i=0; i<MAX; i++)
            keys.add(i);
        Collections.shuffle(keys);

        skipList<Integer, String> myList = new skipList<Integer, String>(50);
        ConcurrentSkipListMap<Integer,String> conList = new ConcurrentSkipListMap<Integer,String>();

        boolean check1 = true;
        long startTime1 = System.currentTimeMillis();
        for(Integer tmp : keys)
            myList.put(tmp, "X");

        for(int i=0; i<(MAX-1); i++) {
            if (i+1 != myList.higherKey(i))
                check1 = false;
        }
        long endTime1 = System.currentTimeMillis();

        boolean check2 = true;
        long startTime2 = System.currentTimeMillis();
        for(Integer tmp : keys)
            conList.put(tmp, "X");

        for(int i=0; i<(MAX-1); i++) {
            if (i+1 != conList.higherKey(i))
                check2 = false;
        }
        long endTime2 = System.currentTimeMillis();


        System.out.println("---------------------------");
        System.out.println("Wyniki:");
        System.out.println("myList: " + (endTime1-startTime1) + "ms, check: " + check1);
        System.out.println("conList: " + (endTime2-startTime2) + "ms, check: " + check2);
        System.out.println("---------------------------");
    }
}
