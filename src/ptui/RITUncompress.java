package ptui;

import model.RITQTNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class RITUncompress {
    private static String arguments[];
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
        ArrayList<Integer> origional = intList;
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

        System.out.println("Uncompressing: " + args[0]);
        System.out.println("QTree: " );
        for(int i = 0; i< origional.size(); i++)
        {
            System.out.print(origional.get(i));
        }
        System.out.println("Output file: ");

    }

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
     * Takes a quadree and breaks it down into a basic ArrayList
     * @param quadtree compressed data structure of image
     * @return decompressed integer list
     */
    public ArrayList<Integer> decompress(RITQTNode quadtree, int area) {
        if(quadtree.getVal() == -1) {
            // create master list and children lists
            int length = area/4;

            ArrayList<Integer> master = new ArrayList<Integer>();
            ArrayList<Integer> listUL = decompress(quadtree.getUpperLeft(), area / 4);
            ArrayList<Integer> listUR = decompress(quadtree.getUpperRight(), area / 4);
            ArrayList<Integer> listLL = decompress(quadtree.getLowerLeft(), area / 4);
            ArrayList<Integer> listLR = decompress(quadtree.getLowerRight(), area / 4);

            // add all elements to master list
            int[][] twoUL = this.twoArray(listUL, length/2);
            int[][] twoUR = this.twoArray(listUR, length/2);
            int[][] twoLL = this.twoArray(listLL, length/2);
            int[][] twoLR = this.twoArray(listLR, length/2);

            for(int i = 0; i< twoUL.length; i++)
            {
                ArrayList<Integer> hold = new ArrayList<Integer>();
                ArrayList<Integer> hold2 = new ArrayList<Integer>();
                for(int z = 0; i < twoUL.length; z++)
                {
                    hold.add(twoUL[i][z]);
                    hold2.add(twoUR[i][z]);
                }
                master.addAll(hold);
                master.addAll(hold2);
            }

            for(int i = 0; i< twoLL.length; i++)
            {
                ArrayList<Integer> hold = new ArrayList<Integer>();
                ArrayList<Integer> hold2 = new ArrayList<Integer>();
                for(int z = 0; i < twoLL.length; z++)
                {
                    hold.add(twoLL[i][z]);
                    hold2.add(twoLR[i][z]);
                }
                master.addAll(hold);
                master.addAll(hold2);
            }

            return master;
        }
        else
        {

            ArrayList<Integer> values = new ArrayList<Integer>();

            for(int j = 0; j < area; j++)
            {
                values.add(quadtree.getVal());
            }
            return values;
        }
    }

    public int[][] twoArray(ArrayList<Integer> list, int length)
    {
        int[][] master = new int[length][length];
        int i = 0;
        for(int y = 0; y<length; y++)
        {
            for(int x = 0; x<length; x++)
            {
                master[y][x] = list.get(i);
                i++;
            }
        }
        return master;
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