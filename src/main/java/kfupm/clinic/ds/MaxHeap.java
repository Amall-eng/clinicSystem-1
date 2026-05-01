package kfupm.clinic.ds;

import java.util.*;

/**
 * MaxHeap starter.
 *
 * Day-1 safety rule:
 * - Constructors must NEVER throw, so the program can start.
 * - Operations may throw UnsupportedOperationException until students implement them;
 *   the command dispatcher will catch and print [NOT SUPPORTED] instead of crashing.
 */
public class MaxHeap<T> {

    protected final Comparator<T> comparator;

    public MaxHeap(Comparator<T> comparator) {
        if (comparator == null) throw new IllegalArgumentException("comparator is null");
        this.comparator = comparator;
    }
    private ArrayList<T> elements =  new ArrayList<>();
    //Insert methode
    public void push(T item) {
        elements.add(item);
        shiftUp(elements.size() - 1);
    }
    //Remove methode
    public T pop() {
        if(elements.isEmpty()) {
            return null;
        }
        T item = elements.get(0);
        T lastItem = elements.remove(elements.size() - 1);
        if (elements.size() > 0){
            elements.set(0, lastItem);
            shiftDown(0);
        }
        return item;
    }

    // Return item without remove it
    public T peek() {
        if(elements.isEmpty()) {
            return null;
        }
        return elements.get(0);
    }
    // Check if heap is empty
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    /** Non-destructive view for printing. */
    // ToList
    public List<T> toListSnapshot() {
        return new  ArrayList<>(elements);
    }

    //Shifting item up to correct place
    private void shiftUp(int index){
        while(index > 0){
            int parent = (index-1)/2;
            if(comparator.compare(elements.get(parent), elements.get(index)) <= 0){
                return;
            }
            swap(index,parent) ;
            index = parent;
        }
    }

    // Shiftting item down
    private void shiftDown(int index){
        int leftChild = index*2+1;
        int rightChild = index*2+2;
        if(leftChild >= elements.size()){
            return;
        }
        int largestChild = leftChild;

        if(rightChild < elements.size()){
            if(comparator.compare(elements.get(rightChild), elements.get(leftChild)) > 0){
                largestChild = rightChild;
            }
        }
        if(comparator.compare(elements.get(index), elements.get(largestChild)) >= 0){
            return;
        }
        swap(index, largestChild);
    }

    // Swap two elements
    private void swap(int Index1, int Index2) {
        T temp = elements.get(Index1);
        elements.set(Index1, elements.get(Index2));
        elements.set(Index2, temp);
    }
}