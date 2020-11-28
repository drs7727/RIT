package ptui;

import model.RITQTNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * RITUncompress is a system that takes a compressed image and breaks it into a text file.
 * It takes two arguments -inputfile -outputfile
 *
 * RITUncompress successfully does this process through the following steps.
 * - buildList -- takes the input image and generates an arraylist to iterate through
 * - parse -- generates a quadtree from the arraylist
 * - brickify -- generates a 2d array (brick) representation of the image
 * - Final Step: iterate through the 2d array and write the text file using I/O
 */
public class RITUncompress {
    private static String arguments[];

    /**
     * Main method creates a compressor object and does the processes necessary
     * to output a decompressed file.
     * @param args the 2 arguments required for output (two text file names)
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RITUncompress compressed.rit uncompressed.txt");
            return;
        }

        // create image and decompressor from args
        arguments = args;
        File compressedImage = new File("compressed\\" + args[0]);
        ptui.RITUncompress decompressor = new ptui.RITUncompress();

        // build an arraylist from the file
        ArrayList<Integer> intList = decompressor.buildList(compressedImage);
        ArrayList<Integer> original = decompressor.buildList(compressedImage);
        int area = intList.get(0); // get base area
        intList.remove(0);

        // generate quadtree from generated arraylist
        RITQTNode quadtree = decompressor.parse(intList);

        /* create a 'brick' by traversing through the tree and making a 2d array
        representation of the image
        */
        int[][] brick = decompressor.brickify(quadtree, area, true);

        // assemble the 'brick' into a linear arraylist
        ArrayList<Integer> master = new ArrayList<Integer>();
        for(int y = 0; y < brick.length; y++)
        {
            for(int x = 0; x < brick.length; x++)
            {
                master.add(brick[y][x]);
            }
        }


        //prints out the name of the file we are uncompressing
        System.out.println("Uncompressing: " + args[0]);
        //prints out the quad tree of the file
        System.out.print("QTree: " );
        //reads the uncompressed image to the file for viewing
        for(int i = 0; i< original.size(); i++)
        {
            System.out.print(original.get(i) + " ");
        }
        System.out.println();
        try {
            FileWriter writer = new FileWriter("uncompressed\\" + arguments[1]);
            for(int num : master)
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
        System.out.println("Output file: " + arguments[1]);


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
     * returns the node of the quad tree
     * @param list takes the list read in from the file
     * @return a quad tree node from the list given
     */
    public RITQTNode parse(ArrayList<Integer> list)
    {
        if(list.size() !=0) {
            int baseInt = list.get(0);
            if (baseInt != -1) {
                return new RITQTNode(baseInt);
            } else {
                list.remove(0);
                RITQTNode ul = parse(list);
                list.remove(0);
                RITQTNode ur = parse(list);
                list.remove(0);
                RITQTNode ll = parse(list);
                list.remove(0);
                RITQTNode lr = parse(list);
                return new RITQTNode(baseInt, ul, ur, ll, lr);
            }
        }
        return null;
    }

    /**
     * This method does a preorder traversal through the given tree
     * and generates a 2d array of the img, using position based logic.
     *
     * Each 2d array unit is represented by the subword 'brick'
     *
     * @param quadtree the quadtree to decompress
     * @param area the total area of the image
     * @param init if this call is the initial call of the method (put in true)
     * @return 2d decompressed array (brick) of the data
     */
    public int[][] brickify(RITQTNode quadtree, int area, boolean init)
    {
        if(init)
        {
            area = (int) Math.sqrt((double)area);
        }
        if(quadtree.getVal() == -1)
        {
            int[][] listUL = brickify(quadtree.getUpperLeft(), area/2, false);
            int[][] listUR = brickify(quadtree.getUpperRight(), area/2, false);
            int[][] listLL = brickify(quadtree.getLowerLeft(), area/2, false);
            int[][] listLR = brickify(quadtree.getLowerRight(), area/2, false);
            int[][] bigBrick = new int[area][area];

            // Assemble the great and mighty brick
            int briklen = bigBrick.length/2;

            for(int y = 0; y < briklen; y++)
            {
                for(int x = 0; x < briklen; x++)
                {
                    bigBrick[y][x] = listUL[y][x];
                    bigBrick[y][briklen + x] = listUR[y][x];
                    bigBrick[briklen + y][x] = listLL[y][x];
                    bigBrick[briklen + y][briklen + x] = listLR[y][x];
                }
            }
            return bigBrick;
        }
        else
        {
            // find the quadtree given value
            int val = quadtree.getVal();

            // generate another mighty brick
            // we generate this based off the current area of the 2d array (brick)
            int[][] littleBrick = new int[area][area];
            for(int y = 0; y < area; y++)
            {
                for(int x = 0; x < area; x++)
                {
                    littleBrick[y][x] = val;
                }
            }
            return littleBrick;
        }
    }
}