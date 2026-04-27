package kfupm.clinic.ds;

import java.util.function.BiConsumer;

/**
 * Students implement AVL tree (balanced BST).
 */
// K represents the key used for ordering nodes in the AVL tree,
// so it must implement Comparable to allow comparisons between keys.
// V represents the stored value associated with each key,
// and it does not need to implement Comparable because it is not used for ordering.

public class AVLTree<K extends Comparable<K>, V> {

    //Node class to presentelem in AVL
    private class Node {
        K key;
        V value;
        Node leftchild;
        Node rightchild;
        int h; //hight

        //Constructor :
        Node(K key, V val){
            this.key = key;
            this.value = val;
            this.leftchild = null;
            this.rightchild = null;
            this.h = 1;
        }
    }
    //Creat a root of AVL tree
    private Node root ;

    // Helper methode to return height of each node
    private int hight(Node node) {
        if (node == null) {
            return 0;
        }
        return node.h;
    }

    public void put(K key, V value) { throw new UnsupportedOperationException("TODO: AVLTree.put"); }
    public V get(K key) { throw new UnsupportedOperationException("TODO: AVLTree.get"); }
    public void remove(K key) { throw new UnsupportedOperationException("TODO: AVLTree.remove"); }

    /** In-order traversal (sorted by key). */
    public void inOrder(BiConsumer<K, V> visitor) { throw new UnsupportedOperationException("TODO: AVLTree.inOrder"); }

    /** Returns the smallest key/value (leftmost node), or null if empty. */
    public Entry<K, V> minEntry() { throw new UnsupportedOperationException("TODO: AVLTree.minEntry"); }

    public record Entry<K, V>(K key, V value) {}
}
