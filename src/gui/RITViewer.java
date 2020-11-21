package gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

public class RITViewer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    public static void main(String[] args) throws Exception {

        if(args.length != 2)
        {
            System.out.println("Usage: filename img_width/height");
            System.exit(0);
        }

        File image = new File("uncompressed\\" + args[0]);
        Scanner scnr = new Scanner(image);

        // base resolution width and height for image
        int res_base = Integer.parseInt(args[1]);

        // location tracker for width --> allows us to know when to change the y value
        int x = 0;

        // location tracker for height
        int y = 0;

        // arraylist representing our read image data
        int[][] img_data = new int[res_base][res_base];

        while(scnr.hasNextLine())
        {
            String line = scnr.nextLine();
            if(x<res_base)
            {
                img_data[y][x] = Integer.parseInt(line);
                x++;
            }
            else {
                x = 0;
                y++;
            }
        }
        for(int i = 0; i < img_data.length; i++){
            for(int j = 0; j < img_data.length; j++){
                System.out.print(img_data[i][j]);
            }
        }
        Application.launch(args);
    }
}
