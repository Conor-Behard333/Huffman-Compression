package Huffman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The type Compress.
 */
public class CompressFile {

    /**
     * Instantiates a new Compress.
     *
     * @param fileDir    the file dir
     * @param newFileDir the new file dir
     * @param encoder    the encoder
     * @param leafNodes  the leaf nodes
     */
    CompressFile(String fileDir, String newFileDir, HashMap<Character, String> encoder, ArrayList<Node> leafNodes){
        String fileContents = readFile(fileDir);
        String compressedData = getCompressedData(fileContents, encoder);
        addTreeCodeAndPaddingToFile(newFileDir, leafNodes, compressedData);
        writeBinaryDataToFile(compressedData, newFileDir);
    }

    /**
     * Read file string.
     *
     * @param fileDir the file dir
     * @return the string
     */
    /*
     * Reads a text file and stores it as a single string
     */
    private String readFile(String fileDir) {
        StringBuilder text = new StringBuilder();
        try {
            try (Scanner sc = new Scanner(new File(fileDir))) {
                while ((sc.hasNext())) {
                    text.append(sc.nextLine()).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString().trim();
    }

    /**
     * Gets compressed data.
     *
     * @param fileContents the file contents
     * @param encoder      the encoder
     * @return the compressed data
     */
    private String getCompressedData(String fileContents, HashMap<Character, String> encoder) {
        StringBuilder compressedData = new StringBuilder();
        for (int i = 0; i < fileContents.length(); i++) {
            if (encoder.get(fileContents.charAt(i)) != null) {
                compressedData.append(encoder.get(fileContents.charAt(i)));
            }
        }
        return compressedData.toString();
    }

    /**
     * Add tree code and padding to file.
     *
     * @param newFileDir     the new file dir
     * @param leafNodes      the leaf nodes
     * @param compressedData the compressed data
     */
    private void addTreeCodeAndPaddingToFile(String newFileDir, ArrayList<Node> leafNodes, String compressedData) {
        int padding = 8 - (compressedData.length() % 8);
        if (compressedData.length() % 8 == 0) {
            padding = 0;
        }

        try {
            FileWriter write = new FileWriter(newFileDir, false);
            try (PrintWriter printLine = new PrintWriter(write)) {
                printLine.print(getTreeCode(leafNodes) + "\n");
                printLine.print(padding + "\n");
            }
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets tree code.
     *
     * @param leafNodes the leaf nodes
     * @return the tree code
     */
    private String getTreeCode(ArrayList<Node> leafNodes) {
        StringBuilder treeCode = new StringBuilder();
        for (Node leaf : leafNodes) {
            treeCode.append(getPath(leaf)).append(" ").append((int) leaf.getValue()).append(" ");
        }
        return treeCode.toString();
    }

    /**
     * Gets path.
     *
     * @param node the node
     * @return the path
     */
    protected String getPath(Node node) {
        StringBuilder path = new StringBuilder(findPath(node)).reverse();
        return path.toString();
    }

    /**
     * Find path string.
     *
     * @param node      the node
     * @param prev_node the prev node
     * @return the string
     */
    private String findPath(Node node, Node... prev_node) {
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
     * Write binary data to file.
     *
     * @param compressedData the compressed data
     * @param fileDir        the file dir
     */
    private void writeBinaryDataToFile(String compressedData, String fileDir) {
        try {
            File compressedFile = new File(fileDir);
            ByteOutputStream outputStream = new ByteOutputStream(compressedFile);

            byte[] bits = new byte[8];
            int index = 0;
            for (int i = 0; i < compressedData.length(); i++) {
                if (index == 8) {
                    outputStream.write(bits);
                    bits = new byte[8];
                    index = 0;
                }
                if (compressedData.charAt(i) == '1') {
                    bits[index] = 1;
                } else if (compressedData.charAt(i) == '0') {
                    bits[index] = 0;
                }
                index++;
            }
            outputStream.write(bits);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
