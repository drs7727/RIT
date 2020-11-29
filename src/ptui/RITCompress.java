package ptui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RITCompress {
    private static String[] arguments;
    private static int area;
    private static ArrayList<Integer> compressedFile;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RITCompress uncompressed-file.txt compressed-file.rit");
            return;
        }
        RITCompress compressor = new RITCompress(args);
    }

    public RITCompress(String[] args)
    {
        // create image and decompressor from args
        arguments = args;
        File compressedImage = new File("uncompressed\\" + args[0]);

        System.out.println("Compressing: images/uncompressed/" + args[0]);

        // build an arraylist from the file
        ArrayList<Integer> intList = buildList(compressedImage);
        area = intList.size();

        int[][] originalBrick = brickify(intList, area);

        // Assemble a list of values to write to the file
        ArrayList<Integer> data = compressedList(originalBrick);
        data.add(0, area);

        // Write the data (list) to an output file
        try {
            FileWriter writer = new FileWriter("compressed\\" + arguments[1]);
            for(int num : data)
            {
                String str = num + "";
                writer.write(str);
                writer.write("\n");
            }
            writer.close();
        }
        catch(IOException e)
        {
            System.err.println("Error: The output file cannot be created: " + arguments[1]);
            System.exit(0);
        }
        //tells what the output file is
        double compressionPercent = 100 - ((double)data.size() / (double)area * 100);

        // Printing out data
        System.out.println("QTree: ");
        for(int i = 1; i < data.size(); i++)
        {
            System.out.print(data.get(i) + " ");
        }
        System.out.println("\nOutput file: " + arguments[1]);
        System.out.println("Raw image size: " + area);
        System.out.println("Compresseed image size: " + data.size());
        System.out.println("Compresson %: " + compressionPercent);
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
     * @param area the area of the img
     * @return 2d decompressed array (brick) of the data
     */
    public int[][] brickify(ArrayList<Integer> values, int area) {
        int dimension = (int) Math.sqrt(area);

        // 2d array to build
        int[][] brick = new int[dimension][dimension];

        // current location in arraylist
        int loc = 0;

        for(int y = 0; y < dimension; y++) {
            for(int x = 0; x < dimension; x++) {
                brick[y][x] = values.get(loc);
                loc++;
            }
        }
        return brick;
    }

    /**
     * Go through the entire 2d array and check if all parts are the same.
     * @param brick 2d array representation of an image portion
     * @return if all values in the array are identical
     */
    public boolean validBrick(int[][] brick)
    {
        int initialVal = brick[0][0];
        for(int y = 0; y < brick.length; y++)
        {
            for(int x = 0; x < brick.length; x++)
            {
                if(initialVal != brick[y][x])
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Break a 2d array (brick) into 4 separate pieces
     * @param originalbrick unbroken 2d array
     * @return a list of 4 separate bricks
     */
    public ArrayList<int[][]> breakBrick(int[][] originalbrick)
    {
        // master arraylist for returning purposes
        ArrayList<int[][]> brokenBrick = new ArrayList<int[][]>();

        int dimension = originalbrick.length;
        // initialize the 4 arrays
        int[][] brickUL = new int[dimension/2][dimension/2];
        int[][] brickUR = new int[dimension/2][dimension/2];
        int[][] brickLL = new int[dimension/2][dimension/2];
        int[][] brickLR = new int[dimension/2][dimension/2];

        // Main Loop
        for(int y = 0; y < dimension/2; y++)
        {
            for(int x = 0; x < dimension/2; x++)
            {
                brickUL[y][x] = originalbrick[y][x];
                brickUR[y][x] = originalbrick[y][x + dimension/2];
                brickLL[y][x] = originalbrick[y + dimension/2][x];
                brickLR[y][x] = originalbrick[y + dimension/2][x + dimension/2];
            }
        }
        brokenBrick.add(brickUL);
        brokenBrick.add(brickUR);
        brokenBrick.add(brickLL);
        brokenBrick.add(brickLR);

        return brokenBrick;
    }

    /**
     * Takes the 2d array representation of an image and
     * breaks it down continuously until each part
     * is properly separated.
     * @return a list of individual 2d arrays, representing each part of a tree
     */
    public ArrayList<int[][]> buildLinearTree(int[][] originalBrick)
    {
        // initialize tree array
        ArrayList<int[][]> manyBricks = new ArrayList<int[][]>();
        manyBricks.add(originalBrick);

        // if flag remains false the loop is complete
        boolean flag = false;

        while(flag == false)
        {
            flag = false;
            for(int x = 0; x < manyBricks.size(); x++)
            {
                int[][] currentBrick = manyBricks.get(x);
                if(validBrick(currentBrick) == false)
                {
                    flag = true;
                    ArrayList<int[][]> split = breakBrick(currentBrick);
                    manyBricks.remove(x);
                    manyBricks.add(x, split.get(0));
                    manyBricks.add(x+1, split.get(1));
                    manyBricks.add(x+2, split.get(2));
                    manyBricks.add(x+3, split.get(3));
                }
            }
        }
        return manyBricks;
    }


    /**
     * CompressedList takes in a 2d array and tests to see of all values in that array are equal
     * and if they are it will return the value in that array, and if they aren't then
     * it will add -1 to the list and break the list up into 4 seperate parts and keep
     * recursivally during this until we have the compressed list
     * @param brick
     * @return
     */
    public ArrayList<Integer> compressedList(int[][] brick)
    {
        //compressed list
        ArrayList<Integer> compressedVal = new ArrayList<Integer>();
        if(validBrick(brick))
        {
            //adds the value to the arraylist if they are all equal
            compressedVal.add(brick[0][0]);
            return compressedVal;
        }
        else
        {
            //adds -1 to the list
            compressedVal.add(-1);
            //breaks the brick up into 4 seperate lists
            ArrayList<int[][]> brokenBrick = breakBrick(brick);
            //recursivally calls the method and adds the returned values to the list
            compressedVal.addAll(compressedList(brokenBrick.get(0)));
            compressedVal.addAll(compressedList(brokenBrick.get(1)));
            compressedVal.addAll(compressedList(brokenBrick.get(2)));
            compressedVal.addAll(compressedList(brokenBrick.get(3)));

            //returns list
            return compressedVal;
        }
    }
}
