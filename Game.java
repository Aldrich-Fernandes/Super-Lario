import java.util.List;
import java.util.HashMap;

/**
 * Game class represents the model component of the game.
 * It contains all game logic, state, and physics calculations.
 */
public class Game {
    // Constants
    public static final int numberOfScreens = 6;
    
    // Game state
    private Player player;
    private int currentScene;
    private GameMap[] levelMaps;
    private int coinCount = 0;
    private int index;
    private boolean keyCollected = false;
    private int timeRemaining = 180; // 3 minutes
    private boolean isPaused = false;
    private int score;
    
    // References to current level elements
    private Tile[][] tiles;
    private List<Coin> coins;
    private Key key;
    private Tile exit;
    
    // Level management
    private LevelManager levelManager;
    
    /**
     * Constructor initializes the game state
     */
    public Game() {
        reset();
    }
    
    /**
     * Reset the game to its initial state
     */
    public void reset() {
        levelManager = new LevelManager();
        levelMaps = new GameMap[numberOfScreens];
        index = 0;
        coinCount = 0;
        keyCollected = false;
        timeRemaining = 180;
        isPaused = false;
        
        // Initialize the level maps
        levelMaps[index] = new GameMap("Levels/playerRoom.txt");
        levelMaps[levelManager.randomKeyRoomIndex(numberOfScreens)] = new GameMap("Levels/keyRoom.txt");
        levelMaps[numberOfScreens-1] = new GameMap("Levels/endRoom.txt");
        
        // Initialize player and current level elements
        updateCurrentLevelElements();
        player = new Player(levelMaps[index].getPlayerX(), levelMaps[index].getPlayerY(), levelMaps[index].getPlayerRadius());
    }
    
    /**
     * Updates references to the current level's elements
     */
    private void updateCurrentLevelElements() {
        tiles = levelMaps[index].getTile();
        coins = levelMaps[index].getCoins();
        key = levelMaps[index].getKey();
        exit = levelMaps[index].getExit();
    }
    
    /**
     * Update the game state for a single frame
     * @param deltaTime Time since the last update in seconds
     */
    public void update(double deltaTime) {
        if (isPaused) return;
        
        player.update(deltaTime);
        checkCollisions();
        checkOutOfBounds();
        checkCoins();
        checkKey();
        checkExit();
    }
    
    /**
     * Check for collisions between the player and level elements
     */
    private void checkCollisions() {
        boolean onPlatform = false;
        
        // Player edges
        HashMap<String, Double> playerEdges = player.getEdges();
        double player_top = playerEdges.get("top");
        double player_bottom = playerEdges.get("bottom");
        double player_left = playerEdges.get("left");
        double player_right = playerEdges.get("right");
        double player_radius = player.getRadius();
        
        for (Tile[] tileRow: tiles) {
            for (Tile tile: tileRow) {
                if (!tile.isPassable() && player.getBoundsInParent().intersects(tile.getBoundsInParent())) {
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
                    
                    // the side with lowest overlap is the one which had been collided with
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
        
        // Update player ground state
        player.setIsOnGround(onPlatform);
    }
    
    /**
     * Check if player is going out of bounds and handle room transitions
     */
    private void checkOutOfBounds() {
        if (player.getCenterX() < 0 || player.getCenterX() > (levelMaps[index].getWidth() * levelMaps[index].TILE_SIZE)) {
            GameMap newRoom;
            
            if (player.getCenterX() < 0) {
                if (index > 0 && levelMaps[index - 1] != null) {
                    newRoom = levelMaps[index-1];
                }
                else {
                    newRoom = levelManager.generateRandomRoom();
                    levelMaps[index-1] = newRoom;
                }
                index--;
                player.setCenterX((newRoom.getWidth() * newRoom.TILE_SIZE) + player.getCenterX());
            }
            else {
                if (index < numberOfScreens - 1 && levelMaps[index + 1] != null) {
                    newRoom = levelMaps[index+1];
                }
                else {
                    newRoom = levelManager.generateRandomRoom();
                    levelMaps[index+1] = newRoom;
                }
                index++;
                player.setCenterX((player.getCenterX() - (newRoom.getWidth() * newRoom.TILE_SIZE)));
            }
            
            updateCurrentLevelElements();
        }
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
            if (key.checkCollection(player)) {
                if (coinCount >= key.getRequiredCoins()) {
                    key.collect();
                    keyCollected = true;
                    coinCount -= key.getRequiredCoins();
                }
            }
        }
    }
    
    /**
     * Check if player has reached the exit
     */
    private void checkExit() {
        if (exit != null && keyCollected) {
            if (player.getBoundsInParent().intersects(exit.getBoundsInParent())) {
                // Game completed logic
            }
        }
    }
    
    /**
     * Decrement the timer by 1 second
     * @return true if time remains, false if time is up
     */
    public boolean decrementTimer() {
        if (isPaused) return true;
        
        timeRemaining--;
        return timeRemaining > 0;
    }
    
    // Getters and setters
    
    public Player getPlayer() {
        return player;
    }
    
    public GameMap getCurrentMap() {
        return levelMaps[index];
    }
    
    public Tile[][] getTiles() {
        return tiles;
    }
    
    public List<Coin> getCoins() {
        return coins;
    }
    
    public Key getKey() {
        return key;
    }
    
    public Tile getExit() {
        return exit;
    }
    
    public int getCoinCount() {
        return coinCount;
    }
    
    public boolean isKeyCollected() {
        return keyCollected;
    }
    
    public int getTimeRemaining() {
        return timeRemaining;
    }
    
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (paused) {
            player.resetInputState();
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public int getIndex() {
        return index;
    }
}