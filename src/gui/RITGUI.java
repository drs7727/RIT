package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class RITGUI extends Application {
    // stores the arguments from the main method
    private static String[] arguments;

    // storage for RITViewer display elements
    Group g;
    Canvas can;
    GraphicsContext gc;

    /**
     * Starting point of the application.
     * Generates an image and displays it.
     * @param stage the stage (window) the application is using
     * @throws Exception exception errors usually occur from invalid images / decompressed files
     */
    @Override
    public void start(Stage stage) throws Exception {
        // base resolution width and height for image
        int res_base = -1;

        // Create group structure for image display
        g = new Group();
        can = new Canvas(res_base-1, res_base-1);
        gc = can.getGraphicsContext2D();

        // Set up the scene and put the image inside
        BorderPane bp = new BorderPane();

        // set the borderpane center to canvas group
        bp.setCenter(g);

        ObservableList<String> options = FXCollections.observableArrayList
                ("Compress", "Uncompress", "View", "Clear", "Quit");
        final ComboBox cbox = new ComboBox(options);

        cbox.setValue("Operation");
        bp.setTop(cbox);

        // set actions on dropdown
        cbox.setOnAction(e -> {
           if(cbox.getValue().equals("Compress"))
           {

           }
           else if(cbox.getValue().equals("Uncompress"))
           {

           }
           else if(cbox.getValue().equals("View"))
           {

           }
           else if(cbox.getValue().equals("Clear"))
           {

           }
           else if(cbox.getValue().equals("Quit"))
           {

           }
        }
        );

        TextField input = new TextField();

        // adds the border pane to the scene then displays it
        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Display image on RITViewer Pane
     * @param inputImg Name of input file
     * @param res_base base width_height of img
     */
    public void displayImage(String inputImg, int res_base)
    {
        File testImage;
        Scanner scnr = null;

        // location tracker for width --> allows us to know when to change the y value
        int x = 0;

        // location tracker for height
        int y = 0;

        // create file scanner with error handling
        try {
            testImage = new File("uncompressed\\" + arguments[0]);
            scnr = new Scanner(testImage);
        }
        catch(IOException fnfe) {
            System.err.println("Error: The file name specified in argument 0 is invalid: " + arguments[0]);
            System.exit(0);
        }

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
    }

    public static void main(String[] args) {
        arguments = args;
        Application.launch(args);
    }
}

