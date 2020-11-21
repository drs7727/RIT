package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * RITViewer takes an uncompressed image full of grayscale
 * pixel values and puts it on display using JavaFX.
 *
 * Two arguments are put in, the name of the uncompressed file and the base width/height.
 * The base width and height have to be equal for the program to function.
 *
 * @author Cody Smith (bcs4313@g.rit.edu)
 * @author Dylan Spence (drs7727@g.rit.edu)
 */
public class RITViewer extends Application {

    // stores the arguments from the main method
    private static String[] arguments;

    /**
     * Starting point of the application.
     * Generates an image and displays it.
     * @param stage the stage (window) the application is using
     * @throws Exception exception errors usually occur from invalid images / decompressed files
     */
    @Override
    public void start(Stage stage) throws Exception{
        if(arguments.length != 2) {
            System.out.println("Usage: filename img_width/height");
            System.exit(0);
        }

        File testImage;
        Scanner scnr = null;

        // create file scanner with error handling
        try {
            testImage = new File("uncompressed\\" + arguments[0]);
            scnr = new Scanner(testImage);
        }
        catch(IOException fnfe) {
            System.err.println("Error: The file name specified in argument 0 is invalid: " + arguments[0]);
            System.exit(0);
        }

        // base resolution width and height for image
        int res_base = Integer.parseInt(arguments[1]);


        // location tracker for width --> allows us to know when to change the y value
        int x = 0;

        // location tracker for height
        int y = 0;

        // Create group structure for image display
        Group g = new Group();
        Canvas can = new Canvas(res_base-1, res_base-1);
        GraphicsContext gc = can.getGraphicsContext2D();

        // Scanner loop to find all integer values for grayscale
        while(scnr.hasNextLine())
        {
            String line = scnr.nextLine();
            if(x<res_base-1) {
                double val = 0;
                try {
                    val = Double.parseDouble(line);
                }
                catch(Exception e) {
                    System.err.println("Value specified is invalid at line " + (x + y*res_base)+1);
                    System.exit(0);

                }
                if(val<0 || val>255) {
                    System.err.println("Invalid integer value for color");
                    System.exit(0);
                }
                Color c = new Color(val/255, val/255, val/255, 1);
                gc.setFill(c);
                gc.fillRect(x, y,1,1);
                x++;
            }
            else {
                x = 0;
                y++;
            }
        }

        g.getChildren().add(can);

        // Set up the scene and put the image inside
        BorderPane bp = new BorderPane();

        // set the borderpane center to canvas group
        bp.setCenter(g);

        // adds the border pane to the scene then displays it
        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        arguments = args;
        Application.launch(args);
    }
}
