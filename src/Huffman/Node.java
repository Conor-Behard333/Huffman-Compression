package Huffman;

/**
 * Used to store the data of a single node in the binary tree
 */
public class Node {
    private boolean root = false;
    private final boolean leafNode;
    private final int frequency;
    private Character value;
    private Node child_left = null;
    private Node child_right = null;
    private Node parent = null;

    /**
     * Instantiates a new Node.
     *
     * @param frequency the frequency of the character
     * @param leafNode  the leaf node
     */
    public Node(int frequency, boolean leafNode) {
        this.frequency = frequency;
        this.leafNode = leafNode;
    }

    /**
     * Gets frequency.
     *
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Gets left child node.
     *
     * @return the child left
     */
    public Node getChild_left() {
        return child_left;
    }

    /**
     * Gets right child node.
     *
     * @return the child right
     */
    public Node getChild_right() {
        return child_right;
    }

    /**
     * Gets parent node.
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
     * Checks to see if this node is a root node
     *
     * @return true if root node, false if not
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Checks to see if this node is a leaf node
     *
     * @return true if leaf node, false if not
     */
    public boolean isLeafNode() {
        return leafNode;
    }

    /**
     * Sets the left child node.
     *
     * @param child_left the child left
     */

    public void setChild_left(Node child_left) {
        this.child_left = child_left;
    }

    /**
     * Sets the right child node.
     *
     * @param child_right the child right
     */
    public void setChild_right(Node child_right) {
        this.child_right = child_right;
    }

    /**
     * Sets parent node.
     *
     * @param parent the parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Sets character value.
     *
     * @param value the value
     */
    public void setValue(Character value) {
        this.value = value;
    }

    /**
     * Sets root node.
     *
     * @param root the root
     */
    public void setRoot(boolean root) {
        this.root = root;
    }

}
