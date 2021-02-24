package Huffman;

import java.io.*;
import java.util.Scanner;

/**
 * Used to create and read files
 */
public class BinaryFile extends FileOutputStream {
    private byte buffer;
    private byte pos;

    /**
     * Instantiates a new BinaryFile object.
     *
     * @param file the file
     * @throws FileNotFoundException the file not found exception
     */
    public BinaryFile(File file) throws FileNotFoundException {
        super(file, true);
        buffer = 0x00;
    }

    /**
     * Write an individual bit to the buffer.
     * If the buffer has 8 bits then write the buffer to the file and reset it
     *
     * @param bit the bit to write to the file
     * @throws IOException the io exception
     */
    public void write(byte bit) throws IOException {
        buffer = (byte) (buffer << 1);
        buffer |= bit;
        pos++;
        if (pos == 8){
            flushByte();
        }
    }

    /**
     * Write all the bits to the file.
     *
     * @param bits a byte array of bits
     * @throws IOException the io exception
     */
    public void write(byte[] bits) throws IOException {
        for (byte bit : bits)
            write(bit);
    }

    /**
     * Closes the output stream.
     *
     * @throws IOException the io exception
     */
    public void close() throws IOException {
        if (pos != 0) {
            buffer = (byte) (buffer << (8 - pos));
            flushByte();
        }
        super.close();
    }

    /**
     * Reset the buffer.
     *
     * @throws IOException the io exception
     */
    private void flushByte() throws IOException {
        super.write(buffer);
        buffer = 0x00;
        pos = 0;
    }

    /**
     * Read a file.
     *
     * @param fileDir the file dir
     * @return the contents of the file as a string
     */
    public static String readFile(String fileDir) {
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
}