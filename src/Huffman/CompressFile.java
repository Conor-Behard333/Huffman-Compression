package Huffman;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

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
        String fileContents = BinaryFile.readFile(fileDir);
        String compressedData = getCompressedData(fileContents, encoder);
        addTreeCodeAndPaddingToFile(newFileDir, leafNodes, compressedData);
        writeBinaryDataToFile(compressedData, newFileDir);
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
            treeCode.append(HuffmanTree.getPath(leaf)).append(" ").append((int) leaf.getValue()).append(" ");
        }
        return treeCode.toString();
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
            BinaryFile outputStream = new BinaryFile(compressedFile);

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
