import javafx.application.Application;
import javafx.geometry.Insets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * This is the controller which handles and aggreates all aspects of the program
 *
 */
public class GameManager
{
    private Stage stage;
    private TitleScreen titleScreen;
    private GameScreen gameScreen;    
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;
    private Scene gameScene;
    

    public GameManager(Stage stage)
    {
        // Creating the stage
        this.stage = stage;
        stage.setTitle("SUPER LARIO");
        stage.setResizable(false); // avoid resizing the game needlessly
        stage.centerOnScreen();
        
        // Other Components
        titleScreen = new TitleScreen(this, 600, 600);
        gameOverScreen = new GameOverScreen(this, 600, 600);
        gameScreen = new GameScreen(this, 900, 690);
        pauseScreen = new PauseScreen(this, gameScreen, 900, 690);
    }    
    
    public void startGame(boolean resume){
        if (resume == false) {
            gameScreen = new GameScreen(this, 900, 690);
            pauseScreen = new PauseScreen(this, gameScreen, 900, 690);
            gameScene = gameScreen.getScene();
            changeScene(gameScene);
            gameScreen.startCountdown();
        }
        else if (resume == true) {
            gameScreen.changePauseTimer();
            gameScreen.resumeCountdown();
            changeScene(gameScene); 
        }
    }
    
    public void showTitleScreen(){
        changeScene(titleScreen.getScene());
    }
    
    public void showGameOverScreen(boolean win, int score, String comment){
        gameOverScreen.displayOutcome(win, score, comment);
        changeScene(gameOverScreen.getScene());
    }
    
    public void changeScene(Scene scene){
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    
    public Stage getStage(){
        return stage;
    }
    
    public void pauseGame(){
        pauseScreen.showPauseScreen();
    }
}
