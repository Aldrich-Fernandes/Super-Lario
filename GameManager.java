
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
public class GameManager extends Application
{
    private Stage stage;


    /**
     * The start method is the main entry point for every JavaFX application. 
     * It is called after the init() method has returned and after 
     * the system is ready for the application to begin running.
     *
     * @param  stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage)
    {
        this.stage = stage;
        stage.setTitle("SUPER LARIO");
        
        TitleScreen titleScreen = new  TitleScreen();
        
        Scene introScene = titleScreen.getScene();
        
        stage.setScene(introScene);
        
        stage.show();
        
    }    
}
