
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

    public GameManager(Stage stage)
    {
        // Creating the stage
        this.stage = stage;
        stage.setTitle("SUPER LARIO");
        
        // Other Components
        titleScreen = new TitleScreen(this, 600, 600);
        gameScreen = new GameScreen(this, new Player(100, 100), 800, 600);
    }    
    
    public void startGame(){
        changeScene(gameScreen.getScene());
    }
    
    public void showTitleScreen(){
        changeScene(titleScreen.getScene());
    }
    
    public void changeScene(Scene scene){
        stage.setScene(scene);
        stage.show();
    }
}
