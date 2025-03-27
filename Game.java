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
    private int coinCount;
    private int index;
    private boolean keyCollected = false;
    private int timeRemaining = INITIAL_TIME; // 2 minutes
    private boolean isPaused = false;
    
    // References to current level elements
    private Tile[][] tiles;
    private List<Coin> coins;
    private List<Trap> traps;
    private List<Tile> turns;
    private Key key;
    private Tile exit;
    
    
    // Level management
    private LevelManager levelManager;
    
    /**
     * Creates a new game with as it's initial state.
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
        player = new Player(levelMaps[index].getPlayerX(), levelMaps[index].getPlayerY(), levelMaps[index].getPlayerRadius()*0.9);
        setPaused(false);
    }
    
    /**
     * Updates references to the current level's elements
     */
    private void updateCurrentLevelElements() {
        tiles = levelMaps[index].getTile();
        coins = levelMaps[index].getCoins();
        traps = levelMaps[index].getTraps();
        turns = levelMaps[index].getTurns();
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
        checkTraps(deltaTime); 
        checkCollisions();
        checkOutOfBounds();
        checkCoins();
        checkKey();
    }
    
    
    private void checkCollisions() {
        boolean onPlatform = false;
        
        List<Tile> collisionTiles = levelMaps[index].getTerrainTiles();
        
        for (Tile terrain : collisionTiles) {
            // Check if the player is interacting with this terrain tile
            if (player.getBoundsInParent().intersects(terrain.getBoundsInParent())) {
                // We find the overlap in the X-axis, this is done by:
                // So we find the minimum X-value that dictates the right side of either the player or tile
                // We find the maximum X-value that dictates the left side of the either the player or tile
                // We then find the difference between these values to get the overlap
                double overlapLeft = Math.min(
                    player.getBoundsInParent().getMaxX(), 
                    terrain.getBoundsInParent().getMaxX()
                ) - Math.max(
                    player.getBoundsInParent().getMinX(), 
                    terrain.getBoundsInParent().getMinX()
                );
                
                
                // Same calculation as above, but now we are looking at the Y-values
                double overlapTop = Math.min(
                    player.getBoundsInParent().getMaxY(), 
                    terrain.getBoundsInParent().getMaxY()
                ) - Math.max(
                    player.getBoundsInParent().getMinY(), 
                    terrain.getBoundsInParent().getMinY()
                );
                
                // We decide what axis the collision is taking place
                // If the difference for the Y-axis is greater than the X-axis
                if (overlapLeft < overlapTop) {
                    // There must be a collision in the X-axis (horizontal) as the differences are negative
                    
                    if (player.getCenterX() < terrain.getBoundsInParent().getCenterX()) {
                        // Collision from left
                        player.setCenterX(terrain.getBoundsInParent().getMinX() - player.getRadius());
                        player.stopHorizontalMovement();
                    } else {
                        // Collision from right
                        player.setCenterX(terrain.getBoundsInParent().getMaxX() + player.getRadius());
                        player.stopHorizontalMovement();
                    }
                } else {
                    // Vertical collision (Y-axis)
                    if (player.getCenterY() < terrain.getBoundsInParent().getCenterY()) {
                        // Collision from top
                        player.setCenterY(terrain.getBoundsInParent().getMinY() - player.getRadius());
                        player.stopVerticalMovement();
                        onPlatform = true;
                    } else {
                        // Collision from bottom
                        player.setCenterY(terrain.getBoundsInParent().getMaxY() + player.getRadius());
                        player.stopVerticalMovement();
                    }
                }
            }
        }
        
        // Ensure player's ground state is accurately set
        player.setIsOnGround(onPlatform);
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
     * Check if player has been hit by a trap.
     */
    private void checkTraps(double deltaTime) {
        for (Trap trap : traps) {
            if (!player.checkAlive()){
                return;
            }
            
            if (trap instanceof MovingSpike){
                for (Tile point: turns){
                    MovingSpike movingSpike = (MovingSpike) trap;
                    if (movingSpike.getBoundsInParent().intersects(point.getBoundsInParent())){
                        movingSpike.turn();
                        break;
                    }
                }
            }
            
            trap.update(player, deltaTime);
        }
    }
    
    /**
     * Calculate score (based on time, coins and health).
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
        if (isPaused) {
            return true;
        }
        
        timeRemaining--;
        return timeRemaining > 0;
    }
    
    // Getters and setters
    
    /**
     * Player object.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Player's health
     */
    public int getPlayerHealth(){
        return player.getHealth();
    }
    
    /**
     * Current gmae map.
     */
    public GameMap getCurrentMap() {
        return levelMaps[index];
    }
    
    /**
     * All tiles in current level.
     */
    public Tile[][] getTiles() {
        return tiles;
    }
    
    /**
     * All coins in current level.
     */
    public List<Coin> getCoins() {
        return coins;
    }
    
    /**
     * All traps in current level.
     */
    public List<Trap> getTraps() {
        return traps;
    }
    
    /**
     * Key in current level.
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Exit tile in current level.
     */
    public Tile getExit() {
        return exit;
    }
    
    /**
     * Total coin amount.
     */
    public int getCoinCount() {
        return coinCount;
    }
    
    /**
     * Whether the key has been collected or not.
     */
    public boolean isKeyCollected() {
        return keyCollected;
    }
    
    /**
     * Remaining time (in seconds).
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }
    
    /**
     * Initial time limit.
     */
    public int getINITIAL_TIME(){
        return INITIAL_TIME;
    }
    
    /**
     * Sets game paused state.
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (paused) {
            player.resetInputState();
        }
    }
    
    /**
     * Whether game is paused.
     */
    public boolean isPaused() {
        return isPaused;
    }
    
    /**
     * Current level index.
     */
    public int getIndex() {
        return index;
    }
}