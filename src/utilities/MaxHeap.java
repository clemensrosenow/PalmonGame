package utilities;
import java.util.ArrayList;


/**
 * A custom Max <a href="https://de.wikipedia.org/wiki/Heap_(Datenstruktur)#Implementierung">Heap</a>.
 * This binary tree is represented as an Array, so no Node pointers are required.
 * Serves as a priority queue for storing and retrieving the highest damage moves.
 * Optimal runtime: O(log n) for insert and delete operations, getting the highest value with 0(1).
 * I'm a humble person, but this is really excellent :)
 */
public class MaxHeap {
    private final ArrayList<Node> nodes = new ArrayList<>();

    /**
     * Represents a node in the heap, containing a move's ID and its damage value.
     */
    private static class Node {
        int key; // the value used for heap ordering
        int value;
        //Pointers are not needed because the binary heap is represented in an Array

        /**
         * @param moveId the move's ID, used as the Node value
         * @param moveDamage the move's damage, used as the Node key
         */
        Node(int moveId, int moveDamage) {
            this.key = moveDamage;
            this.value = moveId;
        }
    }

    /**
     * Inserts a new move Node into the heap.
     *
     * @param moveId     the ID of the move
     * @param moveDamage the damage value of the move
     */
    public void insert(int moveId, int moveDamage) {
        nodes.add(new Node(moveId, moveDamage));
        // Restore the heap property from the bottom up
        heapifyUp(nodes.size() - 1);
    }

    /**
     * Restores the heap property by moving the node at the specified index up the heap.
     *
     * @param childIndex the index of the node to potentially move up
     */
    private void heapifyUp(int childIndex) {
        if (childIndex == 0) return; //Edge case: Child is Root

        int parentIndex = (childIndex - 1) / 2; // Works for both children because int gets floored

        Node parent = nodes.get(parentIndex);
        Node child = nodes.get(childIndex);

        // If the child key is greater than the parent key, swap the nodes
        if (child.key > parent.key) {
            nodes.set(parentIndex, child);
            nodes.set(childIndex, parent);

            heapifyUp(parentIndex); // Recursively move the child up
        }
    }

    /**
     * Removes and returns the move Node with the highest damage value from the heap.
     *
     * @return the ID of the move with the highest damage, or null if the heap is empty
     */
    public Integer delete() {
        if(nodes.isEmpty()) return null;
        int maxValue = nodes.getFirst().value; // Get the highest value node from the tree root

        Node lastNode = nodes.removeLast(); //Remove the last node
        if(nodes.isEmpty()) return maxValue; // Edge case: Last node is root

        nodes.set(0, lastNode); //Insert the last node at the root
        heapifyDown(0); // Restore the heap property from the root down

        return maxValue;
    }

    /**
     * Restores the heap property by moving the node at the specified index down the heap.
     *
     * @param parentIndex the index of the node to potentially move down
     */
    private void heapifyDown(int parentIndex) {
        // Get indices of the child nodes
        int leftChildIndex = 2 * parentIndex + 1;
        int rightChildIndex = 2 * parentIndex + 2;

        // Determine the largest node
        int largestIndex = parentIndex;
        // Check the children for out-of-bounds indices and compare them with the currently largest Node
        if(leftChildIndex < nodes.size() && nodes.get(leftChildIndex).key > nodes.get(largestIndex).key) {
            largestIndex = leftChildIndex;
        }
        if(rightChildIndex < nodes.size() && nodes.get(rightChildIndex).key > nodes.get(largestIndex).key) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex == parentIndex) return; // Early return when parent is the largest node

        //Swap parent node with the largest child node
        Node parent = nodes.get(parentIndex);
        Node largestChild = nodes.get(largestIndex);
        nodes.set(parentIndex, largestChild);
        nodes.set(largestIndex, parent);

        heapifyDown(largestIndex); // Recursively move the parent down
    }
}
