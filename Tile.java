import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Tile class represents a single tile in the game world.
 * It extends Rectangle to provide visual representation and collision detection.
 */
public class Tile extends Rectangle {
    private String type;
    private boolean isPassable;
    
    /**
     * Creates a new Tile with the specified position, size, and type.
     * 
     * @param x The x-coordinate of the tile
     * @param y The y-coordinate of the tile
     * @param width The width of the tile
     * @param height The height of the tile
     * @param type The type of tile (e.g., "TERRAIN", "BACKGROUND")
     */
    public Tile(double x, double y, double width, double height, String type) {
        super(width, height);
        this.type = type;
        
        // Set position
        setTranslateX(x);
        setTranslateY(y);
        
        // Configure tile properties based on type
        configureTileProperties();
    }
    
    /**
     * Configures the visual appearance and game properties of the tile based on its type.
     */
    private void configureTileProperties() {
        switch (type) {
            case "TERRAIN":
                setFill(Color.DARKGRAY);
                setStroke(Color.BLACK);
                setStrokeWidth(1);
                isPassable = false;
                break;
                
            case "BACKGROUND":
                setFill(Color.LIGHTBLUE);
                setOpacity(0.3); // Semi-transparent
                isPassable = true;
                break;
                
            default:
                setFill(Color.WHITE);
                isPassable = true;
                break;
        }
    }
    
    /**
     * Checks if this tile can be passed through by the player.
     * 
     * @return true if the tile is passable, false otherwise
     */
    public boolean isPassable() {
        return isPassable;
    }
    
    /**
     * Gets the type of this tile.
     * 
     * @return The tile type
     */
    public String getType() {
        return type;
    }
}