package Huffman;

/**
 * The type Node.
 */
public class Node implements Comparable<Node> {
    /**
     * The Root.
     */
    private boolean root = false;
    /**
     * The Leaf node.
     */
    private boolean leafNode;
    /**
     * The Frequency.
     */
    private int frequency;
    /**
     * The Child left.
     */
    private Node child_left = null;
    /**
     * The Child right.
     */
    private Node child_right = null;
    /**
     * The Parent.
     */
    private Node parent = null;
    /**
     * The Value.
     */
    private Character value;

    /**
     * Instantiates a new Node.
     *
     * @param frequency the frequency
     * @param leafNode  the leaf node
     */
    Node(int frequency, boolean leafNode) {
        this.frequency = frequency;
        this.leafNode = leafNode;
    }

    /**
     * Compare to int.
     *
     * @param other the other
     * @return the int
     */
    @Override
    public int compareTo(Node other) {
        //when sorting an array of nodes it will order them by frequency from smallest to biggest
        return Integer.compare(this.frequency, other.getFrequency());
    }

    /**
     * Gets frequency.
     *
     * @return the frequency
     */
// Getters
    public int getFrequency() {
        return frequency;
    }

    /**
     * Gets child left.
     *
     * @return the child left
     */
    public Node getChild_left() {
        return child_left;
    }

    /**
     * Gets child right.
     *
     * @return the child right
     */
    public Node getChild_right() {
        return child_right;
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Character getValue() {
        return value;
    }

    /**
     * Is root boolean.
     *
     * @return the boolean
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Is leaf node boolean.
     *
     * @return the boolean
     */
    public boolean isLeafNode() {
        return leafNode;
    }

    /**
     * Sets child left.
     *
     * @param child_left the child left
     */
//Setters
    public void setChild_left(Node child_left) {
        this.child_left = child_left;
    }

    /**
     * Sets child right.
     *
     * @param child_right the child right
     */
    public void setChild_right(Node child_right) {
        this.child_right = child_right;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(Character value) {
        this.value = value;
    }

    /**
     * Sets root.
     *
     * @param root the root
     */
    public void setRoot(boolean root) {
        this.root = root;
    }
}
