package paint;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javafx.scene.shape.Circle;
import javafx.scene.control.Slider;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.control.TextArea;

public class Paint extends Application {
    
    @Override
    public void start(Stage primaryStage) {  
    
//Create menu buttons
    //Toggle buttons can be selected and unselected
        ToggleButton drawbtn = new ToggleButton("Draw");
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton erasebtn = new ToggleButton("Eraser");
        ToggleButton recbtn = new ToggleButton("Rectangle");
        ToggleButton circbtn = new ToggleButton("Circle");
        ToggleButton elpsbtn = new ToggleButton("Ellipse");
        ToggleButton textbtn = new ToggleButton("Text");
    //Regular buttons get clicked once and an action happens
        Button saveas = new Button("Save As");
        Button open = new Button("Open");
        Button save = new Button("Save");
        Button undo = new Button("Undo");
        Button redo = new Button("Redo");
        
//Shape functionalities
        Line line = new Line();
        Rectangle rec = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();
        
//Create stacks for undo and redo functionality
        Stack<Shape> undoHistory = new Stack();
        Stack<Shape> redoHistory = new Stack(); 
	
//Implement menu buttons
        ToggleButton[] toolsArr = {drawbtn, linebtn, erasebtn, recbtn, circbtn, elpsbtn, textbtn};
        ToggleGroup tools = new ToggleGroup();
        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
        }
        
//Initializing Color Picker and Color fills selections  
        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);
        
//Creating label names		
        Label line_color = new Label("Line Color");
        Label fill_color = new Label("Fill Color");
        
//Creating line width selector       
        Slider slider = new Slider(1, 25, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
    
//Creating text area functionality
        TextArea text = new TextArea();
        text.setPrefRowCount(1);

//Menu with buttons
        VBox btns = new VBox(10);
        btns.getChildren().addAll(drawbtn, linebtn, slider, line_color, cpLine, fill_color, cpFill, open, saveas, erasebtn, recbtn, circbtn, elpsbtn, textbtn, text, save, undo, redo);
        btns.setPadding(new Insets(5));
        btns.setPrefWidth(100);
        
//Setting up the blank canvas
        Canvas canvas = new Canvas(1080, 790);
        GraphicsContext gc;
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        
		
//Was the mouse button pressed on the canvas..                  
        canvas.setOnMousePressed(e->{
            if(drawbtn.isSelected()) {          //While the draw button is selected
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            }
            else if(linebtn.isSelected()) {     //While the line button is selected
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());       //Start line at current x value
                line.setStartY(e.getY());       //Start line at current y value
            }
            else if(erasebtn.isSelected()) {    //While the eraser button is selected
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
            else if(recbtn.isSelected()) {      //While the rectangle button is selected
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());  //Fill the rectangle with fill color
                rec.setX(e.getX());             //Start rectangle at current x
                rec.setY(e.getY());             //Start rectangle at current y
            }
            else if(circbtn.isSelected()) {     //While the circle button is selected
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());  //Fill the circle with fill color
                circ.setCenterX(e.getX());      //Start circle at current x
                circ.setCenterY(e.getY());      //Start circle at current x
            }
            else if(elpsbtn.isSelected()) {     //While the ellipse button is selected
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());  //Fill the ellipse with fill color
                elps.setCenterX(e.getX());      //Start ellipse at current x
                elps.setCenterY(e.getY());      //Start ellipse at current y
            }
            else if(textbtn.isSelected()) {     //When the text button is pressed
                gc.setLineWidth(1);             
                gc.setFont(Font.font(slider.getValue()));   //"Font Size" using slider value
                gc.setStroke(cpLine.getValue());           
                gc.setFill(cpFill.getValue());              //"Font color" from fill color
                gc.fillText(text.getText(), e.getX(), e.getY());   //Place text with fill at 
                gc.strokeText(text.getText(), e.getX(), e.getY());  // current x and y
            }
        });
        
//Was the mouse button released...
        canvas.setOnMouseReleased(e->{
            if(drawbtn.isSelected()) {      //while the draw button was selcted
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            }
            else if(linebtn.isSelected()) { //while the line button was selcted
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            }
            else if(erasebtn.isSelected()) {    //while the erase button was selcted
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
            else if(recbtn.isSelected()) {      //while the rectangle button was selcted
                rec.setWidth(Math.abs((e.getX() - rec.getX())));
                rec.setHeight(Math.abs((e.getY() - rec.getY())));
//If X value of rectangle is greater than the X value of e, make e = rectangle value
                if(rec.getX() > e.getX()) {
                    rec.setX(e.getX());
                }
//If Y value of rectangle is greater than the Y value of e, make e = rectangle value
                if(rec.getY() > e.getY()) {
                    rec.setY(e.getY());
                }
//Filling the rectangle with color
                gc.fillRect(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());
                gc.strokeRect(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());               
                undoHistory.push(new Rectangle(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight()));            
            }
            else if(circbtn.isSelected()) {
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);
                
                if(circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if(circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }
                
                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));            
            }
            else if(elpsbtn.isSelected()) {
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));
                
                if(elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if(elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }
                
                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                
                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
            }
            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());
        });
        
//Draws and creates line based on mouse clicks    
        canvas.setOnMouseDragged(e->{
            if(drawbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
//Erases areas where the mouse is clicked and dragged over
            else if(erasebtn.isSelected()){
                double lineWidth = gc.getLineWidth();   //Changes size of eraser
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
        });
		
//Setting up the color picker for line/drawing
        cpLine.setOnAction(e->{
                gc.setStroke(cpLine.getValue());
        });
//Setting up the color fill
        cpFill.setOnAction(e->{
                gc.setFill(cpFill.getValue());
        });
//Setting up the width slider
        slider.valueProperty().addListener(e->{
            double width = slider.getValue();
            gc.setLineWidth(width);
        });
        
//Open a file from desktop
        open.setOnAction((e)->{
            FileChooser openFile = new FileChooser();
            openFile.setTitle("Open File");
            File file = openFile.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    InputStream io = new FileInputStream(file);
                    Image img = new Image(io);
                    gc.drawImage(img, 0, 0);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });
        
//Save Canvas
        saveas.setOnAction((e)->{
            FileChooser savefile = new FileChooser();
            savefile.setTitle("Save File");
            File file = savefile.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(1080, 790);
                    canvas.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
            });
        save.setOnAction((e)->{
});
        
        
//Undo functioncality
        undo.setOnAction(e->{
            if(!undoHistory.empty()){
                gc.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if(removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(gc.getFill());
                    tempLine.setStroke(gc.getStroke());
                    tempLine.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                    
                }
                else if(removedShape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) removedShape;
                    tempRect.setFill(gc.getFill());
                    tempRect.setStroke(gc.getStroke());
                    tempRect.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                }
                else if(removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(gc.getLineWidth());
                    tempCirc.setFill(gc.getFill());
                    tempCirc.setStroke(gc.getStroke());
                    redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                }
                Shape lastRedo = redoHistory.lastElement();
                lastRedo.setFill(removedShape.getFill());
                lastRedo.setStroke(removedShape.getStroke());
                lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
                undoHistory.pop();
                
                for(int i=0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if(shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    }
                    else if(shape.getClass() == Rectangle.class) {
                        Rectangle temp = (Rectangle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    }
                    else if(shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    }
                }
            }
        });
        
//Redo Functionality
        redo.setOnAction(e->{
            if(!redoHistory.empty()) {
                Shape shape = redoHistory.lastElement();
                gc.setLineWidth(shape.getStrokeWidth());
                gc.setStroke(shape.getStroke());
                gc.setFill(shape.getFill());
                    
                redoHistory.pop();
                if(shape.getClass() == Line.class) {
                    Line tempLine = (Line) shape;
                    gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                    undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                }
                else if(shape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) shape;
                    gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    
                    undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                }
                else if(shape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) shape;
                    gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    
                    undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                }
                Shape lastUndo = undoHistory.lastElement();
                lastUndo.setFill(gc.getFill());
                lastUndo.setStroke(gc.getStroke());
                lastUndo.setStrokeWidth(gc.getLineWidth());
            } else {
                System.out.println("there is no action to redo");
            }
        });
        
//Borders of screen, placing menu 
        BorderPane pane = new BorderPane();
        pane.setRight(btns);
        pane.setCenter(canvas);
        
//Window scene
        Scene scene = new Scene(pane, 1200, 800);   //Sets size of window
        primaryStage.setTitle("Paint");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

//Launch Program
    public static void main(String[] args) {
        launch(args);
    }
}