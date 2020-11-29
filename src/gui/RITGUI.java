package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ptui.RITCompress;
import ptui.RITUncompress;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class RITGUI extends Application {
    // compression tools used in our GUI
    private RITCompress compressor;
    private RITUncompress decompressor;

    // File Storage
    private File inputFile;
    private File outputFile;

    // storage for RITViewer display elements
    Group g;
    Canvas can;
    GraphicsContext gc;
    BorderPane bp;

    /**
     * Starting point of the application.
     * Generates an image and displays it.
     * @param stage the stage (window) the application is using
     * @throws Exception exception errors usually occur from invalid images / decompressed files
     */
    @Override
    public void start(Stage stage) throws Exception {
        bp = new BorderPane();
        // base resolution width and height for image
        int res_base = -1;

        // Create group structure for image display
        g = new Group();

        ObservableList<String> options = FXCollections.observableArrayList
                ("Compress", "Uncompress", "View", "Clear", "Quit");
        final ComboBox cbox = new ComboBox(options);

        cbox.setValue("Operation");


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");



        TextField inputField = new TextField();
        TextField outputField = new TextField();
        TextField consoleOutput = new TextField();
        consoleOutput.setEditable(false);

        // Set up the buttons
        Button inputButt = new Button();
        Button outputButt = new Button();
        inputButt.setText("Input File");
        outputButt.setText("Output File");

        // action events
        inputButt.setOnAction(actionEvent -> {
            inputFile = fileChooser.showOpenDialog(stage);
            if(inputFile != null) {
                inputField.setText(inputFile.getPath());
            }
        });

        outputButt.setOnAction(actionEvent -> {
            outputFile = fileChooser.showOpenDialog(stage);
            if(inputFile != null) {
                outputField.setText(outputFile.getPath());
            }
        });

        // top Area of The Scene is bp2
        BorderPane bp2 = new BorderPane();
        bp2.setTop(cbox);
        bp2.setCenter(inputField);
        BorderPane bp3 = new BorderPane();
        BorderPane bp4 = new BorderPane();

        // bp3 and bp4 are on the center and bottom respectively
        bp3.setLeft(inputButt);
        bp3.setCenter(inputField);
        bp4.setLeft(outputButt);
        bp4.setCenter(outputField);
        bp2.setCenter(bp3);
        bp2.setBottom(bp4);
        bp.setTop(bp2);

        bp.setBottom(consoleOutput);

        // set actions on dropdown
        cbox.setOnAction(actionEvent -> {
            if(cbox.getValue().equals("Compress"))
            {
                String[] args = new String[2];
                args[0] = inputFile.getName();
                args[1] = outputFile.getName();
                RITCompress compressor = new RITCompress(args);
                consoleOutput.setText(compressor.getDebug());
            }
            else if(cbox.getValue().equals("Uncompress")) {
                String[] args = new String[2];
                args[0] = inputFile.getName();
                args[1] = outputFile.getName();
                RITUncompress decompressor = new RITUncompress(args);
                consoleOutput.setText(decompressor.getDebug());
            }
            else if(cbox.getValue().equals("View")) {
                displayImage(inputFile.getName());
            }
            else if(cbox.getValue().equals("Clear"))
            {
                inputField.setText("");
                outputField.setText("");
                consoleOutput.setText("");
                clear();
            }
            else if(cbox.getValue().equals("Quit")) {
                System.exit(0);
            }
        }
        );

        // adds the border pane to the scene then displays it
        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Display an image on the RITViewer Center Pane
     * @param inputImg Name of input file
     */
    public void displayImage(String inputImg)
    {
        File testImage;
        Scanner scnr = null;

        // location tracker for width --> allows us to know when to change the y value
        int x = 0;

        // location tracker for height
        int y = 0;

        // base height/with of img
        int res_base = -1;

        // create file scanner with error handling
        try {
            testImage = inputFile;
            scnr = new Scanner(testImage);
        }
        catch(IOException fnfe) {
            System.err.println("Error: The file name specified is invalid: " + inputImg);
            System.exit(0);
        }

        // find res_base
        Scanner counter = null;
        try {
            testImage = inputFile;
            counter = new Scanner(testImage);
        }
        catch(IOException fnfe) {
            System.err.println("Error: The file name specified is invalid: " + inputImg);
            System.exit(0);
        }
        while (counter.hasNextLine()) {
            counter.nextLine();
            res_base++;
        }
        res_base = (int)Math.sqrt(res_base);
        res_base = res_base + 1;

        // Create group structure for image display
        g = new Group();
        can = new Canvas(res_base-1, res_base-1);
        gc = can.getGraphicsContext2D();

        // Scanner loop to find all integer values for grayscale
        while(scnr.hasNextLine()) {
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

        // set the borderpane center to canvas group
        bp.setCenter(g);
    }

    /**
     * Remove img from viewer
     */
    public void clear()
    {
        g.getChildren().clear();
        bp.setCenter(g);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}

