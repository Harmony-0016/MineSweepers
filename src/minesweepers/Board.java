package minesweepers;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Board extends Application {

    //Number of NON-MINE Squares
    private int totalNonMines;
    public int revealedNonMines;
    private GridPane grid;

    //creates a new difficulty tab
    private Difficulty difficulty;
    private int[][] minefield; // Minefield (0 = no mine, 1 = mine)
    private int[][] adjacentMines; // Adjacent mines count for each tile

    private Square[][] squareArray;
    
    // FIX: Store the Stage at the class level so Alert dialogs can restart the game safely
    private Stage primaryStage; 

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.difficulty = Difficulty.EASY;
        Initialize();
    }

    // Method to initialize the game (reset the minefield and game state)
    private void Initialize() throws Exception {
        // FIX: Reset the revealed squares counter so new games work properly
        this.revealedNonMines = 0; 
        
        squareArray = new Square[difficulty.getRows()][difficulty.getCols()];

        // Initialize the grid and game state
        if (this.primaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null");
        }

        //creates the dimenstions of the minefield
        this.minefield = new int[difficulty.getRows()][difficulty.getCols()]; 
        this.adjacentMines = new int[difficulty.getRows()][difficulty.getCols()]; 

        // Place mines and calculate adjacent mines
        placeMines();
        adjacentsMines();
        nonMineTileCounter();

        // Create the grid
        gridCreation();

        // Create the scene and add the grid to the scene
        Scene scene = new Scene(grid); 
        this.primaryStage.setTitle("Minesweeper");
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    public void revealSquare(int row, int col) {
        if (row < 0 || row >= difficulty.getRows() || col < 0 || col >= difficulty.getCols()) {
            return;
        }

        Square square = squareArray[row][col];

        // FIX: Do not recursively reveal squares that are already revealed or flagged
        if (square.isRevealed() || square.getState() == 1) {
            return;
        }

        int adjacentCount = adjacentMines[row][col];

        square.reveal();
        revealedNonMines++;

        if (adjacentCount == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        revealSquare(row + i, col + j);
                    }
                }
            }
        } 
    }

    // Method to check if the player has won
    private void checkWinCondition() {
        if (revealedNonMines == totalNonMines) {
            showAlert("You Win!", "All non-mine tiles revealed!");
        }
    }

    // Helper method to show an alert when the game is over or won
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Add a "New Game" button and handle event
        ButtonType newGameButton = new ButtonType("New Game");
        alert.getButtonTypes().setAll(newGameButton, ButtonType.CANCEL);

        alert.showAndWait().ifPresent(response -> {
            if (response == newGameButton) {
                try {
                    Initialize(); // FIX: Call initialize directly without relying on alert.getOwner()
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void placeMines() {
        int minesPlaced = 0;
        Random random = new Random(); // FIX: Moved outside the while loop for better performance

        // Loop until the required number of mines are placed
        while (minesPlaced < this.difficulty.getMines()) {
            int row = random.nextInt(this.difficulty.getRows());
            int col = random.nextInt(this.difficulty.getCols());
            if (this.minefield[row][col] == 0) {
                this.minefield[row][col] = 1; // Place a mine if the cell is empty
                minesPlaced++;
            }
        }
    }

    private void adjacentsMines() {
        for (int row = 0; row < this.difficulty.getRows(); row++) {
            for (int col = 0; col < this.difficulty.getCols(); col++) {
                if (minefield[row][col] == 1) {
                    adjacentMines[row][col] = 9; // 9 represents a mine in the adjacentMines array
                } else {
                    int adjacentMineCount = 0;
                    // Check all 8 neighboring cells
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int newRow = row + i;
                            int newCol = col + j;

                            if (newRow >= 0 && newRow < difficulty.getRows() && newCol >= 0 && newCol < difficulty.getCols()) {
                                if (minefield[newRow][newCol] == 1) {
                                    adjacentMineCount++;
                                }
                            }
                        }
                    }
                    adjacentMines[row][col] = adjacentMineCount;
                }
            }
        }
    }

    private void nonMineTileCounter() {
        totalNonMines = 0;
        for (int row = 0; row < this.difficulty.getRows(); row++) {
            for (int col = 0; col < this.difficulty.getCols(); col++) {
                if (minefield[row][col] != 1) {
                    totalNonMines++;
                }
            }
        }
    }

    private void gridCreation() {
        grid = new GridPane(); 

        grid.setVgap(1);
        grid.setHgap(1);

        for (int row = 0; row < difficulty.getRows(); row++) {
            for (int col = 0; col < difficulty.getCols(); col++) {
                Square square = new Square(); 

                int adjacentCount = adjacentMines[row][col];
                if (adjacentCount > 0) {
                    square.setValue((char) ('0' + adjacentCount)); 
                } else {
                    square.setValue(Square.Blank); 
                }

                final int r = row; 
                final int c = col;

                square.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (square.isRevealed() || square.getState() == 1) {
                            return;
                        }
                        if (square.getValue() == Square.Mine) {
                            square.reveal();
                            showAlert("Game Over", "You hit a mine!");
                        } else {
                            revealSquare(r, c);
                        }

                        checkWinCondition();
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        square.onRightClick(event);
                    }
                });

                grid.add(square, col, row);
                squareArray[row][col] = square;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}