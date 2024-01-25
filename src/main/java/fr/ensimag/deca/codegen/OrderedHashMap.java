package fr.ensimag.deca.codegen;

import java.util.HashMap;
import java.util.LinkedList;

public class OrderedHashMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 5554277472235530940L;
    private final LinkedList<K> keysList;
    private final LinkedList<V> valuesList;

    public OrderedHashMap() {
        super();

        this.keysList = new LinkedList<>();
        this.valuesList = new LinkedList<>();
    }

//    public void addFirst(K key, V value) {
//        if (!containsKey(key)) {
//            put(key, value);
//            keysList.addFirst(key);
//            valuesList.addFirst(value);
//        }
//    }

    public void addLast(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            keysList.addLast(key);
            valuesList.addLast(value);
        }
    }

    public void updateValue(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
        }
    }

    public LinkedList<K> getOrderedKeys() {
        return keysList;
    }

    public LinkedList<V> getOrderedValues() {
        return valuesList;
    }
}
