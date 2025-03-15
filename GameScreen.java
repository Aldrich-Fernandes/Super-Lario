import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
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

/**
 * Main screen where the game is rendered and run.
 * 
 * // Will pass contain other paramters such as player and worldMap
 * 
 * IT will contain the entities in order to reder them within the same screen
 */
public class GameScreen extends BaseScreen
{    
    private static final int numberOfScreens = 10;
    private int currentScene;
    private Player player;
    private Pane gamePane;
    private AnimationTimer gameLoop;
    private GameMap gameMap;
    
    private Tile[][] tiles;
    private GameMap[] levelMaps;
    private List<String> levelPaths;
    private List<Coin> coins;
    private int coinCount = 0;
    private int index;
    private Label coinLabel;
    
    public GameScreen(GameManager gameManager, int width, int height)
    {
        super(gameManager, width, height);
        setContent();
        setGameLoop();
    }
    
    /**
     * Scans the Levels directory and collects paths to all .txt files.
     */
    private void loadLevelPaths() {
        try {
            
            System.out.println("OverOverHere");
            // Get the path to the Levels directory
            File levelsDir = new File("Levels");
            
            levelMaps = new GameMap[numberOfScreens];
            levelPaths = new ArrayList<>();
            
            // Check if directory exists
            if (!levelsDir.exists() || !levelsDir.isDirectory()) {
                System.err.println("Levels directory not found!");
                return;
            }
            
            // List all files in the directory
            File[] files = levelsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Only add .txt files
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".txt") && !file.getName().equals("playerRoom.txt") && !file.getName().equals("keyRoom.txt") && !file.getName().equals("endRoom.txt")) {
                        levelPaths.add(file.getPath());
                        System.out.println("Found level: " + file.getPath());
                    }
                }
            }
            
            System.out.println("Total levels found: " + levelPaths.size());
        } catch (Exception e) {
            System.err.println("Error loading levels: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    protected void setContent(){
        
        System.out.println("OverHere");
        loadLevelPaths();
        gameMap = new GameMap("Levels/playerRoom.txt");
        index = 0;
        levelMaps[0] = gameMap;
        Random rand  = new Random();
        int randindex = rand.nextInt(1, numberOfScreens-1);
        levelMaps[randindex] = new GameMap("Levels/keyRoom.txt");
        levelMaps[9] = new GameMap("Levels/endRoom.txt");
        
        // Node start coordinated from top left
        // temporary Pane for game elements
        gamePane = gameMap.getMapGrid();
        
        
        
        // Adding coin display functionality
        coinLabel = new Label("Coint Count: 0");
        coinLabel.setLayoutX(10);
        coinLabel.setLayoutY(10);
        coinLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        root.getChildren().add(coinLabel);
        
        player = new Player(gameMap.getPlayerX(), gameMap.getPlayerY(), gameMap.getPlayerRadius());
        tiles = gameMap.getTile();
        coins = gameMap.getCoins();
        
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
        gameMap = new GameMap("level.txt");
        tiles = gameMap.getTile();
        coins = gameMap.getCoins();
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
                else if (player.getCenterX() < 0 || player.getCenterX() > (gameMap.getWidth() * gameMap.TILE_SIZE)) {
                    System.out.println("CENTERX: " + player.getCenterX() + ", INDEX: " + index);
                    GameMap newMap = new GameMap(levelPaths.get(0));
                    
                    gamePane.getChildren().remove(player);
                    root.getChildren().remove(gamePane);
                    
                    if (player.getCenterX() < 0) {
                        if (levelMaps[index - 1] != null) {
                            newMap = levelMaps[index-1];
                        }
                        else {
                            Random rand = new Random();
                            int randIndex = rand.nextInt(levelPaths.size());
                            newMap = new GameMap(levelPaths.get(0));
                            levelMaps[index-1] = newMap;
                        }
                        index--;
                        player.setCenterX((newMap.getWidth() * newMap.TILE_SIZE) + player.getCenterX() + player_radius);
                    }
                    else if (player.getCenterX() > (gameMap.getWidth() * gameMap.TILE_SIZE)) {
                        if (levelMaps[index + 1] != null) {
                            newMap = levelMaps[index+1];
                        }
                        else {
                            Random rand = new Random();
                            int randIndex = rand.nextInt(levelPaths.size());
                            newMap = new GameMap(levelPaths.get(0));
                            levelMaps[index+1] = newMap;
                            System.out.println("Great success.");
                        }
                        index++;
                        player.setCenterX((player.getCenterX() - (newMap.getWidth() * newMap.TILE_SIZE)) + player_radius);
                    }
                    
                    System.out.println("CENTERX: " + player.getCenterX() + ", INDEX: " + index);
                    
                    gameMap = newMap;
                    gamePane = gameMap.getMapGrid();
                    gamePane.getChildren().add(player);
                    root.getChildren().add(gamePane);
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
        checkCoins();
    }

    
    /**
     * Add a coin to the game at the specified position
     */
    private void addCoin(double x, double y) {
        Coin coin = new Coin(x, y, 10); 
        coins.add(coin);
        gamePane.getChildren().add(coin);
    }
    
    /**
     * Check if player has collected any coins
     */
    private void checkCoins() {
        for (Coin coin : coins) {
            if (!coin.isCollected() && coin.checkCollection(player)) {
                coin.collect();
                coinCount++;
                coinLabel.setText("Coint Count: " + coinCount);
            }
        }
    }
}