package Huffman;

import java.util.*;

/**
 * Creates a Huffman tree based on input data.
 * Options to compress and uncompress data
 */
public class HuffmanTree {

    /**
     * Compress a text file.
     *
     * @param fileDir        the dir of the file
     * @param newFileDir     the dir of the new compressed file
     * @param outputFileName the name of the compressed file
     */
    public void compress(String fileDir, String newFileDir, String outputFileName) {
        String fileContents = BinaryFile.readFile(fileDir);

        //create the leaf nodes for the given data in the file
        ArrayList<Node> leafNodes = getTree(fileContents);

        //create an encoder to compress the data
        HashMap<Character, String> encoder = getEncoder(leafNodes);

        newFileDir += "\\" + outputFileName + "_compressed.txt";
        new CompressedFile(fileContents, newFileDir, encoder, leafNodes);
    }

    /**
     * uncompress a text file.
     *
     * @param fileDir        the dir of the compressed file
     * @param newFileDir     the dir of the new uncompressed file
     * @param outputFileName the name of the uncompressed file
     */
    public void uncompress(String fileDir, String newFileDir, String outputFileName) {
        newFileDir += "\\" + outputFileName + "_uncompressed.txt";
        new UncompressedFile(fileDir, newFileDir);
    }

    /**
     * Fills the tree with the data.
     *
     * @param fileContents the file dir
     * @return the leaf nodes of the tree
     */
    private ArrayList<Node> getTree(String fileContents) {
        //gets the character frequencies
        HashMap<Character, Integer> characterFrequencies = getCharFrequencies(fileContents);

        //get the leaf nodes of the tree which can be used to traverse it
        ArrayList<Node> tree = getLeafNodes(characterFrequencies);

        //stores just the leaf nodes
        ArrayList<Node> leafNodes = new ArrayList<>(tree);

        //fills the tree using the character frequencies
        fillTree(tree);

        return leafNodes;
    }

    /**
     * Creates the encoder.
     *
     * @param leafNodes the leaf nodes
     * @return the encoder
     */
    private HashMap<Character, String> getEncoder(ArrayList<Node> leafNodes) {
        HashMap<Character, String> encoder = new HashMap<>();

        //dictionary where the key is the path and the value is the character of the respected leaf node
        for (Node leafNode : leafNodes) {
            String path = getPath(leafNode);
            Character value = leafNode.getValue();
            encoder.put(value, path);
        }

        return encoder;
    }


    /**
     * Creates a dictionary where the key is the character and the value is how often that key appears in the text.
     *
     * @param fileContents the file dir
     * @return the character frequencies
     */
    private HashMap<Character, Integer> getCharFrequencies(String fileContents) {
        HashMap<Character, Integer> characterFrequencies = new HashMap<>();

        for (int i = 0; i < fileContents.length(); i++) {
            char character = fileContents.charAt(i);
            if (characterFrequencies.containsKey(character)) {
                characterFrequencies.put(character, characterFrequencies.get(character) + 1);
            } else {
                characterFrequencies.put(character, 1);
            }
        }
        return characterFrequencies;
    }

    /**
     * create the leaf nodes for the tree.
     *
     * @param characterFrequencies the character frequencies
     * @return the leaf nodes
     */
    private ArrayList<Node> getLeafNodes(HashMap<Character, Integer> characterFrequencies) {
        ArrayList<Node> tree = new ArrayList<>();
        final int[] index = {0};
        characterFrequencies.forEach((key, value) -> {
            tree.add(new Node(value, true));
            tree.get(index[0]).setValue(key);
            index[0]++;
        });
        return tree;
    }

    /**
     * Find the path from a leaf node to the root node.
     *
     * @param node      the node
     * @param prev_node the prev node
     * @return the string
     */
    private static String findPath(Node node, Node... prev_node) {
        String str = "";
        if (prev_node.length > 0) {
            if (node.getChild_left().equals(prev_node[0])) {
                str += "0";
            } else {
                str += "1";
            }
        }

        if (!node.isRoot()) {
            str += findPath(node.getParent(), node);
        }
        return str;
    }

    /**
     * Return the reverse of the path from a leaf node to a root node.
     * The reverse is simply the path from the root node to a leaf node
     *
     * @param node the node
     * @return the path
     */
    protected static String getPath(Node node) {
        StringBuilder path = new StringBuilder(findPath(node)).reverse();
        return path.toString();
    }

    /**
     * Create a filled tree
     *
     * Tree starts as a list of leaf nodes
     * The list is then sorted by frequency from smallest to biggest
     * Two child nodes are created which are the first 2 nodes in the list
     * The two child nodes are removed from the list of leaf nodes
     * A parent node is created and added to the tree
     *
     * This is repeated until there is only 1 node left which is the root node
     *
     * @param tree the list of leaf nodes
     */
    private void fillTree(ArrayList<Node> tree) {
        while (tree.size() > 1) {
            Collections.sort(tree);
            Node childLeft = tree.get(0);
            Node childRight = tree.get(1);
            Node parent = createNode(childLeft, childRight);
            tree.remove(0);
            tree.remove(0);
            tree.add(parent);
        }
        tree.get(0).setRoot(true);
    }

    /**
     * Create a parent node with two child nodes.
     *
     * @param childLeft  the left child
     * @param childRight the right child
     * @return the parent node
     */
    private Node createNode(Node childLeft, Node childRight) {
        Node node = new Node(childLeft.getFrequency() + childRight.getFrequency(), false);
        node.setChild_left(childLeft);
        node.setChild_right(childRight);
        childLeft.setParent(node);
        childRight.setParent(node);
        node.setRoot(false);
        return node;
    }
}
