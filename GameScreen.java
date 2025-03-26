import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.geometry.Bounds;
import javafx.animation.KeyFrame;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.util.Duration;


/**
 * GameScreen is responsible for rendering the game and handling UI components.
 * View component of the MVC pattern.
 */
public class GameScreen extends BaseScreen {    
    private Game game;
    private Pane gamePane;
    private boolean gameOver;
    
    private AnimationTimer gameLoop;
    private long lastUpdateTime = 0;
    private int frameCount = 0;
    private long lastFpsUpdateTime = 0;
    
    private HBox statsBox;
    private Label coinLabel;
    private CheckBox keyLabel;
    private Label fpsLabel;
    
    private Label healthLabel;
    private ProgressBar healthBar;
    
    private Label countdownLabel;
    private ProgressBar timeBar;
    private Timeline timer;
    
    /**
     * Create a new game screen and initialize game components.
     */
    public GameScreen(GameManager gameManager, int width, int height) {
        super(gameManager, width, height);
        gameOver = false;
        game = new Game();
        setupView();
        setGameLoop();
    }
    
    /**
     * Set up the UI components and the game pane
     */
    @Override
    protected void setContent() {
        // game pane where game objects are rendered
        if (gamePane != null){
            gamePane.getChildren().clear();
        }
        gamePane = game.getCurrentMap().getMapGrid();
        
        // Add UI elements
        setupUIElements();
        
        // Add the player to the game pane
        gamePane.getChildren().add(game.getPlayer());
        
        root.getChildren().add(gamePane);
    }
    
    /**
     * Create and add UI elements (coins, key, health, countdown,fps and stats).
     */
    private void setupUIElements() {
        // Coin counter label
        coinLabel = new Label("Coin Count: 0");
        coinLabel.getStyleClass().add("ui_text_lbl");
        
        // Key collected status label
        keyLabel = new CheckBox("Key");
        keyLabel.getStyleClass().add("ui_text_lbl");
        keyLabel.getStyleClass().add("key-checkbox");
        keyLabel.setDisable(true); 
        keyLabel.setSelected(false);
        
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
     * Update all UI elements with current game state
     */
    private void updateUI() {
        
        healthBar.setProgress(((double)game.getPlayerHealth()) / 100);
        timeBar.setProgress((double) game.getTimeRemaining() / (double)game.getINITIAL_TIME());
        coinLabel.setText("Coins " + game.getCoinCount());
        countdownLabel.setText("Time: " + game.getTimeRemaining());
        keyLabel.setSelected(game.isKeyCollected());
    }
    
    /**
     * update scene when player moves to a different room
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
    
    /**
     * Add event handles to the scene
     */
    @Override
    public Scene getScene() {
        Scene scene = super.getScene();
        scene.setOnKeyPressed(event -> handleKeyPress(event));
        scene.setOnKeyReleased(event -> handleKeyRelease(event));
        return scene;
    }
    
    /**
     * Handles key input events to the player (movement and pause).
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
     * Handles key release events to the player for movement.
     */
    private void handleKeyRelease(KeyEvent event) {
        game.getPlayer().handleKeyReleased(event.getCode());
    }
    
    /**
     * Reset the game to its initial state.
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
        
        gameOver = false;
        setupView();
        setGameLoop();
        
    }
    
    /**
     * Start the countdown timer.
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
     * Pause the game and displays pause scene.
     */
    public void pauseGame() {
        game.setPaused(true);
        if (timer != null) {
            timer.pause();
        }
        gameManager.pauseGame();
    }
    
    /**
     * Resume the game after pause.
     */
    public void resumeCountdown() {
        game.setPaused(false);
        if (timer != null) {
            timer.play();
        }
    }
    
    /**
     * Toggle pause state.
     */
    public void changePauseTimer() {
        game.setPaused(!game.isPaused());
    }
    
    /**
     * Reset the timer to initial value.
     */
    public void resetTimer() {
        game.reset();
        countdownLabel.setText("Time ReshowTitleScreenmaining: " + game.getTimeRemaining());
    }
    
    /**
     * Handle end of game completion event.
     */
    private void gameCompleted() {
        // Stop game loop and timer
        gameLoop.stop();
        timer.stop();
        gameOver = true;
        
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
    
    /**
     * Returns whether the game has ended.
     */
    public boolean isCompleted() {
        return gameOver;
    }
    
    /**
     * Set up the game loop animation timer which handles updates and collisions.
     */
    private void setGameLoop() {
        lastUpdateTime = System.nanoTime();
        lastFpsUpdateTime = System.nanoTime();
        frameCount = 0;
        
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Calculate delta time in seconds
                
                // Update game state
                if (!game.isPaused()) {
                    game.update(deltaTime);
                    handleSceneChange();
                    updateUI();
                    
                    // Check game completion states (if player is dead, end is reached or player caught cheating)
                    if (!game.getPlayer().checkAlive()){
                        gameCompleted();
                    }
                    else if (game.getExit() != null && game.isKeyCollected()) {
                        Bounds playerBounds = game.getPlayer().getBoundsInParent();
                        Bounds exitBounds = game.getExit().getBoundsInParent();
                        if (playerBounds.intersects(exitBounds)){
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