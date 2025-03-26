import java.awt.GraphicsDevice.WindowTranslucency.*;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.geometry.Pos;


/**
 * Overlay scene that occurs when a player presses esc (pauses) during the game.
 */
public class PauseScreen extends BaseScreen {
    
    private Stage overlayStage;
    private Button resumeBtn;
    private Button exitBtn;
    private VBox overlayRoot;
    private Scene overlayScene;
    private GameScreen gameScreen;
    
    /**
     * Create a semi-transparent pause overlay with resume and exit option.
     */
    public PauseScreen(GameManager gameManager, GameScreen gameScreen, int width, int height) {
        super(gameManager, width, height);
        this.gameScreen = gameScreen;
        overlay();
    }
    
    /**
     * Sets up a transparent overlay stage.
     */
    private void overlay(){
        
        overlayStage = new Stage();
        overlayStage.initStyle(StageStyle.TRANSPARENT); 
        overlayStage.initModality(Modality.APPLICATION_MODAL); //hides input on game stage 
        overlayStage.initOwner(gameManager.getStage()); //sets the parent of the pause screen to be the main game screen
        
        overlayRoot = new VBox(10);
        overlayRoot.getStyleClass().add("overlay_root");
        overlayRoot.setPadding(new javafx.geometry.Insets(50));
        overlayRoot.setMouseTransparent(false);
        overlayRoot.setAlignment(Pos.CENTER);
        
        overlayScene = new Scene(overlayRoot, width, height, Color.TRANSPARENT); 
        
        setContent();
        
        overlayScene.getStylesheets().add(getClass().getResource("/format.css").toExternalForm());
        overlayStage.setScene(overlayScene);
    }
    
    /**
     * Adds buttons + event handlers.
     */
    @Override
    protected void setContent(){
        
        resumeBtn = new Button("Resume");
        exitBtn = new Button("Main Menu");
        
        //dealing with button/escape from inside this stage
        resumeBtn.setOnAction(event -> {
            hidePauseScreen();
            gameScreen.changePauseTimer();
            gameScreen.resumeCountdown();
        });
        
        overlayScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                    hidePauseScreen();
                    gameScreen.changePauseTimer();
                    gameScreen.resumeCountdown();
                }
        });
        
        exitBtn.setOnAction(event -> {
            hidePauseScreen();
            gameManager.showTitleScreen(false);
        } );

        overlayRoot.getChildren().addAll(resumeBtn, exitBtn);
   
    }    
 
    /**
     * Displays the pause overlay.
     */
    public void showPauseScreen(){
        overlayOverMainWindow();
        overlayStage.show();
    }
    
    /**
     * Hides the pause overlay.
     */
    private void hidePauseScreen(){
        overlayStage.hide();
    }
    
    /**
     * Positions the overlay to match with the game window.
     */
    private void overlayOverMainWindow() {
        Stage mainStage = gameManager.getStage();
        overlayStage.setX(mainStage.getX());
        overlayStage.setY(mainStage.getY());
    } 
}