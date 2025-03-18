import java.awt.GraphicsDevice.WindowTranslucency.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;


/**
 * The pause screen that occurs when a player presses esc during the game
 */

public class PauseScreen extends BaseScreen
{
    
    private Stage overlayStage;
    private Button resumeBtn;
    private Button exitBtn;
    private VBox overlayRoot;
    private Scene overlayScene;
    private GameScreen gameScreen;

    public PauseScreen(GameManager gameManager, GameScreen gameScreen, int width, int height)
    {
        super(gameManager, width, height);
        this.gameScreen = gameScreen;
        overlay();
    }
    
    private void overlay(){
        overlayStage = new Stage();
        overlayStage.initStyle(StageStyle.TRANSPARENT); 
        //hides input on game stage
        overlayStage.initModality(Modality.APPLICATION_MODAL); 
        //sets the parent of the pause screen to be the main game screen
        overlayStage.initOwner(gameManager.getStage());
        
        overlayRoot = new VBox(10);
        overlayRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlayRoot.setPadding(new javafx.geometry.Insets(50));
        overlayRoot.setMouseTransparent(false);
        
        overlayScene = new Scene(overlayRoot, super.getScene().getWidth(), super.getScene().getHeight(), Color.TRANSPARENT); 
        
        setContent();
        
               
        overlayScene.getStylesheets().add(getClass().getResource("/format.css").toExternalForm());
        overlayStage.setScene(overlayScene);
    }
    
    @Override
    protected void setContent(){
        
        //the buttons
        resumeBtn = new Button("Resume");
        exitBtn = new Button("Exit");
        
        //dealing with button/escape from inside this stage
        resumeBtn.setOnAction(event -> {
            gameScreen.resumeCountdown();
            hidePauseScreen();
            gameScreen.changePauseTimer();
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
            gameScreen.resetTimer();
            gameManager.showTitleScreen();
            
        } );

        
        overlayRoot.getChildren().addAll(resumeBtn, exitBtn);

        
    }    
    
    
    
    
    public void showPauseScreen(){
        overlayStage.show();
    }
    
    private void hidePauseScreen(){
        overlayStage.hide();
    }
    
}
