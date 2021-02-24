package Huffman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Used to uncompress a file and save it to a location
 */
public class UncompressedFile {
    /**
     * Instantiates a new UncompressedFile.
     *
     * @param fileDir    the compressed file dir
     * @param newFileDir the file dir for the uncompressed file
     */
    UncompressedFile(String fileDir, String newFileDir) {
        //get the tree structure and the padding stored in the file
        String[] treeAndPadding = getTreeStructureAndPadding(fileDir);

        //read the binary data (compressed data) and store in a string
        String file = readBinaryDataFromFile(fileDir, treeAndPadding[0].length() + treeAndPadding[1].length());

        //Re-create the tree given the tree structure and padding
        Node tree = createTree(treeAndPadding[0]);

        //decode the compressed data using the tree
        String decodedFile = decode(tree, file, Integer.parseInt(treeAndPadding[1]));

        //save the uncompressed file
        saveFile(decodedFile, newFileDir);
    }

    /**
     * Get tree structure and padding from the compressed file.
     *
     * @param fileDir the file dir of the compressed file
     * @return the tree structure and the padding
     */
    private String[] getTreeStructureAndPadding(String fileDir) {
        String[] treeAndPadding = new String[2];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileDir));
            treeAndPadding[0] = reader.readLine();
            treeAndPadding[1] = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return treeAndPadding;
    }

    /**
     * Read binary data from the compressed file.
     *
     * @param fileDir the file dir of the compressed file
     * @param offset  the offset of bits due to the padding
     * @return the string
     */
    private String readBinaryDataFromFile(String fileDir, int offset) {
        StringBuilder bits = new StringBuilder();
        byte[] array = getFileAsByteArray(fileDir);
        assert array != null;
        for (int i = offset + 2; i < array.length; i++) {
            byte b = array[i];
            int result = b & 0xff;
            String binary = Integer.toBinaryString(result);

            if (binary.length() != 8) {
                StringBuilder fullByte = new StringBuilder();
                for (int j = 0; j < 8 - binary.length(); j++) {
                    fullByte.append("0");
                }
                fullByte.append(binary);
                bits.append(fullByte);
            } else {
                bits.append(binary);
            }
        }
        return bits.toString();
    }

    /**
     * Create a tree from a tree structure.
     *
     * @param treeStructure the structure of the tree
     * @return the root node of the tree
     */
    private Node createTree(String treeStructure) {
        String[] treeCodeArr = treeStructure.split(" ");
        String[][] leafNodesAndPaths = getLeafNodesAndPaths(treeCodeArr);

        Node rootNode = new Node(0, false);
        rootNode.setRoot(true);
        for (String[] leafNode : leafNodesAndPaths) {
            addNodeToTree(rootNode, leafNode[0], leafNode[1]);
        }
        return rootNode;
    }

    /**
     * Store the leaf node value as one element and the path to that node as another
     *
     * @param treeStructureArr the tree structure as a String array
     * @return the leaf nodes and path
     */
    private String[][] getLeafNodesAndPaths(String[] treeStructureArr) {
        String[][] leafNodes = new String[treeStructureArr.length / 2][2];

        int index = 0;
        for (int i = 0; i < leafNodes.length; i++) {
            for (int j = 0; j < leafNodes[i].length; j++) {
                leafNodes[i][j] = treeStructureArr[index];
                index++;
            }
        }
        return leafNodes;
    }

    /**
     * Add the node to rootNode.
     *
     * @param rootNode the rootNode
     * @param path     the path to the root node
     * @param value    the value of the node
     */
    private void addNodeToTree(Node rootNode, String path, String value) {
        Node currentNode = rootNode;
        for (int i = 0; i < path.length(); i++) {
            char direction = path.charAt(i);
            if (direction == '0') {
                if (currentNode.getChild_left() == null) {
                    if (i == path.length() - 1) {
                        currentNode.setChild_left(new Node(0, true));
                        currentNode.getChild_left().setValue((char) Integer.parseInt(value));
                    } else {
                        currentNode.setChild_left(new Node(0, false));
                    }
                }
                currentNode = currentNode.getChild_left();
            } else if (direction == '1') {
                if (currentNode.getChild_right() == null) {
                    if (i == path.length() - 1) {
                        currentNode.setChild_right(new Node(0, true));
                        currentNode.getChild_right().setValue((char) Integer.parseInt(value));
                    } else {
                        currentNode.setChild_right(new Node(0, false));
                    }
                }
                currentNode = currentNode.getChild_right();
            }
        }
    }

    /**
     * Get file as byte array
     *
     * @param fileDir the file dir for the compressed file
     * @return the byte array of the compressed file
     */
    private byte[] getFileAsByteArray(String fileDir) {
        byte[] array = null;
        try {
            array = Files.readAllBytes(Paths.get(fileDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * Decode the compressed data using the built root node.
     *
     * @param rootNode       the root node
     * @param compressedData the compressed data
     * @param padding        the padding of bits
     * @return the decoded string
     */
    private String decode(Node rootNode, String compressedData, int padding) {
        StringBuilder decodedData = new StringBuilder();
        Node currentNode = rootNode;
        int index = 0;
        while (index < compressedData.length() - padding) {
            while (!currentNode.isLeafNode()) {
                if (compressedData.charAt(index) == '1') {
                    currentNode = currentNode.getChild_right();
                } else if (compressedData.charAt(index) == '0') {
                    currentNode = currentNode.getChild_left();
                }
                index++;
            }
            decodedData.append(currentNode.getValue());
            currentNode = rootNode;
        }
        return decodedData.toString();
    }

    /**
     * Save the decoded file.
     *
     * @param uncompressedData the uncompressed data
     * @param fileDir          the file dir
     */
    protected void saveFile(String uncompressedData, String fileDir) {
        try {
            FileWriter write = new FileWriter(fileDir, false);
            try (PrintWriter printLine = new PrintWriter(write)) {
                printLine.print(uncompressedData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
