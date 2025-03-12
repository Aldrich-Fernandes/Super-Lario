import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * GameMap class handles the creation and management of the game's tile-based world.
 * It reads a level definition file and creates the appropriate tiles in a GridPane.
 */
public class GameMap {
    // Constants for tile dimensions
    private static final int TILE_SIZE = 50;
    
    // The main container for all tiles
    private GridPane mapGrid;
    
    // Map dimensions
    private int width;
    private int height;
    
    // Storage for game entities
    private Tile[][] tiles;
    private Player player;
    private List<Coin> coins;
    private List<Tile> terrainTiles;
    
    /**
     * Creates a new GameMap and loads the specified level file.
     * 
     * @param levelFilePath Path to the level definition file
     */
    public GameMap(String levelFilePath) {
        mapGrid = new GridPane();
        mapGrid.setGridLinesVisible(false); // Set to true for debugging
        
        coins = new ArrayList<>();
        terrainTiles = new ArrayList<>();
        
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
                createTile(x, y, tileChar);
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
                mapGrid.add(terrain, x, y);
                break;
                
            case 'P': // Player
                // Create an empty/background tile first
                Tile backgroundTile = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "BACKGROUND");
                tiles[x][y] = backgroundTile;
                mapGrid.add(backgroundTile, x, y);
                
                // Create player at this position
                player = new Player(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2);
                mapGrid.add(player, x, y);
                break;
                
            case 'C': // Coin
                // Create an empty/background tile first
                Tile coinBackgroundTile = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "BACKGROUND");
                tiles[x][y] = coinBackgroundTile;
                mapGrid.add(coinBackgroundTile, x, y);
                
                // Create coin at this position
                Coin coin = new Coin(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/3);
                coins.add(coin);
                mapGrid.add(coin, x, y);
                break;
                
            case 'K': // Key (from level file, adding support for it)
                // Create an empty/background tile first
                Tile keyBackgroundTile = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "BACKGROUND");
                tiles[x][y] = keyBackgroundTile;
                mapGrid.add(keyBackgroundTile, x, y);
                
                // Create a key object (you would need to implement this class)
                Key key = new Key(x * TILE_SIZE + TILE_SIZE/2, y * TILE_SIZE + TILE_SIZE/2, TILE_SIZE/2);
                mapGrid.add(key, x, y);
                break;
                
            default: // Empty space
                Tile emptyTile = new Tile(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, "BACKGROUND");
                tiles[x][y] = emptyTile;
                mapGrid.add(emptyTile, x, y);
                break;
        }
    }
    
    /**
     * Returns the GridPane containing the entire game map
     * 
     * @return The GridPane containing all map tiles
     */
    public GridPane getMapGrid() {
        return mapGrid;
    }
    
    /**
     * Get the player object
     * 
     * @return The player
     */
    public Player getPlayer() {
        return player;
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
