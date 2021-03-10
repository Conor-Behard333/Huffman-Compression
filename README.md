# Huffman-Compression

## How to run the program:

Download and run the jar file named 'CA1.jar' in out/artifacts/CA1_jar

If for whatever reason it does not run do this instead:
In the terminal go to the submission folder where the src folder is located and run

```
java -classpath out/production/CA1 HomeUI
```

## How to use the program:

### Compress:

To compress a file, click on the compress button given when the program first runs.

Then click the 'select txt file' button. This will allow you to choose the text file that you want to compress.

Next click the 'select output directory' button. This will allow you to choose where you want the compressed file to be
written to. The initial directory shown when you click the button is the directory of the text file that you chose to
compress.

Then click compress. If everything goes right then a message will be displayed showing where the file was outputted to (
the directory you chose). If there is an error, you will be shown an error message.

You can use the 'back' button to go back to the options to either compress or uncompress.

### Uncompress:

The method is the same to uncompress a file just make sure that the file you select is the compressed version of the
file.

### Saving an encoder:

This option will allow you to save the encoder for a particular file, so you can compress another file with this saved
encoder.

If you want to save the encoder for a particular file, before compressing a file, make sure that the "save encoder"
check box is selected. The encoder file will be saved in the same directory as the compressed file once the file has
been compressed.

### Loading an encoder:

This option allows you to load an encoder that you have saved.

If you want to load an encoder, make sure you select the right .ser file by using the "select .ser file" button. Then
when compressing the file the encoding that you selected will be used instead of an encoder being generated for the
file.