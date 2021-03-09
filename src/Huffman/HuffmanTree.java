package Huffman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Creates a Huffman tree based on input data.
 * Options to compress and uncompress data
 */
public class HuffmanTree {
    private String fileContents;
    private HashMap<Character, String> codes;
    private HashMap<Character, Integer> characterFrequencies;

    public HuffmanTree(String fileDir) {
        fileContents = readFile(fileDir);

        //create the leaf nodes for the given data in the file
        ArrayList<Node> leafNodes = getTree(fileContents);
        //create an encoder to compress the data
        codes = getEncoder(leafNodes);

        characterFrequencies = getCharFrequencies(fileContents);
        //todo Added
//        if (true) {// save the encoding for this file
//            FileOutputStream serializer = new FileOutputStream("encoder.ser");
//            ObjectOutputStream out = new ObjectOutputStream(serializer);
//            out.writeObject(encoder);
//            out.close();
//            serializer.close();
//
//            FileOutputStream serializer2 = new FileOutputStream("characterFrequencies.ser");
//            ObjectOutputStream out2 = new ObjectOutputStream(serializer2);
//            out2.writeObject(characterFrequencies);
//            out2.close();
//            serializer2.close();
//        } else { //use the encoding of another file
//            FileInputStream fileIn = new FileInputStream("encoder.ser");
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            encoder = (HashMap<Character, String>) in.readObject();
//            in.close();
//            fileIn.close();
//
//            FileInputStream fileIn2 = new FileInputStream("characterFrequencies.ser");
//            ObjectInputStream in2 = new ObjectInputStream(fileIn2);
//            characterFrequencies = (HashMap<Character, Integer>) in2.readObject();
//            in2.close();
//            fileIn2.close();
//        }
    }

    public String getFileContents() {
        return fileContents;
    }

    public HashMap<Character, String> getCodes() {
        return codes;
    }

    public HashMap<Character, Integer> getCharacterFrequencies() {
        return characterFrequencies;
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
        // This is done so that any unknown characters will be represented using an underscore
        if (!characterFrequencies.containsKey('_')) {
            characterFrequencies.put('_', 0);
        }
        return characterFrequencies;
    }

    /**
     * create the leaf nodes for the tree.
     *
     * @param characterFrequencies the character frequencies
     * @return the leaf nodes
     */
    public static ArrayList<Node> getLeafNodes(HashMap<Character, Integer> characterFrequencies) {
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
     * <p>
     * Tree starts as a list of leaf nodes
     * The list is then sorted by frequency from smallest to biggest
     * Two child nodes are created which are the first 2 nodes in the list
     * The two child nodes are removed from the list of leaf nodes
     * A parent node is created and added to the tree
     * <p>
     * This is repeated until there is only 1 node left which is the root node
     *
     * @param tree the list of leaf nodes
     */
    public static void fillTree(ArrayList<Node> tree) {
        while (tree.size() > 1) {
            insertionSort(tree);
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
     * Sorts the list of leaf nodes by frequency from smallest to biggest
     * using insertion sort.
     *
     * @param tree list of leaf nodes in the tree
     */
    public static void insertionSort(ArrayList<Node> tree) {
        int pos = tree.size() - 1;
        while (pos > 0) {
            int indexNew = pos - 1;
            Node n = tree.get(indexNew);
            int valueNew = tree.get(indexNew).getFrequency();
            while ((indexNew < tree.size() - 1) && (valueNew > tree.get(indexNew + 1).getFrequency())) {
                tree.set(indexNew, tree.get(indexNew + 1));
                indexNew++;
            }
            tree.set(indexNew, n);
            pos--;
        }
    }

    /**
     * Create a parent node with two child nodes.
     *
     * @param childLeft  the left child
     * @param childRight the right child
     * @return the parent node
     */
    private static Node createNode(Node childLeft, Node childRight) {
        Node node = new Node(childLeft.getFrequency() + childRight.getFrequency(), false);
        node.setChild_left(childLeft);
        node.setChild_right(childRight);
        childLeft.setParent(node);
        childRight.setParent(node);
        node.setRoot(false);
        return node;
    }

    /**
     * Read a file.
     *
     * @param fileDir the file dir
     * @return the contents of the file as a string
     */
    private String readFile(String fileDir) {
        StringBuilder text = new StringBuilder();
        try {
            String line;
            BufferedReader r = new BufferedReader(new FileReader(fileDir));
            while ((line = r.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString().trim();
    }
}
