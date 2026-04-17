package kfupm.clinic.ds;

import java.util.List;
import java.util.ArrayList;

/** Students implement. */
public class SinglyLinkedList<T> {
    private class Node{
        T info;
        Node next;

        Node(T info){
            this.info = info;
            this.next = null;
        }
   }
   private Node head;
   public void addLast(T item) {
        Node node = new Node(item);
        if (head == null){              // Empty --> new node = head
            head = node;
            return;
        }
        Node temp = head;              //Traverse to reach last node
        while (temp.next != null){
            temp = temp.next;
        }
        temp.next = node;               // last node = new node
    }
    public List<T> toList() {
       List<T> list = new ArrayList<>();
       Node temp = head;
        while (temp != null){
            list.add(temp.info);
            temp = temp.next;
        }
        return list;
    }

}
