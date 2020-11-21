package ptui;

import model.RITQTNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class RITUncompress {
    private static String arguments[];
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RITUncompress compressed.rit uncompressed.txt");
            return;
        }
        arguments = args;
        File compressedImage = new File("compressed\\" + args[0]);
        RITUncompress decompressor = new RITUncompress();
        decompressor.buildList(compressedImage);
    }

    public ArrayList<Integer> buildList(File compressedImg)
    {
        Scanner scnr = null;
        try
        {
            scnr = new Scanner(compressedImg);
        }
        catch(FileNotFoundException fnfe)
        {
            System.err.println("Error: The file name specified in argument 0 is invalid: " + arguments[0]);
            System.exit(0);
        }

        ArrayList<Integer> compressedValues = new ArrayList<Integer>();

        while(scnr.hasNextLine())
        {
            int currentVal = Integer.parseInt(scnr.nextLine());
            compressedValues.add(currentVal);
        }
        return compressedValues;
    }

    public RITQTNode parse(ArrayList<Integer> list)
    {
        int baseInt = list.get(0);
        if(baseInt != -1)
        {
            list.remove(0);
            return new RITQTNode(baseInt);
        }
        else if(list.size() >= 1)
        {
            list.remove(0);
            RITQTNode ul = parse(list);
            list.remove(0);
            RITQTNode ur = parse(list);
            list.remove(0);
            RITQTNode ll = parse(list);
            list.remove(0);
            RITQTNode lr = parse(list);
            list.remove(0);
            return new RITQTNode(baseInt, ul, ur, ll, lr);
        }
        return null;
    }

}