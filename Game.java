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
                    
                    // Finds overlaps with the platform
                    double topOverlap = player_bottom - tile_top;
                    double bottomOverlap = tile_bottom - player_top;
                    double leftOverlap = player_right - tile_left;
                    double rightOverlap = tile_right - player_left;
                    
                    // Checks which side is being colided with
                    double minOverlap = Math.min(
                        Math.min(topOverlap, bottomOverlap),
                        Math.min(leftOverlap, rightOverlap));
                    
                    // Collision with top of platform
                    if (minOverlap == topOverlap && player.getVelocityY() >= 0) {
                        player.setCenterY(tile_top - player_radius);
                        onPlatform = true;
                        player.stopVerticalMovement();
                        break;
                    }
                    // Collision with bottom of platform
                    else if (minOverlap == bottomOverlap && player.getVelocityY() < 0) {
                        player.setCenterY(tile_bottom + player_radius);
                        player.stopVerticalMovement();
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