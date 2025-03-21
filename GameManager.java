import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the controller which handles and aggreates all aspects of the program
 *
 */
public class GameManager
{
    private final Stage stage;
    private final TitleScreen titleScreen;
    private final GameOverScreen gameOverScreen;
    
    private GameScreen gameScreen;    
    private PauseScreen pauseScreen;
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
        if (resume == false || (resume == true && gameScreen.isCompleted())) {
            gameScreen.reset();
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
