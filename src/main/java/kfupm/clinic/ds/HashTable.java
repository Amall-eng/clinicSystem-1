package kfupm.clinic.ds;

/**
 * Students implement a hash table.
 * Recommended collision handling: separate chaining.
 */
public class HashTable<K, V> {

    private static final int initial_size = 13;
    private static final double load_factor_limit = 0.75;

    private Node<K, V>[] table;
    private int size;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    public HashTable() { table = (Node<K, V>[]) new Node[initial_size];
        size = 0;}

    public void put(K key, V value) {  if ((size + 1.0) / table.length > load_factor_limit) {
        resize(table.length * 2 + 1);
    }

        int index = hash(key);

        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        table[index] = new Node<K, V>(key, value, table[index]);
        size++; }
    public V get(K key) {   int index = hash(key);

        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null; }
    public V remove(K key) {
        int index = hash(key);

        Node<K, V> current = table[index];
        Node<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                size--;
                return current.value;
            }

            prev = current;
            current = current.next;
        }

        return null;
    }
    public int size() {return size;}
    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    private void resize(int newSize) {
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[newSize];
        size = 0;

        for (int i = 0; i < oldTable.length; i++) {
            Node<K, V> current = oldTable[i];

            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
}