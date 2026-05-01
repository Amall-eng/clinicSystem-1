package kfupm.clinic.ds;

import java.util.List;
import java.util.ArrayList;

/** Students implement. */
public class LinkedQueue<T> {

    private class Node{
        T info;
        Node next;

        Node(T info) {
            this.info = info;
            this.next = null;
        }
    }

    private Node front,rear;


    public void enqueue(T item) { Node node = new Node(item);
    if ( isEmpty())
        front =node;
    else
        rear.next=node;
    rear=node;
    }
    public T dequeue() { if (isEmpty())
    return null;
        T item=front.info;
        front=front.next;
        if (front==null)
            rear=null;
        return item;
    }
    public boolean isEmpty() { return (front==null); }

    /** Non-destructive view for printing. */
    public List<T> toList() {   List<T> list = new ArrayList<>();
        Node current = front;
        while (current != null) {
            list.add(current.info);
            current = current.next;
        }

        return list;
    }
}
