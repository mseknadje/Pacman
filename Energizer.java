package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * Represents an energizer item as a circle which implements the collideable interface with collision and score.
 */
public class Energizer implements Collideable {

    private Circle circle;
    private Pane gamePane;
    private Game game;

    /*
    Energizer is associated with location and game class, in order to set its location when instantiated in the game class
    and add to the score calculated in the game class.
    */
    public Energizer(double xLoc, double yLoc, Pane gamePane, Game game) {
        this.game = game;
        this.gamePane = gamePane;
        this.circle = new Circle(xLoc, yLoc, Constants.ENERGIZER_RADIUS, Constants.ENERGIZER_COLOR);
        this.gamePane.getChildren().add(this.circle);
    }

    /*
    Method declared in the collideable interface defined in this class to make the energizer disappear, add to count
    of eaten objects, add to the score, and trigger Frightened mode.
    */
    @Override
    public void collide() {
        this.gamePane.getChildren().remove(this.circle);
        // 1 adds to dot count to determine when player wins the game, energizer score increases the score.
        this.game.updateScore(1, Constants.ENERGIZER_SCORE);
        // calling the setter method sets the frightened mode boolean to true any time pacman collides with energizer
        this.game.setFrightMode(true);
    }
}
