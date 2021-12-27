package pacman;

import cs15.fnl.pacmanSupport.CS15SquareType;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.Queue;
import java.util.LinkedList;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

/**
 * Handles the high-level logic of the game, by running a timeline that updates
 * the pacman, ghost pen, and status of the game at each tick of the timeline and by updating the
 * pacman direction of key input.
 */
public class Game {

    private Pane gamePane;
    private Timeline timeline;
    private MazeSquare[][] maze;
    private Pacman pacman;
    private Ghost inky;
    private Ghost blinky;
    private Ghost clyde;
    private Ghost pinky;
    // Keeps track of direction pacman currently moves in, which is passed into method when a key is pressed
    private Direction direction;
    private int score;
    private Label scoreLabel;
    private Label livesLabel;
    // Multiple counters to account for the pen, game modes, because they operate independently of another.
    private double counterPen;
    private int countLives;
    private int counter;
    private int countFright;
    private Queue<Ghost> ghostPen;
    private int dotCount;
    // Boolean keeps track of the status of game when frightened mode is on or off.
    public boolean frightMode;

    /*
     * Sets up the game by registering the KeyEvent handler on the gamePane, initialising conditions for the whole
     * game (lives, score), instantiating the 2D array that represents the maze, and calling the timeline.
     */
    public Game(Pane gamePane, Label score, Label lives) {
        this.gamePane = gamePane;
        this.scoreLabel = score;
        this.livesLabel = lives;
        this.countLives = 3;
        this.score = 0;
        this.maze = new MazeSquare[Constants.MAZE_DIMENSION][Constants.MAZE_DIMENSION];
        this.PacmanTimeline();
        this.start();
    }

    /*
    Initialises game state for player to continue playing a new round whilst keeping track of score and lives.
    Resets the ghostpen, the counters, moves ghosts to starting positions, reset fright mode, and play the timeline.
     */
    private void start() {
        this.ghostPen = new LinkedList<>();
        this.counterPen = 0;
        this.countFright = 0;
        this.dotCount = 0;
        this.counter = 0;
        this.frightMode = false;
        this.setUpMap();
        // set initial direction of pacman towards an invalid square, so pacman is stationary when game starts.
        this.direction = Direction.UP;
        this.gamePane.setFocusTraversable(true);
        this.timeline.play();
    }

    /*
    Handles resetting the game after pacman loses a life by calling the start method and updating remaining lives.
     */
    public void reset() {
        this.countLives -= 1;
        this.start();
        this.timeline.play();
    }

    /*
    Registers key event handlers and sets up timeline, and updates the state of the game by calling update game
    method in each timeline cycle that is set to play indefinitely.
     */
    private void PacmanTimeline() {
        this.gamePane.setOnKeyPressed(this::handleKeyPress);
        KeyFrame kf = new KeyFrame(Duration.seconds(Constants.TIMELINE_DURATION),
                (ActionEvent event) -> this.updateGame());
        this.timeline = new Timeline(kf);
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }

    /*
     * Updates the state of the game, by releasing ghosts from pen, moving pacman, updating the score, checking for
     * collisions before and after we tell the ghosts to move (accounting for head on edge cases), and updating the lives
     * when necessary.
     */
    private void updateGame() {
        this.initialisePen();
        this.pacman.movePacman(this.direction);
        this.collide();
        this.switchGameMode();
        this.collide();
        this.updateLives();
    }

    /*
    Helper method to set up the maze as a 2D array of mazequares graphically and logically.
     */
    private void setUpMaze() {
        for (int row = 0; row < Constants.MAZE_DIMENSION; row++) {
            for (int col = 0; col < Constants.MAZE_DIMENSION; col++) {
                this.maze[row][col] = new MazeSquare(this.gamePane);
                this.maze[row][col].setRow(row * Constants.SQ_WIDTH);
                this.maze[row][col].setCol(col * Constants.SQ_WIDTH);
            }
        }
    }

    /*
    Configures the graphical and logical setup of the game using the support map to determine the locations to
    place all the elements of the game (wall, paths, dots, energizers, ghosts' and pacmans' starting locations).
     */
    private void setUpMap() {
        this.setUpMaze();
        // store support map as local variable
        CS15SquareType[][] supportMap = cs15.fnl.pacmanSupport.CS15SupportMap.getSupportMap();
        for (int row = 0; row < Constants.MAZE_DIMENSION; row++) {
            for (int col = 0; col < Constants.MAZE_DIMENSION; col++) {
                // enum given by support map determines what type of object to instantiate at given row and col
                CS15SquareType type = supportMap[row][col];
                switch (type) {
                    case WALL:
                        this.maze[row][col].setColor(Constants.WALL_COLOR);
                        break;
                    case DOT:
                        // adds object graphically
                        Dot dot = new Dot((col * Constants.SQ_WIDTH) + Constants.SQ_OFFSET,
                                (row * Constants.SQ_WIDTH) + Constants.SQ_OFFSET, this.gamePane, this);
                        this.maze[row][col].addCollideable(dot); // adds object logically to smart square
                        break;
                    case ENERGIZER:
                        Energizer energizer = new Energizer((col * Constants.SQ_WIDTH) + Constants.SQ_OFFSET,
                                (row * Constants.SQ_WIDTH) + Constants.SQ_OFFSET, this.gamePane, this);
                        this.maze[row][col].addCollideable(energizer);
                        break;
                    case PACMAN_START_LOCATION:
                        this.pacman = new Pacman(this.gamePane, this);
                        this.pacman.setPacmanLoc((col * Constants.SQ_WIDTH) + Constants.SQ_OFFSET,
                                (row * Constants.SQ_WIDTH) + Constants.SQ_OFFSET);
                        break;
                    case GHOST_START_LOCATION:
                        /*
                        Instantiate 4 instances of ghost to create the unique ghosts, adding them to ghostPen queue to
                        initialise the ghostPen.
                         */
                        this.inky = new Ghost(this.gamePane, Color.ORANGE, this);
                        this.inky.setGhostLoc((col * Constants.SQ_WIDTH),
                                (row * Constants.SQ_WIDTH));
                        this.maze[row][col].addCollideable(this.inky);
                        this.ghostPen.add(this.inky);

                        this.clyde = new Ghost(this.gamePane, Color.RED, this);
                        this.clyde.setGhostLoc((col * Constants.SQ_WIDTH),
                                (row * Constants.SQ_WIDTH) - Constants.CLYDE_OFFSET);
                        this.maze[row][col].addCollideable(this.clyde);

                        this.pinky = new Ghost(this.gamePane, Color.PINK,this);
                        this.pinky.setGhostLoc((col * Constants.SQ_WIDTH) - Constants.PINKY_OFFSET,
                                (row * Constants.SQ_WIDTH));
                        this.maze[row][col].addCollideable(this.pinky);
                        this.ghostPen.add(this.pinky);

                        this.blinky = new Ghost(this.gamePane, Color.CYAN,this);
                        this.blinky.setGhostLoc((col * Constants.SQ_WIDTH),
                                (row * Constants.SQ_WIDTH) + Constants.SQ_WIDTH);
                        this.maze[row][col].addCollideable(this.blinky);
                        this.ghostPen.add(this.blinky);
                        break;
                }
            }
        }
        this.toFront(); // calls method to set relevant objects to front after setting up background elements
    }

    /*
    Helper method to set all the moving objects graphically in front of the secondary elements (dots, energizers).
     */
    private void toFront() {
        this.pacman.bringToFront();
        this.inky.bringToFront();
        this.blinky.bringToFront();
        this.pinky.bringToFront();
        this.clyde.bringToFront();
    }

    /*
     * Handles key input by changing direction of pacman on up, down, left, and right arrow keys.
     * Tracks direction currently moving in, so continues moving if direction from key input is invalid.
     */
    private void handleKeyPress(KeyEvent e) {
        KeyCode keyPressed = e.getCode();
        switch (keyPressed) {
            case LEFT:
                this.direction = this.pacman.setMoveDirection(Direction.LEFT, this.direction);
                break;
            case RIGHT:
                this.direction = this.pacman.setMoveDirection(Direction.RIGHT, this.direction);
                break;
            case DOWN:
                this.direction = this.pacman.setMoveDirection(Direction.DOWN, this.direction);
                break;
            case UP:
                this.direction = this.pacman.setMoveDirection(Direction.UP, this.direction);
                break;
        }
    }

    /*
    Method called in timeline to continuously check for any pacman collisions with collideable objects.
    When Pacman enters a square, iterate over that square's arraylist and tell each element to perform its collision action.
     */
    private void collide() {
        if (this.pacman.getCol() <= Constants.RIGHT_EXTREMA) {
            MazeSquare currSquare = this.maze[this.pacman.getRow()][this.pacman.getCol()]; // gets square at current location
            ArrayList<Collideable> arrayList = currSquare.getCollideables();
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).collide();
                currSquare.clearArray();
            }
        }
    }

    /*
    Sets the target location for ghost behaviour which must change depending on current game mode, by
    calling direction generation on each instance of ghost and passing in pacman's current location.
    Only BFS requires a target, so in frightened mode a null target is passed whilst still calling the ghosts to move.
     */
    private void setGhostTarget(GameMode mode) {
        int row = this.pacman.getRow();
        int col = this.pacman.getCol();
        switch(mode) {
            case CHASE:
                this.inky.generateDirection(new BoardCoordinate(row - Constants.INKY_ROW, col, false));
                this.clyde.generateDirection(new BoardCoordinate(row, col, false));
                this.pinky.generateDirection(new BoardCoordinate(row + 1, col - Constants.PINKY_COL, false));
                this.blinky.generateDirection(new BoardCoordinate(row, col + Constants.BLINKY_COL, false));
                break;
            case SCATTER:
                this.inky.generateDirection(new BoardCoordinate(1, 1, false));
                this.clyde.generateDirection(new BoardCoordinate(Constants.RIGHT_EXTREMA, 1, false));
                this.pinky.generateDirection(new BoardCoordinate(1, Constants.RIGHT_EXTREMA, false));
                this.blinky.generateDirection(new BoardCoordinate(Constants.RIGHT_EXTREMA, Constants.RIGHT_EXTREMA, false));
                break;
            case FRIGHTENED:
                this.inky.generateDirection(null);
                this.clyde.generateDirection(null);
                this.pinky.generateDirection(null);
                this.blinky.generateDirection(null);
        }
    }

    /*
    Handles switching between game modes by incrementing a counter with timeline and tracking time
    when game must switch mode: 10 seconds chase, 7 seconds scatter repeatedly. First checks whether game
    is in frightened mode and if so, sets off counter for frightened mode. When frightened mode is over,
    set the fright mode boolean to false.
     */
    private void switchGameMode() {
        if (!this.frightMode) { // determine whether game is in frightened mode
            if (this.counter < Constants.CHASE_LIMIT) {
                this.setGhostTarget(GameMode.CHASE);
                this.counter ++;
            }
            if (this.counter >= Constants.CHASE_LIMIT) {
                this.setGhostTarget(GameMode.SCATTER);
                this.counter ++;
            }
            if (this.counter >= Constants.SCATTER_LIMIT) {
                this.counter = 0;}
        }
        else {
            this.countFright ++;
            this.setGhostTarget(GameMode.FRIGHTENED);
            if (this.countFright >= Constants.FRIGHTENED_LIMIT) {
                this.setFrightMode(false); // set frightened mode to false after 7 seconds
                this.countFright = 0; // reset counter
            }
        }
    }

    /*
    Setter method that assigns truth value to frightMode instance variable change game to frightened mode.
     */
    public void setFrightMode(Boolean isOn) {
        this.frightMode = isOn;
    }

    /*
    Getter method that returns truth assignment of frightMode to track whether game is in frightened mode.
     */
    public Boolean getFrightMode() {
        return this.frightMode;
    }

    /*
    Getter method called in ghost and pacman classes to return associated 2D Array maze set up in game class.
     */
    public MazeSquare[][] returnMazeArray() {
        return this.maze;
    }

    /*
    Handles configuration of ghost pen with counter that pops out the instance of first ghost of the linkedlist,
    takes this instance to move it inside the pen graphically and resets the counter after each instance.
     */
    private void initialisePen() {
        this.counterPen += Constants.TIMELINE_DURATION;
        if (this.counterPen >= Constants.PEN_COUNT && !this.ghostPen.isEmpty()) {
            Ghost ghost = this.ghostPen.remove();
            ghost.setGhostLoc(Constants.OUTSIDE_PEN_X, Constants.OUTSIDE_PEN_Y);
            this.counterPen = 0;
        }
    }

    /*
    Public method to add a ghost object to queue when pacman eats ghost in frightened mode, adding the ghost to ghost
    pen graphically and logically.
     */
    public void addToPen(Ghost ghost) {
        this.ghostPen.add(ghost);
        ghost.setGhostLoc(Constants.INSIDE_PEN_X, Constants.INSIDE_PEN_Y);
        this.maze[Constants.INSIDE_PEN_Y/Constants.SQ_WIDTH][Constants.INSIDE_PEN_X/Constants.SQ_WIDTH].addCollideable(ghost);
    }

    /*
    Handles game over set up by stopping timeline and creating graphical set up for label, passing
    in text for label ("winner!" or "game over") as a parameter.
     */
    private void gameOver(String labelText) {
        this.timeline.stop();
        Label label = new Label(labelText);
        VBox labelBox = new VBox(label);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setPrefHeight(this.gamePane.getHeight());
        labelBox.setPrefWidth(this.gamePane.getWidth());
        label.setStyle("-fx-font: italic bold 50px arial, serif;-fx-text-alignment: center;-fx-text-fill: rgba(255,251,0,0.87);");
        this.gamePane.getChildren().add(labelBox);
    }

    /*
    Updates the count of eaten dots/energizers to track winning status of game
    and updates the score label by incrementing total score
     */
    public void updateScore(int dot, int score) {
        this.dotCount += dot;
        this.score += score;
        this.scoreLabel.setText(Constants.SCORE_LABEL_TEXT + this.score);
    }

    /*
    Continuously updates amount of lives remaining and calls end of game when either (1) player wins by eating all items
    or (2) game over when player loses all 3 lives.
     */
    private void updateLives() {
        this.livesLabel.setText(Constants.LIVES_LABEL_TEXT + this.countLives);
        if (this.dotCount == Constants.TOTAL_DOTS) {
            this.gameOver(Constants.GAME_WON_LABEL_TEXT);
        }
        if (this.countLives == 0) {
            this.gameOver(Constants.GAME_OVER_LABEL_TEXT);
        }
    }
}