package Huffman;

import java.util.*;

/**
 * The type Huffman tree.
 */
public class HuffmanTree {

    /**
     * Compress.
     *
     * @param fileDir        the file dir
     * @param newFileDir     the new file dir
     * @param outputFileName the output file name
     */
    public void compress(String fileDir, String newFileDir, String outputFileName) {
        ArrayList<Node> leafNodes = getTree(fileDir);
        HashMap<Character, String> encoder = getEncoder(leafNodes);
        newFileDir += "\\" + outputFileName;
        new CompressFile(fileDir, newFileDir, encoder, leafNodes);
    }

    /**
     * Uncompress.
     *
     * @param fileDir        the file dir
     * @param newFileDir     the new file dir
     * @param outputFileName the output file name
     */
    public void uncompress(String fileDir, String newFileDir, String outputFileName) {
        newFileDir += "\\" + outputFileName;
        new UncompressFile(fileDir, newFileDir);
    }

    /**
     * Gets tree.
     *
     * @param fileDir the file dir
     * @return the tree
     */
    private ArrayList<Node> getTree(String fileDir) {
        HashMap<Character, Integer> wordFrequencies = getWordFrequencies(fileDir);

        ArrayList<Node> tree = getLeafNodes(wordFrequencies);
        ArrayList<Node> leafNodes = new ArrayList<>(tree);

        fillTree(tree);

        return leafNodes;
    }

    /**
     * Gets encoder.
     *
     * @param leafNodes the leaf nodes
     * @return the encoder
     */
    private HashMap<Character, String> getEncoder(ArrayList<Node> leafNodes) {
        HashMap<Character, String> encoder = new HashMap<>();

        for (Node leafNode : leafNodes) {
            String path = getPath(leafNode);
            Character value = leafNode.getValue();
            encoder.put(value, path);
        }

        return encoder;
    }


    /**
     * Gets word frequencies.
     *
     * @param fileDir the file dir
     * @return the word frequencies
     */
    private HashMap<Character, Integer> getWordFrequencies(String fileDir) {
        String file = BinaryFile.readFile(fileDir);
        HashMap<Character, Integer> wordFrequencies = new HashMap<>();

        for (int i = 0; i < file.length(); i++) {
            char character = file.charAt(i);
            if (wordFrequencies.containsKey(character)) {
                wordFrequencies.put(character, wordFrequencies.get(character) + 1);
            } else {
                wordFrequencies.put(character, 1);
            }
        }
        return wordFrequencies;
    }

    /**
     * Gets leaf nodes.
     *
     * @param wordFrequencies the word frequencies
     * @return the leaf nodes
     */
    private ArrayList<Node> getLeafNodes(HashMap<Character, Integer> wordFrequencies) {
        ArrayList<Node> tree = new ArrayList<>();
        final int[] index = {0};
        wordFrequencies.forEach((key, value) -> {
            tree.add(new Node(value, true));
            tree.get(index[0]).setValue(key);
            index[0]++;
        });
        return tree;
    }

    /**
     * Find path string.
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
     * Gets path.
     *
     * @param node the node
     * @return the path
     */
    protected static String getPath(Node node) {
        StringBuilder path = new StringBuilder(findPath(node)).reverse();
        return path.toString();
    }

    /**
     * Fill tree.
     *
     * @param tree the tree
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
     * Create node node.
     *
     * @param childLeft  the child left
     * @param childRight the child right
     * @return the node
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
