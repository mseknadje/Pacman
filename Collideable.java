package pacman;

/**
 * Interface which is implemented by the dots, ghosts, energizers.
 * The dots, ghosts, energizers share the need and property of being able to collide with pacman
 * but act upon collision in different ways.
 *
 * The collide method is declared here, but each collideable object defines its own collide method to
 * add the score, disappear when eaten, or even change the game mode.
 */
public interface Collideable {
    void collide();
}
