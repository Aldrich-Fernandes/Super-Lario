
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the game.
 * 
 * Can be later used to intialise game settings or loop (can be intgreated into GameManager)
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Main extends Application
{

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
       GameManager gameManager = new GameManager(stage);
       gameManager.showTitleScreen();
    }

    public static void main(String[] args){
        launch(args);
    }
}
