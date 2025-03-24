
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the game.
 */
public class Main extends Application
{
    /**
     * Will display our title screen
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
