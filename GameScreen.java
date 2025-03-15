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
    private int coinCount = 0;
    private int index;
    private Label coinLabel;
    
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
        root.getChildren().add(coinLabel);
        
        player = new Player(levelMaps[index].getPlayerX(), levelMaps[index].getPlayerY(), levelMaps[index].getPlayerRadius());
        tiles = levelMaps[index].getTile();
        coins = levelMaps[index].getCoins();
        
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
                        //break;
                    }
                    // Collision with bottom of platform
                    else if (minOverlap == bottomOverlap && player.getVelocityY() < 0) {
                        player.setCenterY(tile_bottom + player_radius);
                        player.stopVerticleMovement();
                        //break;
                    }
                    // Collision with left of platform
                    else if (minOverlap == leftOverlap && player.getVelocityX() > 0) {
                        player.setCenterX(tile_left - player_radius);
                        player.stopHorizontalMovement();
                        //break;
                    }
                    // Collision with right of platform
                    else if (minOverlap == rightOverlap && player.getVelocityX() < 0) {
                        player.setCenterX(tile_right + player_radius);
                        player.stopHorizontalMovement();
                        //break;
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