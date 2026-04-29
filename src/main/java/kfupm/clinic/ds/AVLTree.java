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
    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.h;
    }
    // Return balance factor
    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }

        return height(node.leftchild) - height(node.rightchild);
    }

    // Right rotation
    private Node rotateRight( Node node) {

        Node temp = node.leftchild;
        Node right = temp.rightchild;

        temp.rightchild = node;
        node.leftchild = right;

        node.h = 1+ Math.max(height(node.leftchild), height(node.rightchild));
        temp.h = 1+ Math.max(height(temp.leftchild), height(temp.rightchild));
        return temp;
    }


    //Insert key and val into Tr
    public void put(K key, V value) {
        root = put(root, key, value);
    }
    //Helper method:
    private Node put(Node node, K key, V value) {
        if (node == null) {
              return new Node(key, value);
        }
        if(key.compareTo(node.key) < 0){
            node.leftchild = put(node.leftchild, key, value);
        }
        else if (key.compareTo(node.key) > 0) {
            node.rightchild = put(node.rightchild, key, value);
        }
        else{
            node.value = value;
            return node;
        }
        //Update height
        node.h = 1 + Math.max(height(node.leftchild), height(node.rightchild));
        return node;
    }

    // Search for key and return the val
    public V get(K key) {
        Node currentNode = root;
        while (currentNode != null) {
            if (currentNode.key.compareTo(key) == 0) {
                return currentNode.value;   //key found
            }
            if (currentNode.key.compareTo(key) > 0) {
                currentNode = currentNode.leftchild; // left subtree(smaller)
            }
            else {
                currentNode = currentNode.rightchild; // right subtree(larger)
            }
        }
        return null;

    }


    public void remove(K key) { throw new UnsupportedOperationException("TODO: AVLTree.remove"); }
    /** In-order traversal (sorted by key). */
    // BiConsumer used to apllay operation on (key, value)
    public void inOrder(BiConsumer<K, V> visitor) {
        //Start traversal from root
        inOrder(root, visitor);
    }
    //Helper methode to recursively traverse
    private void inOrder(Node node, BiConsumer<K, V> visitor) {
        // Noood is empty
        if (node == null) {
            return;
        }
        // Visit left subtree
        inOrder(node.leftchild, visitor);
        // visit node
        visitor.accept(node.key, node.value);
        //Visit right subtree
        inOrder(node.rightchild, visitor);
    }
    /** Returns the smallest key/value (leftmost node), or null if empty. */
    public Entry<K, V> minEntry() {
        if (root == null) {
            return null;
        }
        Node cureent = root;
        while (cureent.leftchild != null) {
            cureent = cureent.leftchild;    // smallest one
        }
        return new Entry<>(cureent.key, cureent.value); // smallest Entry
    }

    public record Entry<K, V>(K key, V value) {}
}
