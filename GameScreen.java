import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;

/**
 * Main screen where the game is rendered and run.
 * Contains the game map, player, and game logic.
 */
public class GameScreen extends BaseScreen {
    private GameMap gameMap;
    private BorderPane gameLayout;
    private Label scoreLabel;
    private int score = 0;
    private boolean gameRunning = false;
    
    /**
     * Constructor for objects of class GameScreen
     */
    public GameScreen(GameManager gameManager, int width, int height) {
        super(gameManager, width, height);
        setContent();
    }
    
    @Override
    protected void setContent() {
        gameLayout = new BorderPane();
        
        // Create score display
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10px;");
        
        // Load the game map
        gameMap = new GameMap("level.txt");
        
        // Set up the layout
        gameLayout.setTop(scoreLabel);
        gameLayout.setCenter(gameMap.getMapGrid());
        
        root.getChildren().add(gameLayout);
        
        // Set up keyboard handling
        setupKeyboardHandling();
        
        // Start the game loop
        startGameLoop();
    }
    
    /**
     * Sets up keyboard event handling for the game
     */
    private void setupKeyboardHandling() {
        scene = getScene();
        
        scene.setOnKeyPressed(event -> {
            if (gameMap.getPlayer() != null) {
                gameMap.getPlayer().handleKeyPressed(event.getCode());
            }
        });
        
        scene.setOnKeyReleased(event -> {
            if (gameMap.getPlayer() != null) {
                gameMap.getPlayer().handleKeyReleased(event.getCode());
            }
        });
    }
    
    /**
     * Starts the game animation loop
     */
    private void startGameLoop() {
        gameRunning = true;
        
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameRunning) return;
                
                // Update player
                if (gameMap.getPlayer() != null) {
                    gameMap.getPlayer().update();
                    checkCollisions();
                }
                
                // Update score display
                scoreLabel.setText("Score: " + score);
            }
        };
        
        gameLoop.start();
    }
    
    /**
     * Checks for collisions between the player and game objects
     */
    private void checkCollisions() {
        Player player = gameMap.getPlayer();
        
        // Check coin collisions
        for (Coin coin : gameMap.getCoins()) {
            if (!coin.isCollected() && coin.checkCollection(player)) {
                coin.collect();
                score += 10; // Increase score
            }
        }
        
        // Check terrain collisions for player movement
        // This is a simplified approach; a more complex collision system would be needed for a full game
        
        // TODO: Implement terrain collision handling
    }
}