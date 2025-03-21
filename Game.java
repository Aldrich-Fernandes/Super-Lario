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
    private static final int initialTime = 120;
    private int timeRemaining = initialTime; // 2 minutes
    private boolean isPaused = false;
    private boolean cheating = false;
    
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
        timeRemaining = initialTime;
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
            
            if (player.getCenterX() < 0) {
                index--;
                player.setCenterX((levelMaps[index].getWidth() * levelMaps[index].TILE_SIZE) + player.getCenterX());
            }
            else {
                index++;
                player.setCenterX((player.getCenterX() - (levelMaps[index].getWidth() * levelMaps[index].TILE_SIZE)));
            }
            
            updateCurrentLevelElements();
        }
    }
    
    public boolean CheckOutOfWorldBounds(int verticleLimit, int horizontalLimit){
        boolean state = false;
        // Checks if player has fallen through the world or avoid the ceiling
        if (player.getCenterY() < 0 || player.getCenterY() > verticleLimit){
            state = true;
        }
        else if ((player.getCenterX() < 0 && index==0) || (player.getCenterX() > horizontalLimit && index==numberOfScreens-1)){
            state = true;
        }
        
        cheating = state;
        return state;
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
    
    public int getInitialTime(){
        return initialTime;
    }
    
    public boolean isCheating(){
        return cheating;
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