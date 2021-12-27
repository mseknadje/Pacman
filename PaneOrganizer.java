package pacman;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

/**
 * Organizes the JavaFX graphical structure of the program. It
 * creates the root BorderPane with the gamePane in the center and a bottomPane
 * with Quit button and two labels.
 */
public class PaneOrganizer {

    private BorderPane root;
    /*
     * Constructs the PaneOrganizer and does the graphical setup.
     */
    public PaneOrganizer() {
        this.root = new BorderPane();

        Pane gamePane = new Pane();
        this.root.setCenter(gamePane);

        Pane bottomPane = this.createBottomPane();
        this.root.setBottom(bottomPane);

        Label score = new Label(Constants.SCORE_LABEL_TEXT + 0);
        bottomPane.getChildren().add(score);

        Label lives = new Label(Constants.LIVES_LABEL_TEXT);
        bottomPane.getChildren().add(lives);

        /*
         instantiate the game, associating the gamepane, score, lives labels whose values are added
         and tracked in the game class as the status of the game changes.
         */
        new Game(gamePane, score , lives);
    }

    /*
     * Creates and styles the bottom pane as an HBox with a quit button.
     */
    private HBox createBottomPane() {
        HBox lowerPane = new HBox();
        lowerPane.setAlignment(Pos.CENTER);
        lowerPane.setSpacing(Constants.SCORE_PANE_SPACING);
        lowerPane.setPrefHeight(Constants.SCORE_PANE_HEIGHT);
        Button quit = new Button("Quit");
        quit.setFocusTraversable(false);
        quit.setOnAction(ActionEvent -> { Platform.exit(); } );
        lowerPane.getChildren().add(quit);
        return lowerPane;
    }

    /*
     * Gets the root of the program.
     */
    public BorderPane getRoot() {
        return this.root;
    }
}