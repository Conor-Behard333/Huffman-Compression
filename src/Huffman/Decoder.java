package Huffman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Decoder {

    public static void decompress(String fileDir, String newFileDir, String outputFileName) throws IOException {
        newFileDir += "\\" + outputFileName + "_uncompressed.txt";
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
    private static String[] getTreeStructureAndPadding(String fileDir) {
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
    private static String readBinaryDataFromFile(String fileDir, int offset) {
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
    private static Node createTree(String treeStructure) {
        HashMap<Character, Integer> characterFrequencies = new HashMap<>();
        String[] data = treeStructure.split(" ");
        for (int i = 0; i < data.length; i += 2) {
            characterFrequencies.put((char) Integer.parseInt(data[i]), Integer.parseInt(data[i + 1]));
        }

        //get the leaf nodes of the tree which can be used to traverse it
        ArrayList<Node> tree = HuffmanTree.getLeafNodes(characterFrequencies);

        //fills the tree using the character frequencies
        HuffmanTree.fillTree(tree);

        return tree.get(0);
    }

    /**
     * Get file as byte array
     *
     * @param fileDir the file dir for the compressed file
     * @return the byte array of the compressed file
     */
    private static byte[] getFileAsByteArray(String fileDir) {
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
    private static String decode(Node rootNode, String compressedData, int padding) {
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
    protected static void saveFile(String uncompressedData, String fileDir) throws IOException {
        FileWriter fw = new FileWriter(fileDir);
        fw.write(uncompressedData);
        fw.close();
    }
}
