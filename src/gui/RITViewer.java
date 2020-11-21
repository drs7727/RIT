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
import java.util.Scanner;

public class RITViewer extends Application {

    // stores the arguments from the main method
    private static String[] arguments;

    @Override
    public void start(Stage stage) throws Exception {
        if(arguments.length != 2)
        {
            System.out.println("Usage: filename img_width/height");
            System.exit(0);
        }

        File image = new File("uncompressed\\" + arguments[0]);
        Scanner scnr = new Scanner(image);

        // base resolution width and height for image
        int res_base = Integer.parseInt(arguments[1]);

        // location tracker for width --> allows us to know when to change the y value
        int x = 0;

        // location tracker for height
        int y = 0;

        Group g = new Group();
        Canvas can = new Canvas(res_base, res_base);
        GraphicsContext gc = can.getGraphicsContext2D();

        while(scnr.hasNextLine())
        {
            String line = scnr.nextLine();
            if(x<res_base-1)
            {
                double val = Double.parseDouble(line);
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

    public static void main(String[] args) throws Exception {
        arguments = args;
        Application.launch(args);
    }
}
