package pacman;

import javafx.scene.paint.Color;

/**
 * Stores constants used throughout the program.
 */
public class Constants {

    // describes the constants in relation to the geometry of the map
    public static final int MAZE_DIMENSION = 23;
    public static final int SQ_WIDTH = 25;
    public static final double SQ_OFFSET = SQ_WIDTH/2;
    public static final int RIGHT_EXTREMA = 22;
    public static final int TUNNEL_ROW = 11;
    public static final int MIN_DISTANCE = 99999;
    public static final double CLYDE_OFFSET = 4 * Constants.SQ_OFFSET;
    public static final double PINKY_OFFSET = Constants.SQ_OFFSET*2;

    // describes the constants in relation to dimensions of the game
    public static final int SIDEBAR_HEIGHT = 60;
    public static final int GAME_PANE_HEIGHT = SQ_WIDTH * MAZE_DIMENSION;
    public static final int SCENE_HEIGHT = GAME_PANE_HEIGHT + SIDEBAR_HEIGHT;
    public static final int SCENE_WIDTH = SQ_WIDTH * MAZE_DIMENSION;
    public static final double SCORE_PANE_SPACING = 40;
    public static final double SCORE_PANE_HEIGHT = 60;

    // describes the constants in relation to size and properties of the objects
    public static final double DOT_RADIUS = 3;
    public static final double ENERGIZER_RADIUS = 6;
    public static final double PACMAN_RADIUS = 9;
    public static final Color DOT_COLOR = Color.WHITE;
    public static final Color ENERGIZER_COLOR = Color.WHITE;
    public static final int TOTAL_DOTS = 186;
    public static final Color WALL_COLOR = Color.DARKBLUE;

    // describes the constants used to set ghost targets in chase mode
    public static final int INKY_ROW = 4;
    public static final int PINKY_COL = 3;
    public static final int BLINKY_COL = 2;

    // describes the constants in relation to the timeline and speed
    public static final double TIMELINE_DURATION = 0.3;
    public static final double PACMAN_SPEED = 25;
    public static final double SCATTER_LIMIT = 17/Constants.TIMELINE_DURATION;
    public static final double FRIGHTENED_LIMIT = 7/Constants.TIMELINE_DURATION;
    public static final double CHASE_LIMIT = 10/Constants.TIMELINE_DURATION;

    // describes the constants in relation to the ghost pen
    public static final double PEN_COUNT = 5;
    public static final int OUTSIDE_PEN_X = 275;
    public static final int OUTSIDE_PEN_Y = 200;
    public static final int INSIDE_PEN_X = 250;
    public static final int INSIDE_PEN_Y = 250;

    // describes the constants in relation to the labels
    public static final String SCORE_LABEL_TEXT = "Score: ";
    public static final String LIVES_LABEL_TEXT = "Lives: ";
    public static final String GAME_OVER_LABEL_TEXT = "Game Over";
    public static final String GAME_WON_LABEL_TEXT = "Winner!";
    public static final int DOT_SCORE = 10;
    public static final int ENERGIZER_SCORE = 100;
    public static final int GHOST_SCORE = 200;

}
