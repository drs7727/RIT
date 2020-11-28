package ptui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class RITCompress {
    private static String[] arguments;
    private static int area;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RITCompress uncompressed-file.txt compressed-file.rit");
            return;
        }
        // create image and decompressor from args
        arguments = args;
        File compressedImage = new File("uncompressed\\" + args[0]);
        ptui.RITCompress compressor = new ptui.RITCompress();

        // build an arraylist from the file
        ArrayList<Integer> intList = compressor.buildList(compressedImage);
        area = intList.size();

        System.out.println(area);

    }
    /**
     * Turns the input file into a arraylist
     * @param compressedImg the input being read from the file
     * @return a list of integers given from the file
     */
    public ArrayList<Integer> buildList(File compressedImg) {
        Scanner scnr = null;
        try {
            scnr = new Scanner(compressedImg);
        }
        catch(FileNotFoundException fnfe) {
            System.err.println("Error: The file name specified in argument 0 is invalid: " + arguments[0]);
            System.exit(0);
        }

        ArrayList<Integer> compressedValues = new ArrayList<Integer>();

        while(scnr.hasNextLine()) {
            int currentVal = Integer.parseInt(scnr.nextLine());
            compressedValues.add(currentVal);
        }
        return compressedValues;
    }

    /**
     * This method does a simple traversal through a list
     * to assemble a 2d representation of the image.
     * @param values the numbers to traverse through for making an image.
     * @param dimension the width/height of the image.
     * @return 2d decompressed array (brick) of the data
     */
    public int[][] brickify(ArrayList<Integer> values, int dimension) {
        // 2d array to build
        int[][] brick = new int[dimension][dimension];

        // current location in arraylist
        int loc = 0;

        for(int y = 0; y < dimension; y++) {
            for(int x = 0; x < dimension; x++) {
                brick[y][x] = values.get(loc);
            }
        }
        return brick;
    }
}
