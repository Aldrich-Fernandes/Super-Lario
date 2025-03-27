import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller which handles all game screens and transitions.
 */
public class GameManager {
    private final Stage stage;
    private final TitleScreen titleScreen;
    private final GameOverScreen gameOverScreen;
    private final GameScreen gameScreen;    
    private final PauseScreen pauseScreen;
    
    private Scene gameScene;
    
    
    /**
     * Initialize the game manager with all required screens.
     */
    public GameManager(Stage stage) {
        // Create the stage
        this.stage = stage;
        stage.setTitle("SUPER LARIO");
        stage.setResizable(false); // avoid resizing the game needlessly
        stage.centerOnScreen();
        
        // Initialize game screens with their respective resolutions.
        titleScreen = new TitleScreen(this, 600, 600);
        gameOverScreen = new GameOverScreen(this, 600, 600);
        gameScreen = new GameScreen(this, 900, 690);
        pauseScreen = new PauseScreen(this, gameScreen, 900, 690);
    }    
    
    /**
     * Start or resume the game (depending on if we paused the game and are trying to get back in or launching for the first time) . 
     */
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
    
    /**
     * Displays the title scene.
     */
    public void showTitleScreen(){
        changeScene(titleScreen.getScene());
    }
    
    /**
     * Displays the title scene. ( Overloaded method that includes a boolean for whether the resume button is disabled).
     */
    public void showTitleScreen(boolean resumeDisabled){
        titleScreen.setResumeDisable(resumeDisabled);
        changeScene(titleScreen.getScene());
    }
    
    /**
     * Displays a scene of when the game has ended (will include win/loss status and score in the winning case).
     */
    public void showGameOverScreen(boolean win, int score, String comment){
        gameOverScreen.displayOutcome(win, score, comment);
        changeScene(gameOverScreen.getScene());
    }
    
    /**
     * Handles scene changes and centers it on the scene.
     */
    public void changeScene(Scene scene){
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    
    /**
     * An accessor for the main stage of the application.
     */
    public Stage getStage(){
        return stage;
    }
    
    /**
     * Pauses the game and shows the pause scene.
     */
    public void pauseGame(){
        pauseScreen.showPauseScreen();
    }
}
