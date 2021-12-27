package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

/**
 * Wraps one square of the maze and tracks its contents (array list of collideables).
 */
public class MazeSquare {

    private Pane gamePane;
    private Rectangle rect;
    private ArrayList<Collideable> collideables;

    /*
    Constructs an empty maze square as rectangle and creates an arraylist for each square
     */
    public MazeSquare(Pane gamePane) {
        this.gamePane = gamePane;
        this.rect = new Rectangle(Constants.SQ_WIDTH, Constants.SQ_WIDTH, Color.BLACK);
        this.collideables = new ArrayList<>();
        this.gamePane.getChildren().add(this.rect);
    }

    /*
    Public method to add an object to the array list of collideable objects of the particular square
     */
    public void addCollideable(Collideable object) {
        this.collideables.add(object);
    }

    /*
    Public method to remove an object from the array list of collideables contained in this square
     */
    public void removeCollideable(Collideable object) { this.collideables.remove(object);}

    /*
    Public method to clear the array list of collideables and remove all its contents
     */
    public void clearArray() {
        this.collideables.clear();
    }

    /*
    Setter method to set the x location of the square (column)
    */
    public void setCol(double X) {
        this.rect.setX(X);
    }

    /*
    Setter method to change the y location of the square (row)
    */
    public void setRow(double Y) {
        this.rect.setY(Y);
    }

    /*
    Setter method to change the colour of the square
    */
    public void setColor(Color color) {
        this.rect.setFill(color);
    }

    /*
    Getter method to obtain the colour of the square
     */
    public Color getColor() {
        return (Color) this.rect.getFill();
    }

    /*
    Getter method to return the arraylist of collideable objects
     */
    public ArrayList<Collideable> getCollideables() {
        return collideables;
    }

}
