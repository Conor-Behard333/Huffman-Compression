package Huffman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The type Uncompress.
 */
public class UncompressFile {
    /**
     * Instantiates a new Uncompress.
     *
     * @param fileDir    the file dir
     * @param newFileDir the new file dir
     */
    UncompressFile(String fileDir, String newFileDir) {
        String[] treeAndPadding = getTreeCodeAndPadding(fileDir);
        String file = readBinaryDataFromFile(fileDir, treeAndPadding[0].length() + treeAndPadding[1].length());
        Node tree = createTree(treeAndPadding[0]);
        String decodedFile = decode(tree, file, Integer.parseInt(treeAndPadding[1]));
        saveFile(decodedFile, newFileDir);
    }

    /**
     * Get tree code and padding string [ ].
     *
     * @param fileDir the file dir
     * @return the string [ ]
     */
    private String[] getTreeCodeAndPadding(String fileDir) {
        String[] treeAndPadding = {"", ""};
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
     * Read binary data from file string.
     *
     * @param fileDir the file dir
     * @param offset  the offset
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
     * Create tree node.
     *
     * @param treeCode the tree code
     * @return the node
     */
    private Node createTree(String treeCode) {
        String[] treeCodeArr = treeCode.split(" ");
        String[][] leafNodesAndPaths = getLeafNodesAndPaths(treeCodeArr);

        Node tree = new Node(0, false);
        tree.setRoot(true);
        for (String[] leafNode : leafNodesAndPaths) {
            addNodeToTree(tree, leafNode[0], leafNode[1]);
        }
        return tree;
    }

    /**
     * Get leaf nodes and paths string [ ] [ ].
     *
     * @param treeCodeArr the tree code arr
     * @return the string [ ] [ ]
     */
    private String[][] getLeafNodesAndPaths(String[] treeCodeArr) {
        String[][] leafNodes = new String[treeCodeArr.length / 2][2];

        int index = 0;
        for (int i = 0; i < leafNodes.length; i++) {
            for (int j = 0; j < leafNodes[i].length; j++) {
                leafNodes[i][j] = treeCodeArr[index];
                index++;
            }
        }
        return leafNodes;
    }

    /**
     * Add node to tree.
     *
     * @param tree  the tree
     * @param path  the path
     * @param value the value
     */
    private void addNodeToTree(Node tree, String path, String value) {
        Node currentNode = tree;
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
     * Get file as byte array byte [ ].
     *
     * @param fileDir the file dir
     * @return the byte [ ]
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
     * Decode string.
     *
     * @param tree           the tree
     * @param compressedData the compressed data
     * @param padding        the padding
     * @return the string
     */
    private String decode(Node tree, String compressedData, int padding) {
        StringBuilder decodedData = new StringBuilder();
        Node currentNode = tree;
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
            currentNode = tree;
        }
        return decodedData.toString();
    }

    /**
     * Save file
     *
     * @param decompressedData the decompressed data
     * @param fileDir          the file dir
     */
    protected void saveFile(String decompressedData, String fileDir) {
        try {
            FileWriter write = new FileWriter(fileDir, false);
            try (PrintWriter printLine = new PrintWriter(write)) {
                printLine.print(decompressedData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
