import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;

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
    private AnimationTimer gameLoop;
    
    public GameScreen(GameManager gameManager, Player player, int width, int height)
    {
        super(gameManager, width, height);
        this.player = player;
        setContent();
        setGameLoop();
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
        player.setCenterY(rect1.getY() - 500);
        
        gamePane.getChildren().addAll(rect1, rect2, player);
        
        root.getChildren().addAll(temp, gamePane);
    }

    @Override
    public Scene getScene() {
        Scene scene = super.getScene();
    
        scene.setOnKeyPressed(event -> handleKeyPress(event));
        scene.setOnKeyReleased(event -> handleKeyRelease(event));

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

    /**
     * Set up game loop with AnimationTimer
     * check this: https://stackoverflow.com/questions/73326895/javafx-animationtimer-and-events
     * ^ I'm not sure if we want to keep the game at 60fps or if you got something else in mind.
     */
    private void setGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGameState();
            }
        };
        gameLoop.start();
    }

    /**
     * Main game update method - might be a good idea to take it to game manager?
     */
    private void updateGameState() {
        player.update();

        double newX = player.getCenterX() + player.getVelocityX();
        double newY = player.getCenterY() + player.getVelocityY();
    
        player.setCenterX(newX);
        player.setCenterY(newY);
    }
}