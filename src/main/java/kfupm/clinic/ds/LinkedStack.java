package kfupm.clinic.ds;

/** Students implement. */
public class LinkedStack<T> {
    private class Node{
        T info;
        Node next;

        Node(T info) {
            this.info = info;
            this.next = null;
        }
    }
    private Node top;

    public void push(T item) { Node tmp=new Node(item);
    tmp.next=top;
    top=tmp;}
    public T pop() { if (isEmpty())return null;
            T item=top.info;
            top=top.next;
            return item; }
    public T peek() { if (isEmpty())
        return null;
        return top.info;  }
    public boolean isEmpty() {   return top == null; }
}
