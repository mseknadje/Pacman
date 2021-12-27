package pacman;

/**
 * Represents the ghost as a collideable rectangle that can change direction,
 * move with BFS or with random direction, collide with pacman and return a score.
 */
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.lang.Math;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

public class Ghost implements Collideable{
    private Pane gamePane;
    private Rectangle rect;
    private MazeSquare[][] maze;
    private Game game;
    /*
    Keeps track of direction currently moving in to account for wrapping when the ghost
    must continue to move in the same direction, updated in BFS.
     */
    private Direction currDirection;
    private Direction[][] directions; // 2D array of directions that represent the whole maze
    private Color color; // keeps track of original colour so ghost can turn back to its colour after frightened.

    public Ghost(Pane gamePane, Color ghostColor, Game game) {
        this.gamePane = gamePane;
        this.rect = new Rectangle(Constants.SQ_WIDTH, Constants.SQ_WIDTH);
        this.rect.toFront();
        this.game = game;
        this.color = ghostColor;
        this.setColor();
        this.currDirection = Direction.UP; // initialise the direction of the ghosts to move upwards
        this.maze = this.game.returnMazeArray(); // ghosts must move in the same maze that we build in the game class
        gamePane.getChildren().add(this.rect);
    }

    /*
    Changes the colour of the ghost depending on whether frightened mode is activated.
     */
    private void setColor() {
        if (this.game.getFrightMode()) {
            this.rect.setFill(Color.BLUE);
        }
        else {
            this.rect.setFill(color);
        }
    }

    /*
    Setter method which takes in two parameters to set x and y location all at once.
     */
    public void setGhostLoc(double xLoc, double yLoc) {
        this.rect.setX(xLoc);
        this.rect.setY(yLoc);
    }

    /*
    Getter method to convert x coordinate to column and return this index.
     */
    private int getGhostCol() {
        return (int) this.rect.getX()/Constants.SQ_WIDTH;
    }

    /*
    Getter method to convert y coordinate to row and return this index.
    */
    private int getGhostRow() { return (int) this.rect.getY()/Constants.SQ_WIDTH; }

    /*
    We use override to define the collide method declared in collideable interface since ghost is a collideable object.
    If in frightened mode, then ghost returns to the pen and game score increases.
    In any other mode, the ghost causes pacman to lose a life and game resets.
     */
    @Override
    public void collide() {
        // call boolean getter method to determine status of game mode
        if (this.game.getFrightMode()) {
            // pacman eats ghost and ghost returns to ghost pen.
            this.game.addToPen(this);
            this.game.updateScore(0, Constants.GHOST_SCORE);
        }
        else {
            // pacman loses a life and the game resets
            this.game.reset();
        }
    }

    /*
    Moves the ghost with BFS algorithm, which tells the ghost the shortest path towards a given target
    by checking all the neighbouring valid squares, assigning a direction, and adding them to a queue.
    Uses a while loop to continuously obtain the square at top of queue and calculates the distance from that
    location to the target, and updates the direction accordingly if this distance is the shortest.
     */
    public Direction ghostBFS(BoardCoordinate target) {
        // set to very large threshold so that minimum distance is immediately updated.
        int minDistance = Constants.MIN_DISTANCE;
        // initialise the direction as null
        Direction direction = null;
        // initialize 2D array of directions
        this.directions = new Direction[Constants.MAZE_DIMENSION][Constants.MAZE_DIMENSION];
        // initialize the queue
        Queue<BoardCoordinate> queue = new LinkedList<>();
        // access ghost x and y location and store as local variables
        int row = this.getGhostRow();
        int col = this.getGhostCol();
        /*
         account for wrapping by first checking whether location is within the board, otherwise
         this would cause a null pointer exception, so order matters.
         */
        if(!(col > 0 && col < 22)) {
            return this.wrapGhost(row, col);
        }
        // access current location
        BoardCoordinate currentLoc = new BoardCoordinate(row, col , true);
        // the first initial neighbours will be directly assigned a direction, so pass in true boolean variable
        this.addValidNeighbour(currentLoc, true, queue);
        // as long as the queue is not empty, keeps adding new neighbours and updating the minimum distance
        while(!queue.isEmpty()) {
            // dequeue current location, the oldest BC added to the queue
            BoardCoordinate newLoc =  queue.remove();
            // calculate distance to target using euclidean geometry
            double distanceX = newLoc.getColumn()*Constants.SQ_WIDTH - target.getColumn()*Constants.SQ_WIDTH ;
            double distanceY = newLoc.getRow()*Constants.SQ_WIDTH - target.getRow()*Constants.SQ_WIDTH ;
            // access direction that was assigned to current location
            Direction curDirection = this.directions[newLoc.getRow()][newLoc.getColumn()];

            double distance = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
            // see if distance < min distance
            if (distance < minDistance) {
                // update the minimum distance to this new minimum
                minDistance = (int) distance;
                // update the direction
                direction = curDirection;
            }
            // for all other neighbours, we just assign direction of previous square, so pass in false boolean variable
            this.addValidNeighbour(currentLoc, false, queue);
        }
        // update the current direction of ghost and return direction
        this.currDirection = direction;
        return direction;
    }

    /*
    Helper method to determine whether neighbours are valid, given a current location. Pass in a boolean variable
    to change what happens when we assign a direction: for all secondary neighbours, just assign direction
    that was assigned to previous neighbour. Associate the queue initialised in BFS method.
     */
    private void addValidNeighbour(BoardCoordinate currentLoc, Boolean isInitial, Queue<BoardCoordinate> queue) {
        // stores row and column index for the location passed in as local variables
        int row = currentLoc.getRow();
        int col = currentLoc.getColumn();

        // to account for wrapping, only loop through directions when the square is within the board, avoids array out of bounds.
        if (col > 0 && col < 22) {
            /*
            We can loop through every value for direction stored in enum class, so that we can check every direction with one condition.
             */
            for (Direction direction : Direction.values()) {
                // a neighbour is valid if (1) not a wall, (2) not in opposite direction, (3) no direction has yet been assigned.
                if (this.maze[direction.newRow(row)][direction.newCol(col)].getColor() == Color.BLACK
                        && this.currDirection != direction.opposite()  && this.directions[direction.newRow(row)][direction.newCol(col)] == null) {
                    if (isInitial) {
                        this.directions[direction.newRow(row)][direction.newCol(col)] = direction;
                    } else {
                        this.directions[direction.newRow(row)][direction.newCol(col)] = this.directions[row][col];
                    }
                    // add the valid board coordinate to the queue
                    queue.add(new BoardCoordinate(direction.newRow(row), direction.newCol(col), true));
                }
            }
        }
    }

    /*
    Handles wrapping which is called earlier in BFS method to return the direction that ghost should continue
    to move in given its position in row and column. Ghost should only wrap when at the row where tunnel is located.
     */
    private Direction wrapGhost(int row, int col) {
        if (col == 0 && row == Constants.TUNNEL_ROW) {
            this.rect.setX(Constants.RIGHT_EXTREMA * Constants.SQ_WIDTH);
            return Direction.LEFT;
        }
        else if (col == Constants.RIGHT_EXTREMA && row == Constants.TUNNEL_ROW) {
            this.rect.setX(0);
            return Direction.RIGHT;
        }
        // if neither condition returns true, then ghost should continue to move in the same direction.
        return currDirection;
    }

    /*
    Method called in timeline to continuously generate direction for ghost movement. The ghost is removed from
    the square's array list to handle logical movement, then a direction is assigned through calling
    BFS or a random direction if in frightened mode, and the direction generated is passed into move method.
     */
    public void generateDirection(BoardCoordinate target) {
        // get ghost's initial location before making a move
        int row = this.getGhostRow();
        int col = this.getGhostCol();

        // as ghosts moves, remove the ghost from array list of collideables from current square
        this.maze[row][col].removeCollideable(this);

        Direction direction;
        if (this.game.getFrightMode()) {
            // in Frightened mode, move in a random direction and change ghost colour to blue
            direction = this.generateRandomDirection(row, col);
            this.rect.setFill(Color.BLUE);
        }
        else { // in scatter or chase mode, move with BFS and return to original property colour
            direction = this.ghostBFS(target);
            this.rect.setFill(color);
        }
        // update ghost's direction and call move method to move in this direction.
        this.currDirection = direction;
        this.moveGhost(direction);

        // get ghost's new location after completing its move
        row = this.getGhostRow();
        col = this.getGhostCol();
        // then, add the ghost to array list of collideables of new square it has just moved into. This enforces ghost collisions
        this.maze[row][col].addCollideable(this);
    }

    /*
    Handles ghost's graphical movement using a switch statement to set the location given a direction when
    the method is called.
     */
    private void moveGhost(Direction direction){
    // ensures no null pointer exception error
        if(direction!=null) {
            switch (direction) {
                case UP:
                    this.rect.setY(this.rect.getY() - Constants.PACMAN_SPEED);
                    break;
                case DOWN:
                    this.rect.setY(this.rect.getY() + Constants.PACMAN_SPEED);
                    break;
                case LEFT:
                    this.rect.setX(this.rect.getX() - Constants.PACMAN_SPEED);
                    break;
                case RIGHT:
                    this.rect.setX(this.rect.getX() + Constants.PACMAN_SPEED);
                    break;
            }
        }
    }

    /*
    Separate method that handles generating a random direction to move in when status of game changes to frightened mode.
    Current location is passed as row and column, and valid directions for movement at that position are stored in array list.
    If a direction is valid, then store in array list and select a random index to return a valid direction.
     */
    private Direction generateRandomDirection(int row, int col) {
        ArrayList<Direction> validDirections = new ArrayList<>();
        if (col > 0 && col < 22) {
            for (Direction direction : Direction.values()) {
                if (this.maze[direction.newRow(row)][direction.newCol(col)].getColor() == Color.BLACK &&
                        this.currDirection != direction.opposite()) {
                    validDirections.add(direction);
                }
            }
        }
        else { return this.wrapGhost(row, col);} // accounts for wrapping, so a direction is still returned outside maze
        /*
         Randomly choose one of those valid directions for the Ghost to move in
         (if there is only one, the Ghost should move in that direction).
         */
        int rand_index = (int) (Math.random() * validDirections.size());
        // access direction stored at random index in array list and update the current direction
        Direction direction = validDirections.get(rand_index);
        this.currDirection = direction;
        return direction;
    }

    /*
    Sets the circle shape to front in order for pacman to show up in front of collideable objects (dots)
    */
    public void bringToFront(){
        this.rect.toFront();
    }
}

