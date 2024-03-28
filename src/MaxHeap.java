import java.util.ArrayList;
import java.util.Map;

public class MaxHeap {
    private final ArrayList<Node> nodes = new ArrayList<>();

    public static class Node {
        int key;
        int value;
        Node(int moveId, int moveDamage) {
            this.key = moveDamage;
            this.value = moveId;
        }
    }

    public void insert(Node node) {
        nodes.add(node);
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

    private void heapifyDown(int index) {
        int largestIndex = index;
        int leftChildIndex = 2 * index + 1;
        int rightChildIndex = 2 * index + 1;

        if(leftChildIndex >= nodes.size()) return;
        if(nodes.get(leftChildIndex).key > nodes.get(largestIndex).key) {
            largestIndex = leftChildIndex;
        }

        if(rightChildIndex >= nodes.size()) return;
        if(nodes.get(rightChildIndex).key > nodes.get(largestIndex).key) {
            largestIndex = rightChildIndex;
        }

        if (largestIndex == index) return; // Edge case: Parent has stayed the largest node
        Node parent = nodes.get(index);
        nodes.set(index, nodes.get(largestIndex));
        nodes.set(largestIndex, parent);
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
