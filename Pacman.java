package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents the pacman as a circle moving around the maze that can change direction,
 * wrap around the maze, and check for move validity whenever it moves into a square.
 */
public class Pacman {
    private Pane gamePane;
    private Circle circle;
    private Game game;

    /*
    Constructs the pacman as a yellow circle associated with the game
     */
    public Pacman(Pane gamePane, Game game) {
        this.gamePane = gamePane;
        this.circle = new Circle(Constants.PACMAN_RADIUS, Color.YELLOW);
        this.game = game;
        this.gamePane.getChildren().add(this.circle);
    }

    /*
    Sets the position of the pacman for a given x and y coordinate
     */
    public void setPacmanLoc(double xLoc, double yLoc) {
        this.circle.setCenterX(xLoc);
        this.circle.setCenterY(yLoc);
    }

    /*
    Checks for the validity of a move pacman wants to make, by calculating new position after tentative move,
    checking for colour validity (cannot move into a wall), and return a boolean given these conditions.
     */
    public boolean checkDirValid(int yMove, int xMove) {
        // gets the current location
        int row = this.getRow();
        int col = this.getCol();

        // returns the 2D array that represents the maze created in the game class
        MazeSquare[][] maze = this.game.returnMazeArray();

        /*
        Handles edge case when pacman needs to wrap around, pacman must continue to move is it is at extrema
        otherwise program generates an array out of bounds error since there is no square to check outside
         */
        if ((col + xMove) < 0 || (col + xMove) >= Constants.MAZE_DIMENSION) {
            this.wrapPacman();
            return true;
        }

        // this will return true if the new square is not a wall
        else return maze[row + yMove][col + xMove].getColor() != Constants.WALL_COLOR;
    }

    /*
     * Handles key input by changing direction of pacman on up, down, left, and
     * right arrow keys, given a direction and current direction.
     *
     * For each direction, the method checks whether a move is valid and if so returns a direction to move in.
     * Otherwise, the method returns the current direction and Pacman continues to move in the same direction until
     * a new move is valid.
     */
    public Direction setMoveDirection(Direction direction, Direction currentDir) {
        switch (direction) {
            case LEFT:
                if (this.checkDirValid(0, -1)) {
                    return Direction.LEFT;
                }
                break;
            case RIGHT:
                if (this.checkDirValid(0, 1)) {
                    return Direction.RIGHT;
                }
                break;
            case DOWN:
                if (this.checkDirValid(1, 0)) {
                    return Direction.DOWN;
                }
                break;
            case UP:
                if (this.checkDirValid(-1, 0)) {
                    return Direction.UP;
                }
                break;
        }
        /* if no new direction is valid, then pacman should continue moving in the same direction
         * until it can make a new move.
         */
        return currentDir;
    }

    /*
     * Handles pacman's graphical movement around the move, given an associated direction.
     * For each direction, check for move validity in that direction and move pacman by the one square.
     */
    public void movePacman(Direction direction) {
        switch (direction) {
            case UP:
                if (this.checkDirValid(-1, 0)) {
                    this.circle.setCenterY(this.circle.getCenterY() - Constants.PACMAN_SPEED);
                }
                break;
            case DOWN:
                if (this.checkDirValid(1, 0)) {
                    this.circle.setCenterY(this.circle.getCenterY() + Constants.PACMAN_SPEED);
                }
                break;
            case LEFT:
                if (this.checkDirValid(0, -1)) {
                    this.circle.setCenterX(this.circle.getCenterX() - Constants.PACMAN_SPEED);
                }
                break;
            case RIGHT:
                if (this.checkDirValid(0, 1)) {
                    this.circle.setCenterX(this.circle.getCenterX() + Constants.PACMAN_SPEED);
                }
                break;
        }
    }

    /*
     * Handles pacman's wrapping around the maze when pacman moves in the tunnel. We set pacman's location
     * the other extrema of the map, only when the row corresponds to the row in which the tunnel is located.
     */
    public void wrapPacman() {
        if (this.circle.getCenterX() <= Constants.SQ_WIDTH && this.getRow() == Constants.TUNNEL_ROW) {
            this.circle.setCenterX((Constants.RIGHT_EXTREMA * Constants.SQ_WIDTH) - Constants.SQ_OFFSET);
        }
        if (this.circle.getCenterX() >= Constants.RIGHT_EXTREMA * Constants.SQ_WIDTH && this.getRow() == Constants.TUNNEL_ROW) {
            // because pacman is a circle, we need to add an offset to keep pacman aligned to each column's center.
            this.circle.setCenterX(Constants.SQ_OFFSET);
        }
    }

    /*
     * Getter method to calculate and return the column index of pacman current location
     */
    public int getCol() {
        return (int) (this.circle.getCenterX() / Constants.SQ_WIDTH);
    }

    /*
     * Getter method to calculate and return the row index of pacman current location
     */
    public int getRow() {
        return (int) (this.circle.getCenterY() / Constants.SQ_WIDTH);
    }

    /*
    Sets the circle shape to front in order for pacman to show up in front of collideable objects (dots)
     */
    public void bringToFront(){
        this.circle.toFront();
    }
}
