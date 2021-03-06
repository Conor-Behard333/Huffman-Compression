package Huffman;

import java.io.*;
import java.util.HashMap;

/**
 * Used to compress a file and save it to a location
 */
public class CompressedFile {

    /**
     * Instantiates a new Compressed file object.
     *
     * @param fileContents         the file dir of the file to be compressed
     * @param newFileDir           the file dir of the compressed file
     * @param encoder              the encoder
     * @param characterFrequencies the character frequencies
     */
    CompressedFile(String fileContents, String newFileDir, HashMap<Character, String> encoder, HashMap<Character, Integer> characterFrequencies) throws IOException {
        //turns data in file into 1's and 0's
        String compressedData = getCompressedData(fileContents, encoder);

        //adds the padding and tree structure to the compressed file
        int padding = addTreeStructureAndPaddingToFile(newFileDir, compressedData, characterFrequencies);

        //writes the compressed data as binary to a file
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
     * @param newFileDir           the file dir of the compressed file
     * @param compressedData       the compressed data
     * @param characterFrequencies f
     */
    private int addTreeStructureAndPaddingToFile(String newFileDir, String compressedData, HashMap<Character, Integer> characterFrequencies) throws IOException {
        int padding = 8 - (compressedData.length() % 8);
        if (compressedData.length() % 8 == 0) {
            padding = 0;
        }
        StringBuilder treeStructure = new StringBuilder();
        for (Character character : characterFrequencies.keySet()) {
            treeStructure.append((int) character).append(" ").append(characterFrequencies.get(character)).append(" ");
        }

        PrintWriter printLine = new PrintWriter(new FileWriter(newFileDir, false));//don't append to the file
        printLine.print(treeStructure.toString() + "\n");
        printLine.print(padding + "\n");

        printLine.close();
        return padding;
    }

    /**
     * Write the compressed data to file.
     *
     * @param compressedData the compressed data
     * @param fileDir        the file dir of the compressed file
     */
    private void writeBinaryDataToFile(String compressedData, String fileDir, int padding) throws IOException {
        compressedData = addPadding(compressedData, padding);

        byte[] data = new byte[compressedData.length() / 8];
        int index = 0;
        for (int i = 0; i < compressedData.length(); i += 8) {
            data[index] = (byte) Integer.parseInt(compressedData.substring(i, i + 8), 2);
            index++;
        }

        InputStream is = new ByteArrayInputStream(data);
        OutputStream os = new FileOutputStream(fileDir, true);
        while (is.read(data) != -1) {
            os.write(data);
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
