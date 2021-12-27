package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * Represents a dot item as a circle which implements the collideable interface with collision and score.
 */
public class Dot implements Collideable {

    private Circle circle;
    private Pane gamePane;
    private Game game;

    /*
    Dots is associated with location and game class, in order to set its location when instantiated in the game class
    and add to the dot count calculated in the game class.
     */
    public Dot(double xLoc, double yLoc, Pane gamePane, Game game) {
        this.gamePane = gamePane;
        this.game = game;
        this.circle = new Circle(xLoc, yLoc, Constants.DOT_RADIUS, Constants.DOT_COLOR);
        this.gamePane.getChildren().add(this.circle);
    }

    /*
    Method declared in the collideable interface defined in this class to make the dot disappear, add to count
    of dots to determine when the game has ended, and return a unique score (10).
     */
    @Override
    public void collide() {
        this.gamePane.getChildren().remove(this.circle);
        // 1 adds to count of dots to determine when the players wins, dot score increases the score.
        this.game.updateScore(1, Constants.DOT_SCORE);
    }
}
