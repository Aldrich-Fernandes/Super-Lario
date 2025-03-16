import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import java.util.HashMap;
import java.util.Random;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

/**
 * Main screen where the game is rendered and run.
 * 
 * // Will pass contain other paramters such as player and worldMap
 * 
 * IT will contain the entities in order to reder them within the same screen
 */
public class GameScreen extends BaseScreen
{    
    public static final int numberOfScreens = 10;
    private int currentScene;
    private Player player;
    private Pane gamePane;
    private AnimationTimer gameLoop;
    private LevelManager levelManager;
    
    private Tile[][] tiles;
    private GameMap[] levelMaps;
    private List<Coin> coins;
    private Key key;
    
    private int coinCount = 0;
    private int index;
    private boolean keyCollected = false;
    
    private Label coinLabel;
    private Label keyLabel;
    
    public GameScreen(GameManager gameManager, int width, int height)
    {
        super(gameManager, width, height);
        setContent();
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
        
        // With this code:
        HBox statsBox = new HBox(20); // 20 is the spacing between elements
        statsBox.setPrefHeight(20); // Set the preferred height to exactly 40 pixels
        statsBox.setMinHeight(20);  // Set the minimum height to 40 pixels
        statsBox.setMaxHeight(20);  // Set the maximum height to 40 pixels      
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(10));
        statsBox.getChildren().addAll(coinLabel, keyLabel);
        
        // Reset the coinLabel and keyLabel positions
        coinLabel.setLayoutX(0);
        coinLabel.setLayoutY(0);
        keyLabel.setLayoutX(0);
        keyLabel.setLayoutY(0);
        
        root.getChildren().add(statsBox);
        
        player = new Player(levelMaps[index].getPlayerX(), levelMaps[index].getPlayerY(), levelMaps[index].getPlayerRadius());
        tiles = levelMaps[index].getTile();
        coins = levelMaps[index].getCoins();
        key = levelMaps[index].getKey();
        
        gamePane.getChildren().add(player);
        
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
            key = levelMaps[index].getKey();
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
    }
    
    /**
     * Update UI labels with current game state
     */
    private void updateLabels() {
        coinLabel.setText("Coins: " + coinCount);
        keyLabel.setText("Key Collected: " + keyCollected);
    }
    
    /**
     * Check if player has collected any coins
     */
    private void checkCoins() {
        for (Coin coin : coins) {
            if (!coin.isCollected() && coin.checkCollection(player)) {
                coin.collect();
                coinCount++;
                updateLabels();
            }
        }
    }
    
    /**
     * Check if player has collected the key
     */
    private void checkKey() {
        if (key != null && !key.isCollected()) {
            // First check if player has enough coins
            if (coinCount >= 5) {
                // Then check collection using the original method
                if (key.checkCollection(player)) {
                    key.collect();
                    keyCollected = true;
                    
                    // Deduct 5 coins from the player's total
                    coinCount -= 5;
                    
                    // Update UI
                    updateLabels();
                    
                    // Optional: Show a notification or play a sound
                    showKeyCollectedMessage();
                    
                    
                }
            }
        }
    }
    
    /**
     * Show a message when the key is collected
     */
    private void showKeyCollectedMessage() {
        // pause the game and reset player input
        gameLoop.stop();
        player.resetInputState();
        
        // make a alert!
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Key Collected");
        alert.setHeaderText(null);
        alert.setContentText("You have collected the key! 5 coins have been deducted.");
        alert.show();
        
        // continue the game!
        alert.setOnHidden(e -> gameLoop.start());
    }
}