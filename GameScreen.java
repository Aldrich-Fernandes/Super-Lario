import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.HashMap;

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
    private Pane gamePane;
    private AnimationTimer gameLoop;
    
    private ArrayList<Rectangle> tiles;
    
    public GameScreen(GameManager gameManager, int width, int height)
    {
        super(gameManager, width, height);
        this.player = new Player(100, 100);
        setContent();
        setGameLoop();
    }
    
    protected void setContent(){
        
        // Node start coordinated from top left
        // temporary Pane for game elements
        gamePane = new Pane();
        gamePane.setPrefSize(800, 500);
        
        // two temp rectangles (collision testing)
        tiles = new ArrayList<>();
        
        Rectangle rect1 = new Rectangle(250, 350, 200, 30); // (x, y, width, height)
        rect1.setFill(Color.DARKGRAY);
        rect1.setStroke(Color.BLACK);
        tiles.add(rect1);
        
        Rectangle floor = new Rectangle(0, 470, 800, 30);
        floor.setFill(Color.DARKGRAY);
        floor.setStroke(Color.BLACK);
        tiles.add(floor);
        
        gamePane.getChildren().addAll(rect1, floor, player);
        
        root.getChildren().add(gamePane);
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
    
    private void restart(){
        player.setCenterX(100);
        player.setCenterY(100);
    }
    
    private void checkCollisions(){
        boolean onPlatform = false; 
        
        // Player edges
        HashMap<String, Double> playerEdges = player.getEdges();
        double player_top = player.getEdges().get("top");
        double player_bottom = player.getEdges().get("bottom");
        double player_left = player.getEdges().get("left");
        double player_right = player.getEdges().get("right");
        double player_radius = player.getRadius();
        
        for (Rectangle tile: tiles){
            if (player.getBoundsInParent().intersects(tile.getBoundsInParent())){
                // Tile borders
                double tile_top = tile.getBoundsInParent().getMinY();
                double tile_bottom = tile.getBoundsInParent().getMaxY();
                double tile_left = tile.getBoundsInParent().getMinX();
                double tile_right = tile.getBoundsInParent().getMaxX();
                
                // Finds overlaps with platform
                double topOverlap = player_bottom - tile_top;
                double bottomOverlap = tile_bottom - player_top;
                double leftOverlap = player_right - tile_left;
                double rightOverlap = tile_right - player_left;
                
                // the side with with lowest overlap is the one which had been collieded with
                double minOverlap = Math.min(
                    Math.min(topOverlap, bottomOverlap),
                    Math.min(leftOverlap, rightOverlap));
                
                // Collision with top of platform
                if (minOverlap == topOverlap && player.getVelocityY() >= 0) {
                    player.setCenterY(tile_top - player_radius);
                    onPlatform = true;
                    player.stopVerticleMovement();
                }
                // Collision with bottom of platform
                else if (minOverlap == bottomOverlap && player.getVelocityY() < 0) {
                    player.setCenterY(tile_bottom + player_radius);
                    player.stopVerticleMovement();
                }
                // Collision with left of platform
                else if (minOverlap == leftOverlap && player.getVelocityX() > 0) {
                    player.setCenterX(tile_left - player_radius);
                    player.stopHorizontalMovement();
                }
                // Collision with right of platform
                else if (minOverlap == rightOverlap && player.getVelocityX() < 0) {
                    player.setCenterX(tile_right + player_radius);
                    player.stopHorizontalMovement();
                }
            }
        }
        
        // Allows for updates both ways when player is on and off the platform
        player.setIsOnGround(onPlatform);
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
        
        checkCollisions();
    }
}