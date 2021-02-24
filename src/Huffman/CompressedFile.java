package Huffman;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to compress a file and save it to a location
 */
public class CompressedFile {

    /**
     * Instantiates a new Compress.
     *
     * @param fileContents the file dir of the file to be compressed
     * @param newFileDir   the file dir of the compressed file
     * @param encoder      the encoder
     * @param leafNodes    the leaf nodes of the Huffman tree
     */
    CompressedFile(String fileContents, String newFileDir, HashMap<Character, String> encoder, ArrayList<Node> leafNodes) {
        String compressedData = getCompressedData(fileContents, encoder);
        int padding = addTreeStructureAndPaddingToFile(newFileDir, leafNodes, compressedData);
        writeBinaryDataToFile(compressedData, newFileDir, padding);
    }


    /**
     * Compresses the data using the encoder
     *
     * @param fileContents the contents of the file
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
     * Adds the tree structure of the Huffman tree and
     * how much padding has been used when compressing the data.
     *
     * @param newFileDir     the file dir of the compressed file
     * @param leafNodes      the leaf nodes of the Huffman tree
     * @param compressedData the compressed data
     */
    private int addTreeStructureAndPaddingToFile(String newFileDir, ArrayList<Node> leafNodes, String compressedData) {
        int padding = 8 - (compressedData.length() % 8);
        if (compressedData.length() % 8 == 0) {
            padding = 0;
        }

        try {
            FileWriter write = new FileWriter(newFileDir, false);//don't append to the file
            try (PrintWriter printLine = new PrintWriter(write)) {
                printLine.print(getTreeStructure(leafNodes) + "\n");
                printLine.print(padding + "\n");
            }
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return padding;
    }

    /**
     * Gets the structure of the Huffman tree.
     * <p>
     * The structure of the tree is represented as the path to a leaf node and the character for that leaf node
     * For example:
     * 01011 A
     * means to get to the leaf node A go left, right, left, right, right
     *
     * @param leafNodes the leaf nodes
     * @return the structure of the Huffman tree as a string
     */
    private String getTreeStructure(ArrayList<Node> leafNodes) {
        StringBuilder treeCode = new StringBuilder();
        for (Node leaf : leafNodes) {
            treeCode.append(HuffmanTree.getPath(leaf)).append(" ").append((int) leaf.getValue()).append(" ");
        }
        return treeCode.toString();
    }

    /**
     * Write the compressed data to file.
     *
     * @param compressedData the compressed data
     * @param fileDir        the file dir of the compressed file
     */
    private void writeBinaryDataToFile(String compressedData, String fileDir, int padding) {
        compressedData = addPadding(compressedData, padding);

        byte[] data = new byte[compressedData.length() / 8];
        int index = 0;
        for (int i = 0; i < compressedData.length(); i += 8) {
            data[index] = (byte) Integer.parseInt(compressedData.substring(i, i + 8), 2);
            index++;
        }

        try {
            InputStream is = new ByteArrayInputStream(data);
            OutputStream os = new FileOutputStream(fileDir, true);

            while (is.read(data) != -1) {
                os.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds padding to the compressed data
     *
     * @param compressedData the compressed data
     * @param padding        the amount of padding needed
     * @return the compressed data with padding
     */
    private String addPadding(String compressedData, int padding) {
        StringBuilder compressedDataBuilder = new StringBuilder(compressedData);
        for (int i = 0; i < padding; i++) {
            compressedDataBuilder.append("0");
        }
        compressedData = compressedDataBuilder.toString();
        return compressedData;
    }

}
