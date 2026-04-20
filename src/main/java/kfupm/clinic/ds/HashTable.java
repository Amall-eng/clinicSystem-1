package kfupm.clinic.ds;

/**
 * Students implement a hash table.
 * Recommended collision handling: separate chaining.
 */
public class HashTable<K, V> {

    private static final int initial_size = 13;

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

    public void put(K key, V value) {  int index = hash(key);

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
    public V get(K key) { throw new UnsupportedOperationException("TODO: HashTable.get"); }
    public V remove(K key) { throw new UnsupportedOperationException("TODO: HashTable.remove"); }
    public int size() {return size;}
    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }
}
