import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;

/**
 * Main screen where the game is rendered and run.
 * 
 * // Will pass contain other paramters such as player and worldMap
 * 
 * IT will contain the entities in order to reder them within the same screen
 */
public class GameScreen extends BaseScreen
{    
    private int currentScene;
    private Player player;
    private Rectangle rect1;
    private Rectangle rect2;
    private Pane gamePane;
    
    public GameScreen(GameManager gameManager, Player player, int width, int height)
    {
        super(gameManager, width, height);
        this.player = player;
        setContent();
    }
    
    protected void setContent(){
        Label temp = new Label("SUPER LARIO");
        temp.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        
        // temporary Pane for game elements
        gamePane = new Pane();
        gamePane.setPrefSize(800, 500); 
        
        // two temp rectangles (collision testing)
        rect1 = new Rectangle(100, 300, 200, 30);
        rect1.setFill(Color.DARKGRAY);
        rect1.setStroke(Color.BLACK);
        
        rect2 = new Rectangle(400, 200, 150, 30);
        rect2.setFill(Color.DARKGRAY);
        rect2.setStroke(Color.BLACK);
        
        // position player on top of rect1
        player.setCenterX(rect1.getX() + 50);
        player.setCenterY(rect1.getY() - player.getRadius());
        
        gamePane.getChildren().addAll(rect1, rect2, player);
        
        root.getChildren().addAll(temp, gamePane);
    }

    @Override
    public Scene getScene() {
        Scene scene = super.getScene();
    
        scene.setOnKeyPressed(event -> handleKeyPress(event));
        scene.setOnKeyPressed(event -> handleKeyRelease(event));

        return scene;
    }

    /**
     * Handle key press events (forwards it to the player class)
     */
    private void handleKeyPress(KeyEvent event) {
        player.handleKeyPressed(event.getCode());
    }

    /**
     * Handle key release events (forwards it to the player class)
     */
    private void handleKeyRelease(KeyEvent event) {
        player.handleKeyReleased(event.getCode());
    }

}