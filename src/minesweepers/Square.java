package minesweepers;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Square extends Button {

    public static final char Mine = '9'; 
    public static final char Blank = ' '; 
    private boolean revealed = false; 
    private char value = Blank; 

    private static final Image imgMine = new Image("/images/mine.png"); 
    private static final Image imgFlag = new Image("/images/flag.png"); 
    private static final Image imgQuestion = new Image("/images/question.png"); 

    private int state = 0; // 0 = Blank, 1 = Flag, 2 = Question Mark

    public Square() {
        this.revealed = false;
        this.setPrefSize(35, 35); 
        this.setStyle("-fx-font-size: 20px; -fx-background-color: lightgray;");
    }

    public void reveal() {
        this.revealed = true;

        if (value == Mine) {
            setGraphic(new ImageView(imgMine));
        } else if (value == Blank) {
            setText("");
            // FIX: Changed font size from 36px to 20px to prevent the square from resizing
            setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: whitesmoke;");
        } else {
            setText(String.valueOf(value));
            // FIX: Changed font size from 36px to 20px 
            setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
            setGraphic(null);
        }

        this.requestLayout();
    }

    public void onRightClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY && !revealed) {
            state = (state + 1) % 3;
            updateState();
        }
    }

    private void updateState() {
        switch (state) {
            case 0:
                setGraphic(null);
                setText("");
                setStyle("-fx-background-color: lightgray;");
                break;
            case 1:
                setGraphic(new ImageView(imgFlag));
                setText("");
                setStyle("-fx-background-color: lightgray;");
                break;
            case 2:
                setGraphic(new ImageView(imgQuestion));
                setText("");
                setStyle("-fx-background-color: lightgray;");
                break;
        }
        this.requestLayout();
    }

    public boolean isRevealed() {
        return this.revealed;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return this.value;
    }
    
    public int getState() {
        return this.state;
    }

    public void reset() {
        this.revealed = false;
        setText("");
        setGraphic(null);
        setStyle("-fx-background-color: lightgray;");
        state = 0;
    }
}