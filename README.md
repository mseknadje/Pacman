# Pacman
Final project 2021

Pacman README

Overview:
    My pacman project has 13 classes: App, PaneOrganizer, Constants, Game, GameMode, Direction, Ghost,
    Pacman, Dot, Energizer, MazeSquare, Collideable, BoardCoordinate, which interact in the following ways:

    - App class instantiates the top-level object, Pane Organizer, set ups the scene and creates the stage.

    - PaneOrganizer is the top-level graphical class that instantiates the Game class. The PaneOrganizer only includes
      high-level graphical elements such as the bottom pane and labels, and passes the gamePane, score and lives labels
      in the game class.

    - There are two enum classes: direction and GameMode. Direction represents the directions that objects can move in,
    and defines methods to return opposite directions and indexes of columns and rows which are used for simplifying implementing BFS.
    GameMode is an enum representing the ghost behaviour for different modes: scatter, chase, frightened.

    - Game class contains top-level logic for the game and contains 4 instances of ghost, the pacman, the maze represented
     by a 2D array of mazesquares, and sets up the timeline and key event handlers for moving pacman. Game contains methods
     to track the status of the game and continuously add the score, while periodically changing the game mode according to counters.

    - Ghost class contains the circle that represents the ghost and the logic that handles ghost movement with BFS.
    It is associated with the game and implements the collideable interface. It contains a linked list which is used for
    the BFS algorithm.

    - Pacman class contains the rectangle that represents pacman and the logic that relates to pacman's capabilities and properties.
    It is associated with the game and contains the methods called in the game class to move pacman after key input.

    - Dot class contains a circle and implements the collideable interface. It is associated with the game to add the score.

    - Energizer class contains a circle and implements the collideable interface. It is associated with the game to add
    the score and set Frightened mode to true.

    - MazeSquare is the wrapper class for the smart squares that form the maze. It contains a rectangle and the arraylist
    of collideable objects, for which public methods are defined in the class and called in the game class. It is associated
    with the game class.

    - Collideable is the interface that declares the methods used for collideable objects: dots, energizers, ghosts.

    - BoardCoordinate is the support class that is the immutable representation for maze coordinates.

    - Constants contains all relevant quantities used in throughout the game.


Design Choices:
    In the PaneOrganizer, I chose to use a BorderPan with a Pane to represent the main game and an HBox
    to hold the score, quit button, and lives label. I then need to associate the gamePane, score and lives
    labels with the game so it could add graphical elements to the game and update the score.

    I delegated to a MazeSquare class as a wrapper for one 'smart' square in the maze and exposed some important
    methods that change the contents of the arraylist (add, remove, clear). These methods are needed in order
    to add collideable objects such as ghost, dots, energizers when setting up the maze in the game class,
    as well as tracking the position of pacman and ghosts for collisions as they move.

    I chose to set up the maze in the game class, instead of creating its own wrapper class. This was necessary
    because all the elements of the game are instantiated through the positions indicated by the support map.
    The ghosts, pacman, dots, energizers are central to the high level logic of game, so they must be instantiated
    in the game class with the map.

    I have a Direction enum to track which direction (right/up/down/left) the ghost or pacman is moving.
    That enum also has methods to check the opposite direction and new row and column value when moving
    in that direction. These methods are used to simplify the logic for the BFS algorithm in the ghost class.

    I have a boolean (isFrightened) to change the mode of ghost behaviour to frightened. With the implementation
    of a setter and getter method to change and return the truth assignment of this variable, the counter in the
    game class can communicate with the ghost class. This simplifies the logic in the ghost class for adding the ghost
    back to the pen, changing the rectangle's colour, and generating a direction through the random generator (versus
    a complicated switch statement for each game mode that would be contained in the game class).

    I deliberately associate the dot, energizer, pacman, ghost with the game. This was necessary to communicate with
    the game class to return the 2D maze array set up in the game class and add to the score. Since each object is
    moving through the maze, they must know about the class in which the maze is created. Although this could also be
    implemented through an additional enum class or mutator method, this association avoids creating additional classes.

    I have multiple if statements to compose the logic for switching between game modes. The outside condition
    determines whether the game is in frightened mode, so the first nested statement indicates the movement for chase mode,
    which alternates with scatter. Because the game should alternate between chase and scatter indefinitely, an
    'else if' statement would prevent this and a switch statement would not be able to account inequalities.

    Additionally, because frightened mode can be triggered at any time, irrespectively of the counter for scatter and
    chase, its counter must be independent; so, there are two variables representing the different counters. Additionally,
    these counters are made instance variables to be initialised at the start of the game and increment with timeline.

    I have a switch statement to control the ghost movement and set the targets inside the game class. Although this
    could be contained inside the GameMode enum class, I deliberately define the statement in the game class to
    call the generateDirection method on each instance of ghost, which would otherwise require additional association
    with the enum class. Additionally, I set the target to null in the case of frightened mode, because the method that
    generates random movement for this mode is called inside the method already exposed to set the targets for other modes.

    I generates 4 new instances of ghost to represent the ghosts vs inheritance. Although each ghost is the same type of
    object and could inherit capabilities from a super class, it is more straightforward to contain all the logic such
    as the BFS algorithm in one class only. The unique characteristics of the ghost such as colour are passed in the
    constructor.

    I check for ghost wrapping inside the method for BFS to avoid a null exception and return a direction based on the
    ghost's current position, which ends the method before finding neighbours. This prevents trying to add valid neighbours
    when the ghost is off the map, which would create an array out of bounds exception error.

    I have an if statement inside the method for generating a direction in ghost class, which allows the program to
    a generate a random direction if in frightened mode or run BFS if not. No matter which game mode is on, the method
    will always return a direction for the ghost and pass this direction as a parameter for the move method (a switch
    statement).

Minor Bugs:
        Despite the fact that the program checks for collisions twice, before and after ghost movement, the game
        has an edge case where the pacman will move to the next square head-on before colliding with an object.
        This happens when a key is pressed at the same time that pacman moves into the next square.

Debugging Collaborators: tchandar

Hours Spent: This project took us approximately 100 hours to complete.
