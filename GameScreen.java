import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.input.KeyCode;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;

/**
 * GameScreen is responsible for rendering the game and handling UI components.
 * It acts as the View component of the MVC pattern.
 */
public class GameScreen extends BaseScreen {    
    private Game game;
    private Pane gamePane;
    private AnimationTimer gameLoop;
    private long lastUpdateTime = 0;
    private int frameCount = 0;
    private long lastFpsUpdateTime = 0;
    
    // UI components
    private Label coinLabel;
    private Label keyLabel;
    private Label fpsLabel;
    private Label countdownLabel;
    private Timeline timer;
    
    /**
     * Constructor initializes the game screen
     */
    public GameScreen(GameManager gameManager, int width, int height) {
        super(gameManager, width, height);
        game = new Game();
        setupView();
        setGameLoop();
    }
    
    /**
     * Set up the UI components
     */
    protected void setContent() {
        // Main game pane where game objects are rendered
        gamePane = game.getCurrentMap().getMapGrid();
        
        // Add UI elements
        setupUIElements();
        
        // Add the player to the game pane
        gamePane.getChildren().add(game.getPlayer());
        
        // Add game pane to the root container
        root.getChildren().add(gamePane);
    }
    
    /**
     * Set up UI elements like labels
     */
    private void setupUIElements() {
        // Coin counter label
        coinLabel = new Label("Coin Count: 0");
        coinLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        // Key collected status label
        keyLabel = new Label("Key Collected: false");
        keyLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        // Countdown timer label
        countdownLabel = new Label("Time remaining: " + game.getTimeRemaining());
        countdownLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        // FPS counter label
        fpsLabel = new Label("FPS: 0");
        fpsLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        // Stats container
        HBox statsBox = new HBox(20);
        statsBox.setPrefHeight(20);
        statsBox.setMinHeight(20);
        statsBox.setMaxHeight(20);      
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(10));
        statsBox.getChildren().addAll(coinLabel, keyLabel, countdownLabel, fpsLabel);
        
        root.getChildren().add(statsBox);
    }
    
    /**
     * Setup the view components
     */
    private void setupView() {
        setContent();
        updateUI();
    }
    
    /**
     * Update all UI elements to reflect current game state
     */
    private void updateUI() {
        coinLabel.setText("Coin Count: " + game.getCoinCount());
        keyLabel.setText("Key Collected: " + game.isKeyCollected());
        countdownLabel.setText("Time remaining: " + game.getTimeRemaining());
        
        // Update exit appearance based on key status
        Tile exit = game.getExit();
        if (exit != null && game.isKeyCollected()) {
            exit.setFill(Color.LIMEGREEN);
            exit.setOpacity(1.0);
        }
    }
    
    /**
     * Handle scene changes when player moves to a different room
     */
    private void handleSceneChange() {
        if (game.getCurrentMap().getMapGrid() != gamePane) {
            // Remove player from current pane
            gamePane.getChildren().remove(game.getPlayer());
            root.getChildren().remove(gamePane);
            
            // Get the new map pane
            gamePane = game.getCurrentMap().getMapGrid();
            
            // Add player to the new pane
            gamePane.getChildren().add(game.getPlayer());
            root.getChildren().add(gamePane);
        }
    }
    
    @Override
    public Scene getScene() {
        Scene scene = super.getScene();
        scene.setOnKeyPressed(event -> handleKeyPress(event));
        scene.setOnKeyReleased(event -> handleKeyRelease(event));
        return scene;
    }
    
    /**
     * Forward key press events to the player
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            if (!game.isPaused()) {
                pauseGame();
            }
        } else {
            game.getPlayer().handleKeyPressed(event.getCode());
        }
    }
    
    /**
     * Forward key release events to the player
     */
    private void handleKeyRelease(KeyEvent event) {
        game.getPlayer().handleKeyReleased(event.getCode());
    }
    
    /**
     * Reset the game to its initial state
     */
    public void reset() {
        gamePane.getChildren().clear();
        game.reset();
        setupView();
    }
    
    /**
     * Start the countdown timer
     */
    public void startCountdown() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            boolean timeRemains = game.decrementTimer();
            countdownLabel.setText("Time remaining: " + game.getTimeRemaining());
    
            if (!timeRemains) {
                timer.stop();
                gameOver();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    /**
     * Handle game over (time ran out)
     */
    private void gameOver() {
        // Display game over message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Time's up!");
        alert.setContentText("You ran out of time. Try again!");
        
        alert.setOnHidden(e -> {
            reset();
            gameManager.showTitleScreen();
        });
        
        alert.show();
    }
    
    /**
     * Pause the game
     */
    public void pauseGame() {
        game.setPaused(true);
        if (timer != null) {
            timer.pause();
        }
        gameManager.pauseGame();
    }
    
    /**
     * Resume the game after pause
     */
    public void resumeCountdown() {
        game.setPaused(false);
        if (timer != null) {
            timer.play();
        }
    }
    
    /**
     * Toggle pause state
     */
    public void changePauseTimer() {
        game.setPaused(!game.isPaused());
    }
    
    /**
     * Reset the timer to initial value
     */
    public void resetTimer() {
        game.reset();
        countdownLabel.setText("Time Remaining: " + game.getTimeRemaining());
    }
    
    /**
     * Handle game completion event
     */
    private void gameCompleted() {
        // Stop game loop and timer
        gameLoop.stop();
        timer.stop();
        
        // Show completion message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Completed!");
        alert.setHeaderText("Congratulations!");
        alert.setContentText("You have successfully completed the game!");
        
        // Return to title screen
        alert.setOnHidden(e -> {
            gameManager.showTitleScreen();
        });
    
        alert.show();   
    }
    
    /**
     * Set up the game loop animation timer
     */
    private void setGameLoop() {
        lastUpdateTime = System.nanoTime();
        lastFpsUpdateTime = System.nanoTime();
        frameCount = 0;
        
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate delta time in seconds
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
                
                // Update game state
                if (!game.isPaused()) {
                    game.update(deltaTime);
                    handleSceneChange();
                    updateUI();
                    
                    // Check for game completion
                    if (game.getExit() != null && game.isKeyCollected() && 
                        game.getPlayer().getBoundsInParent().intersects(game.getExit().getBoundsInParent())) {
                        gameCompleted();
                    }
                }
                
                // Update FPS counter
                frameCount++;
                long elapsedNanos = now - lastFpsUpdateTime;
                if (elapsedNanos > 1_000_000_000) {
                    double actualFps = frameCount / (elapsedNanos / 1_000_000_000.0);
                    fpsLabel.setText(String.format("FPS: %.1f", actualFps));
                    frameCount = 0;
                    lastFpsUpdateTime = now;
                }
                
                // Store time for next frame
                lastUpdateTime = now;
            }
        };
        
        gameLoop.start();
    }

    /**
     * Show a message when key is collected
     */
    private void showKeyCollectedMessage() {
        // Pause the game
        gameLoop.stop();
        game.setPaused(true);
        if (timer != null) {
            timer.pause();
        }
        
        // Show alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Key Collected");
        alert.setContentText("You have collected the key! Coins have been used for the purchase.");
        
        // Resume when alert is closed
        alert.setOnHidden(e -> {
            gameLoop.start();
            game.setPaused(false);
            if (timer != null) {
                timer.play();
            }
        });
        
        alert.show();
    }
}