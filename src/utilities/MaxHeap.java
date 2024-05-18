package utilities;

import java.util.ArrayList;

//Usage: Return the highest damage move
public class MaxHeap {
    private final ArrayList<Node> nodes = new ArrayList<>();

    private static class Node {
        int key;
        int value;
        Node(int moveId, int moveDamage) {
            this.key = moveDamage;
            this.value = moveId;
        }
    }

    public void insert(int moveId, int moveDamage) {
        nodes.add(new Node(moveId, moveDamage));
        heapifyUp(nodes.size() - 1);
    }

    private void heapifyUp(int childIndex) {
        if (childIndex == 0) return; //Edge case: Child is Root

        int parentIndex = (childIndex - 1) / 2; //Works for both children because int is floored

        Node parent = nodes.get(parentIndex);
        Node child = nodes.get(childIndex);
        if (child.key > parent.key) {
            nodes.set(parentIndex, child);
            nodes.set(childIndex, parent);
            heapifyUp(parentIndex);
        }
    }

    private void heapifyDown(int parentIndex) {
        int largestIndex = parentIndex;
        int leftChildIndex = 2 * parentIndex + 1;
        int rightChildIndex = 2 * parentIndex + 2;

        if(leftChildIndex >= nodes.size()) return;
        if(nodes.get(leftChildIndex).key > nodes.get(largestIndex).key) {
            largestIndex = leftChildIndex;
        }

        if(rightChildIndex >= nodes.size()) return;
        if(nodes.get(rightChildIndex).key > nodes.get(largestIndex).key) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex == parentIndex) return; // Edge case: Parent has stayed the largest node
        nodes.set(parentIndex, nodes.get(largestIndex));
        nodes.set(largestIndex, nodes.get(parentIndex));
        heapifyDown(largestIndex);
    }

    //Returns max value of the heap and maintains right structure
    public Integer delete() {
        if(nodes.isEmpty()) return null;
        int maxValue = nodes.removeFirst().value;
        if (!nodes.isEmpty()) {
            nodes.addFirst(nodes.removeLast());
            heapifyDown(0);
        }
        return maxValue;
    }
}
