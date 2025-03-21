import java.util.List;
import java.util.HashMap;

/**
 * Game class represents the model component of the game.
 * It contains all game logic, state, and physics calculations.
 */
public class Game {
    // Constants
    public static final int NO_OF_SCREENS = 6;
    private static final int INITIAL_TIME = 120;
    
    // Game state
    private Player player;
    private GameMap[] levelMaps;
    private int coinCount = 0;
    private int index;
    private boolean keyCollected = false;
    private int timeRemaining = INITIAL_TIME; // 2 minutes
    private boolean isPaused = false;
    
    // References to current level elements
    private Tile[][] tiles;
    private List<Coin> coins;
    private List<Trap> traps;
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
        levelMaps = levelManager.generateLevel();
        index = 0;
        coinCount = 0;
        keyCollected = false;
        timeRemaining = INITIAL_TIME;
        isPaused = false;
        
        // Initialize player and current level elements
        updateCurrentLevelElements();
        player = new Player(levelMaps[index].getPlayerX(), levelMaps[index].getPlayerY(), levelMaps[index].getPlayerRadius());
        setPaused(false);
    }
    
    /**
     * Updates references to the current level's elements
     */
    private void updateCurrentLevelElements() {
        tiles = levelMaps[index].getTile();
        coins = levelMaps[index].getCoins();
        traps = levelMaps[index].getTraps();
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
        checkTraps();
    }
    
    /**
     * Check for collisions between the player and level elements
     */
    private void checkCollisions() {
        boolean onPlatform = false;
        boolean collision = false;
        
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
        // Update player ground state
        player.setIsOnGround(onPlatform);
        }
    }
    
    /**
     * Check if player is going out of bounds and handle room transitions
     */
    private void checkOutOfBounds() {
        int tileSize = levelMaps[index].TILE_SIZE;
        int levelWidth =  levelMaps[index].getWidth() * tileSize;
        int levelHeight = levelMaps[index].getHeight() * tileSize;
        
        // Checking if player has gliched out of the world
        if (player.getCenterY() < 0){
            player.setCenterY(tileSize + player.getRadius());
        }
        else if (player.getCenterY() > levelHeight){
            player.setCenterY(levelHeight - tileSize - player.getRadius());
        }
        if ((player.getCenterX() < 0 && index==0)){
            player.setCenterX(tileSize + player.getRadius());
        }
        else if (player.getCenterX() > levelWidth && index==NO_OF_SCREENS-1){
            player.setCenterY(levelWidth - tileSize - player.getRadius());
        }
        
        // Checks for transition of game scene
        if (player.getCenterX() < 0 || player.getCenterX() > (levelWidth)) {
            
            if (player.getCenterX() < 0) {
                index--;
                player.setCenterX((levelWidth) + player.getCenterX());
            }
            else {
                index++;
                player.setCenterX((player.getCenterX() - (levelWidth)));
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
     * Check if player has collected the key
     */
    private void checkTraps() {
        for (Trap trap : traps) {
            if (!player.checkAlive()){
                continue;
            }
            
            trap.checkInteraction(player);
        }
    }
    
    /**
     * Calculated score
     */
    public int calculateScore(){
        if (!player.checkAlive() || (timeRemaining <= 0)){
            return 0;
        }
        int timeBonus = timeRemaining * 10;
        int coinBonus = coinCount * 50;
        int healthBonus = player.getHealth() * 5;
        
        
        int totalScore = timeBonus + coinBonus + healthBonus;
        return totalScore;
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
    
    public int getPlayerHealth(){
        return player.getHealth();
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
    
    public List<Trap> getTraps() {
        return traps;
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
    
    public int getINITIAL_TIME(){
        return INITIAL_TIME;
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