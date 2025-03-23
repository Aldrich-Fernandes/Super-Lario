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
import javafx.geometry.Bounds;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;


/**
 * GameScreen is responsible for rendering the game and handling UI components.
 * It acts as the View component of the MVC pattern.
 */
public class GameScreen extends BaseScreen {    
    private Game game;
    private Pane gamePane;
    private boolean gameCompleted;
    
    private AnimationTimer gameLoop;
    private long lastUpdateTime = 0;
    private int frameCount = 0;
    private long lastFpsUpdateTime = 0;
    
    // UI components
    private HBox statsBox;
    private Label coinLabel;
    private Label keyLabel;
    private Label fpsLabel;
    
    private Label healthLabel;
    private ProgressBar healthBar;
    
    private Label countdownLabel;
    private ProgressBar timeBar;
    private Timeline timer;
    
    /**
     * Constructor initializes the game screen
     */
    public GameScreen(GameManager gameManager, int width, int height) {
        super(gameManager, width, height);
        gameCompleted = false;
        game = new Game();
        setupView();
        setGameLoop();
    }
    
    /**
     * Set up the UI components
     */
    protected void setContent() {
        // Main game pane where game objects are rendered
        if (gamePane != null){
            gamePane.getChildren().clear();
        }
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
        coinLabel.getStyleClass().add("ui_text_lbl");
        
        // Key collected status label
        keyLabel = new Label("Key Collected: false");
        keyLabel.getStyleClass().add("ui_text_lbl");
        
        // Health box
        HBox health = new HBox(2);
        healthLabel= new Label("Health");
        healthLabel.getStyleClass().add("ui_text_lbl");
        healthBar = new ProgressBar(1.0);
        healthBar.setId("health_bar");
        health.getChildren().addAll(healthLabel, healthBar);
        
        // Countdown timer label
        HBox timer = new HBox(2);
        countdownLabel = new Label("Time remaining");
        countdownLabel.getStyleClass().add("ui_text_lbl");
        timeBar = new ProgressBar(1.0);
        timeBar.setId("timer");
        timer.getChildren().addAll(countdownLabel, timeBar);
        
        // FPS counter label
        fpsLabel = new Label("FPS: 0");
        fpsLabel.getStyleClass().add("ui_text_lbl");
        
        // Stats container
        statsBox = new HBox(20);
        statsBox.setPrefHeight(20);
        statsBox.setMinHeight(20);
        statsBox.setMaxHeight(20);      
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(10));
        statsBox.getChildren().addAll(health, coinLabel, fpsLabel, keyLabel, timer);
        
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
        healthBar.setProgress(((double)game.getPlayerHealth()) / 100);
        timeBar.setProgress((double) game.getTimeRemaining() / (double)game.getINITIAL_TIME());
        coinLabel.setText("Coins " + game.getCoinCount());
        countdownLabel.setText("Time: " + game.getTimeRemaining());
        keyLabel.setText("Collected: "+ game.isKeyCollected());
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
     * Reset the game to its initial state (DOESNT WORK YET PLEASE FIX PRETTY PLEASE)
     */
    public void reset() {
        // Terminates any ongoing timers and game loops from previous game.
        if (timer != null){
            timer.stop();
        }
        if (gameLoop != null){
            gameLoop.stop();
        }
        
        game.reset();
        
        root.getChildren().clear();
        root.getChildren().add(makeMenuBar());
        
        gameCompleted = false;
        setupView();
        setGameLoop();
        
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
                gameCompleted();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
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
        
        int score = 0;
        String comment = "";
        
        if (!game.getPlayer().checkAlive()){
            comment="You've developed a rather deadly affliction to spikes.";
        }
        else if (game.getTimeRemaining() <= 0){
            comment="The clock strikes zero, and so does your chance of survival. Better luck next time!";
        }
        else {
            score = game.calculateScore();
        }
        
        game.reset();
        
        if (score == 0) {
            gameManager.showGameOverScreen(false, score, comment);
        }
        else {
            gameManager.showGameOverScreen(true, score, comment);
        }
             
    }
    
    public boolean isCompleted() {
        return gameCompleted;
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
                    
                    // Check for game completion states (if player is dead, end is reached or player caught cheating)
                    if (!game.getPlayer().checkAlive()){
                        gameCompleted = true;
                        gameCompleted();
                    }
                    else if (game.getExit() != null && game.isKeyCollected()) {
                        Bounds playerBounds = game.getPlayer().getBoundsInParent();
                        Bounds exitBounds = game.getExit().getBoundsInParent();
                        if (playerBounds.intersects(exitBounds)){
                            gameCompleted = true;
                            gameCompleted();
                        }
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
}