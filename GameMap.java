import javafx.scene.layout.Pane;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.ImageView;

/**
 * GameMap class handles the creation and management of the game's tile-based world.
 * It reads a level definition file and creates the appropriate tiles in a GridPane.
 */
public class GameMap {
    // Constants for tile dimensions
    public static final int TILE_SIZE = 30;
    
    // The main container for all tiles
    private final Pane mapPane;
    
    // Map dimensions
    private int width;
    private int height;
    
    private int playerX;
    private int playerY;
    private int playerRadius;
    
    // Storage for game entities
    private Tile[][] tiles;
    private Player player;
    private List<Coin> coins;
    private List<Tile> terrainTiles;
    private List<Trap> traps;
    private Key key;
    private Tile exit;
    private List<Tile> turns;
    
    /**
     * Creates a new GameMap and loads from the specified level file.
     * 
     * @param levelFilePath Path to the level definition file
     */
    public GameMap(String levelFilePath) {
        mapPane = new Pane();
        
        coins = new ArrayList<>();
        terrainTiles = new ArrayList<>();
        traps = new ArrayList<>();
        turns = new ArrayList<>();
        
        loadLevelFromFile(levelFilePath);
    }
    
    /**
     * Reads the level file and creates the appropriate game objects.
     * 
     * @param levelFilePath Path to the level definition file
     */
    private void loadLevelFromFile(String levelFilePath) {
        File levelFile = new File(levelFilePath);
        List<String> lines = new ArrayList<>();
        
        // First read all lines to determine map dimensions
        try (Scanner scanner = new Scanner(levelFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Level file not found: " + levelFilePath);
            e.printStackTrace();
            return;
        }
        
        // Set map dimensions
        height = lines.size();
        width = 0;
        for (String line : lines) {
            width = Math.max(width, line.length());
        }
        
        
        // Initialize the tile array
        tiles = new Tile[width][height];
        
        // Create tiles based on level file
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char tileChar = line.charAt(x);
                if (tileChar == 'E') {
                    createTile(x, y, ' ');
                }
                else {
                    createTile(x, y, tileChar);  
                }
            }
        }
    }
    
    /**
     * Creates a tile based on the character in the level file.
     * 
     * @param x The x coordinate in the grid
     * @param y The y coordinate in the grid
     * @param tileChar The character representing the tile type
     */
    private void createTile(int x, int y, char tileChar) {
        switch (tileChar) {
            case 'X': // Terrain
                Tile terrain = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "TERRAIN");
                tiles[x][y] = terrain;
                terrainTiles.add(terrain);
                mapPane.getChildren().add(terrain);
                
                showSprite(terrain, x, y);
                break;
                
            case 'P': // Player
                // Create an empty/background tile first
                addBackgroundTile(x, y);
                
                playerX = x * TILE_SIZE + TILE_SIZE/2;
                playerY = y * TILE_SIZE + TILE_SIZE/2;
                playerRadius = TILE_SIZE/2;
                
                break;
                
            case 'C': // Coin
                // Create an empty/background tile first
                addBackgroundTile(x, y);
                
                // Create coin at this position
                Coin coin = new Coin(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2);
                coins.add(coin);
                mapPane.getChildren().add(coin.getCoin());
                break;
                
            case 'K': // Key 
                // Create an empty/background tile first
                addBackgroundTile(x, y);
                
                // Create a key object
                key = new Key(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2);
                mapPane.getChildren().add(key.getKey());
                
                break;
                
            case 'A': // Exit
                // Create an empty/background tile first
                exit = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "EXIT");
                tiles[x][y] = exit;
                mapPane.getChildren().add(exit);
                showSprite(exit, x, y);
                
                break;
            
            case 'S': // Spike trap
                // Create an empty/background tile first
                addBackgroundTile(x, y);   
                
                Spike spike = new Spike(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2, TILE_SIZE);
                traps.add(spike);
                mapPane.getChildren().add(spike);
                break;

            case 'M': // Moving spike trap
                // Create an empty/background tile first
                addBackgroundTile(x, y);   
                
                MovingSpike movingSpike = new MovingSpike(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2, TILE_SIZE);
                traps.add(movingSpike);
                mapPane.getChildren().add(movingSpike);
                break;
                
            case 'T':   // Points where moving traps will turn
                addBackgroundTile(x, y); 
                
                Tile turn = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "TURN");
                turns.add(turn);
                mapPane.getChildren().add(turn);
                break;
                
            default: // Empty space
                Tile emptyTile = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "BACKGROUND");
                tiles[x][y] = emptyTile;
                mapPane.getChildren().add(emptyTile);
                
                showSprite(emptyTile, x, y);
                break;
        }
    }
    
    /**
     * Adds a sprite image to the map pane if the tile has one.
     */
    private void showSprite(Tile tile, int x, int y) {
        // Add the sprite image if the tile has one
        if (tile.hasSprite()) {
            ImageView imageView = tile.getImageView();
                
            // Position the ImageView at the same location as the tile
            imageView.setTranslateX(x * TILE_SIZE);
            imageView.setTranslateY(y * TILE_SIZE);
                
            mapPane.getChildren().add(imageView);
        }
    }
    
    
    /**
     * Adds a background tile at the specified position.
     */
    private void addBackgroundTile(int x, int y){
        Tile backgroundTile = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "BACKGROUND");
        tiles[x][y] = backgroundTile;
        mapPane.getChildren().add(backgroundTile);
        showSprite(backgroundTile, x, y);
    }
    
    /**
     * Returns the GridPane containing the entire game map
     * 
     * @return The GridPane containing all map tiles
     */
    public Pane getMapGrid() {
        return mapPane;
    }
    
    /**
     * Return the player's starting X position.
     */
    public int getPlayerX() {
        return playerX;
    }
    
    /**
     * Return the player's starting Y position.
     */
    public int getPlayerY() {
        return playerY;
    }
    
    /**
     * Return the player's radius ( for collsion detection).
     */
    public int getPlayerRadius() {
        return playerRadius;
    }
    
    /**
     * Get all coins in the level
     * 
     * @return List of coins
     */
    public List<Coin> getCoins() {
        return coins;
    }
    
    /**
     * Get all Traps in the level
     * 
     * @return List of traps
     */
    public List<Trap> getTraps() {
        return traps;
    }
    
    /**
     * Return all turn points for moving traps.
     */
    public List<Tile> getTurns(){
        return turns;
    }
    
    /**
     * Get the key in the level
     * 
     * @return The key object
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get the exit in the level
     */
    public Tile getExit() {
        return exit;
    }
    
    /**
     * Get all terrain tiles
     * 
     * @return List of terrain tiles
     */
    public List<Tile> getTerrainTiles() {
        return terrainTiles;
    }
    
    /**
     * Get the width of the map in tiles
     * 
     * @return Width in tiles
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get the height of the map in tiles
     * 
     * @return Height in tiles
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Return a 2D array of all tiles.
     */
    public Tile[][] getTile() {
        return tiles;
    }
    
    /**
     * Get a specific tile at the given coordinates
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The tile at that position, or null if out of bounds
     */
    public Tile getTileAt(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null;
    }
}