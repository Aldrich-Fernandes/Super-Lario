import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import javafx.util.Duration;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;

import javafx.geometry.Pos;
import javafx.geometry.Insets;

/**
 * Main screen where the game is rendered and run.
 * 
 * // Needs to be split into GameScreen (the View) and Game (Model)
 * 
 * IT will contain the entities in order to reder them within the same screen
 */
public class GameScreen extends BaseScreen
{    
    public static final int numberOfScreens = 6;
    private int currentScene;
    private Player player;
    private Pane gamePane;
    private AnimationTimer gameLoop;
    private LevelManager levelManager;
    
    private Tile[][] tiles;
    private GameMap[] levelMaps;
    private List<Coin> coins;
    private List<Trap> traps;
    private Key key;
    private Exit exit;
    
    private int coinCount = 0;
    private int index;
    private boolean keyCollected = false;
    
    private Label coinLabel;
    private Label keyLabel;
    private Label healthLabel;
    
    private Timeline timer;
    private Label countdownLabel;
    private int timeRemaining = 180;        // 3 minutes
    private boolean pauseTimer = false;    

    public GameScreen(GameManager gameManager, int width, int height)
    {
        super(gameManager, width, height);
        reset();
        setGameLoop();
    }
    
    protected void setContent(){
        
        levelManager = new LevelManager();
        levelMaps = new GameMap[numberOfScreens];
        index = 0;
        levelMaps[index] = new GameMap("Levels/playerRoom.txt");
        levelMaps[levelManager.randomKeyRoomIndex()] = new GameMap("Levels/keyRoom.txt");
        levelMaps[numberOfScreens-1] = new GameMap("Levels/endRoom.txt");
        
        gamePane = levelMaps[index].getMapGrid();
        
        // Adding coin display functionality
        coinLabel = new Label("Coint Count: 0");
        coinLabel.setLayoutX(10);
        coinLabel.setLayoutY(10);
        coinLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        keyLabel = new Label("Key Collected: false");
        keyLabel.setLayoutX(10);
        keyLabel.setLayoutY(10);
        keyLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        countdownLabel = new Label("Time remaining: " + timeRemaining);
        countdownLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        countdownLabel.setLayoutX(1010); 
        countdownLabel.setLayoutY(-35);
        
        healthLabel = new Label("Health: 100");
        healthLabel.setLayoutX(10);
        healthLabel.setLayoutY(10);
        healthLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        
        // With this code:
        HBox statsBox = new HBox(20); // 20 is the spacing between elements
        statsBox.setPrefHeight(20); // Set the preferred height to exactly 40 pixels
        statsBox.setMinHeight(20);  // Set the minimum height to 40 pixels
        statsBox.setMaxHeight(20);  // Set the maximum height to 40 pixels      
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(10));
        statsBox.getChildren().addAll(coinLabel, keyLabel,countdownLabel, healthLabel);
        
        // Reset the coinLabel and keyLabel positions
        coinLabel.setLayoutX(0);
        coinLabel.setLayoutY(0);
        keyLabel.setLayoutX(0);
        keyLabel.setLayoutY(0);
        healthLabel.setLayoutX(0);
        healthLabel.setLayoutY(0);
        
        root.getChildren().add(statsBox);
        
        player = new Player(levelMaps[index].getPlayerX(), levelMaps[index].getPlayerY(), levelMaps[index].getPlayerRadius());
        tiles = levelMaps[index].getTile();
        coins = levelMaps[index].getCoins();
        traps = levelMaps[index].getTraps();
        key = levelMaps[index].getKey();
        exit = levelMaps[index].getExit();
        
        gamePane.getChildren().add(player);
        root.getChildren().add(gamePane);
        
        //resetTimer();
        //startCountdown();
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
     * Reloads the entire game from the start
     */
    public void reset(){
        //gamePane.getChildren().clear();
        setContent();
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
        
        
        for (Tile[] tileRow: tiles){
            for (Tile tile: tileRow) {
                if (!tile.isPassable() && player.getBoundsInParent().intersects(tile.getBoundsInParent())){
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
                        break;
                    }
                    // Collision with bottom of platform
                    else if (minOverlap == bottomOverlap && player.getVelocityY() < 0) {
                        player.setCenterY(tile_bottom + player_radius);
                        player.stopVerticleMovement();
                        break;
                    }
                    // Collision with left of platform
                    else if (minOverlap == leftOverlap && player.getVelocityX() > 0) {
                        player.setCenterX(tile_left - player_radius);
                        player.stopHorizontalMovement();
                        break;
                    }
                    // Collision with right of platform
                    else if (minOverlap == rightOverlap && player.getVelocityX() < 0) {
                        player.setCenterX(tile_right + player_radius);
                        player.stopHorizontalMovement();
                        break;
                    }
                }
            }
        }
        
        // Allows for updates both ways when player is on and off the platform
        player.setIsOnGround(onPlatform);
    }
    
    public void startCountdown() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            countdownLabel.setText("Time remaining: " + timeRemaining);
    
            if (timeRemaining <= 0) {
                timer.stop();
                //System.out.println("Game Over");

            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    //For convenience sake for PauseScreen
    public void changePauseTimer(){
        pauseTimer = !(pauseTimer);
    }
    
    public void resetTimer(){
        timeRemaining = 180;
        countdownLabel.setText("Time Remaining: " + timeRemaining);
    }
    
    private void pauseCountdown() {
        if (timer != null) {
            timer.pause();
        }
        player.resetInputState(); 
    }
    
    public void resumeCountdown() {
        if (timer != null) {
            timer.play();
        }
    }
    
    public void endOfTime(){
        if (timeRemaining == 0){
            resetTimer();
            gameManager.showTitleScreen();
        }
    }
        
    private void checkKeyCommands() {
        
        Scene scene = getScene();
        if (scene == null) return; 
    
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (!pauseTimer) {
                    pauseCountdown();
                    pauseTimer = true;
                    gameManager.pauseGame();
                } else {
                    resumeCountdown();
                    pauseTimer = false;
                }
            } else {
                handleKeyPress(event); 
            }
        });
    
        scene.setOnKeyReleased(this::handleKeyRelease);
    }

    /**
     * Checks and updates the screen the player moves to.
     */
    private void checkOutOfBounds() {
        if (player.getCenterX() < 0 || player.getCenterX() > (levelMaps[index].getWidth() * levelMaps[index].TILE_SIZE)) {
            //System.out.println("CENTERX: " + player.getCenterX() + ", INDEX: " + index);
            GameMap newRoom = levelManager.generateRandomRoom();
            
            gamePane.getChildren().remove(player);
            root.getChildren().remove(gamePane);
            
            if (player.getCenterX() < 0) {
                if (levelMaps[index - 1] != null) {
                    newRoom = levelMaps[index-1];
                }
                else {
                    levelMaps[index-1] = newRoom;
                }
                index--;
                player.setCenterX((newRoom.getWidth() * newRoom.TILE_SIZE) + player.getCenterX());
            }
            else if (player.getCenterX() > (levelMaps[index].getWidth() * levelMaps[index].TILE_SIZE)) {
                if (levelMaps[index + 1] != null) {
                    newRoom = levelMaps[index+1];
                }
                else {
                    levelMaps[index+1] = newRoom;
                }
                index++;
                player.setCenterX((player.getCenterX() - (newRoom.getWidth() * newRoom.TILE_SIZE)));
            }
            //System.out.println("CENTERX: " + player.getCenterX() + ", INDEX: " + index);
            
            gamePane = levelMaps[index].getMapGrid();
            tiles = levelMaps[index].getTile();
            coins = levelMaps[index].getCoins();
            traps = levelMaps[index].getTraps();
            key = levelMaps[index].getKey();
            exit = levelMaps[index].getExit();
            gamePane.getChildren().add(player);
            root.getChildren().add(gamePane);
        } 
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
        checkOutOfBounds();
        checkCoins();
        checkKey();
        checkKeyCommands();
        updateLabels();
        endOfTime();
        checkExit();
    }
    
    /**
     * Update UI labels with current game state
     */
    private void updateLabels() {
        coinLabel.setText("Coins: " + coinCount);
        keyLabel.setText("Key Collected: " + keyCollected);
        healthLabel.setText("Health: "+ player.getHealth());
    }
    
    /**
     * Check if player has collected any coins
     */
    private void checkCoins() {
        for (Coin coin : coins) {
            if (!coin.isCollected() && coin.checkCollection(player)) {
                coin.collect();
                coinCount++;
            }
        }
    }
    
    /**
     * Check if player has collected the key
     */
    private void checkKey() {
        if (key != null && !key.isCollected()) {
            // First check collection using the original method
            if (key.checkCollection(player)) {
                // Then check if player has enough coins
                if (coinCount >= key.getRequiredCoins()) {
                    key.collect();
                    keyCollected = true;
                    
                    // Deduct coins from the player's total
                    coinCount -= key.getRequiredCoins();
                    
                    // Collection Feedback - Change so that it is a label
                    showKeyCollectedMessage(true);
                }
            }
        }
    }
    
    /**
     * Show a message when the key is collected
     */
    private void showKeyCollectedMessage(boolean collected) {
        // pause the game and reset player input
        gameLoop.stop();
        player.resetInputState();
        pauseCountdown();
        
        // make a alert!
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        if (collected){
            alert.setTitle("Key Collected");
            alert.setContentText("You have collected the key! Coins have been used for the purchase.");
            alert.show();
        }
        // continue the game!
        alert.setOnHidden(e -> {gameLoop.start();
                            resumeCountdown(); }
                         );
    }
    
    /**
     * Check if player has reached the exit with the key
     */
    private void checkExit() {
        // Only check for exit interaction if exit exists in the current level
        if (exit != null) {
            // Update the exit appearance based on key status
            if (keyCollected) {
                exit.setFill(Color.LIMEGREEN);
                exit.setOpacity(1.0);
            } else {
                exit.setFill(Color.DARKGREEN);
                exit.setOpacity(0.5);
            }
            
            // Check if player interacts with exit while having key
            if (exit.checkInteraction(player, keyCollected)) {
                gameCompleted();
            }
        }
    }
    
     /**
     * Display game completion message and handle end of game
     */
    private void gameCompleted() {
        // Stop game loop and reset player input
        gameLoop.stop();
        timer.stop();
        player.resetInputState();
        
        // Show completion alert
        gameManager.showGameOverScreen(true, calculateScore());  
    }
    
    /**
     * Calculates and return a score
     */
    private int calculateScore(){
        int timeBonus = timeRemaining * 10;
        int coinBonus = coinCount * 50;
        int healthBonus = player.getHealth() * 5;
        
        if (player.checkAlive()) {healthBonus += 1000;}
        
        int totalScore = timeBonus + coinBonus + healthBonus;
        return totalScore;
    }
}