package Huffman;

import java.io.*;
import java.util.Scanner;

/**
 * The type Byte output stream.
 */
public class BinaryFile extends FileOutputStream {
    /**
     * The Buffer.
     */
    byte buffer;
    /**
     * The Pos.
     */
    byte pos;

    /**
     * Instantiates a new Byte output stream.
     *
     * @param file the file
     * @throws FileNotFoundException the file not found exception
     */
    public BinaryFile(File file) throws FileNotFoundException {
        super(file, true);
        buffer = 0x00;
    }

    /**
     * Write.
     *
     * @param bit the bit
     * @throws IOException the io exception
     */
    public void write(byte bit) throws IOException {
        buffer = (byte) (buffer << 1);
        buffer |= bit;

        pos++;

        if (pos == 8)
            flushByte();
    }

    /**
     * Write.
     *
     * @param bits the bits
     * @throws IOException the io exception
     */
    public void write(byte[] bits) throws IOException {
        for (byte bit : bits)
            write(bit);
    }

    /**
     * Close.
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
     * Flush byte.
     *
     * @throws IOException the io exception
     */
    private void flushByte() throws IOException {
        super.write(buffer);
        buffer = 0x00;
        pos = 0;
    }

    /**
     * Read file string.
     *
     * @param fileDir the file dir
     * @return the string
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