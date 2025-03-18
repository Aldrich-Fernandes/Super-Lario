import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
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

    public GameManager(Stage stage)
    {
        // Creating the stage
        this.stage = stage;
        stage.setTitle("SUPER LARIO");
        stage.setResizable(false); // avoid resizing the game needlessly
        
        
        stage.setResizable(false);
        stage.centerOnScreen();
        
        // Other Components
        titleScreen = new TitleScreen(this, 600, 600);
        gameScreen = new GameScreen(this, 1200, 900);
<<<<<<< HEAD
=======
        pauseScreen = new PauseScreen(this,gameScreen, 600, 600);
>>>>>>> 6b1658a3ea8bbefffb1f17c12274911fc4d0fd86
    }    
    
    public void startGame(){
        changeScene(gameScreen.getScene());
        gameScreen.startCountdown();
    }
    
    public void showTitleScreen(){
        changeScene(titleScreen.getScene());
    }
    
    public void changeScene(Scene scene){
        stage.setScene(scene);
        stage.show();
    }
    
    public Stage getStage(){
        return stage;
    }
    
    public void pauseGame(){
        pauseScreen.showPauseScreen();
    }
}
