import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Tile class represents a single tile in the game world.
 * Able to represent terrain, background or special objects (i.e keys, coins and spikes).
 */
public class Tile extends Rectangle {
    private final String TYPE;
    private boolean isPassable;
    private ImageView imageView;
    
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
        this.TYPE = type;
        
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
        switch (TYPE) {
            case "TERRAIN":
                loadSprite("Sprites/stone_brick12.png");
                isPassable = false;
                break;
                
            case "BACKGROUND":
                loadSprite("Sprites/brick_dark2.png");
                isPassable = true;
                break;
                
            case "KEY":
                loadSprite("Sprites/key.png");
                isPassable = true;
                break;
                
            case "EXIT":
                loadSprite("Sprites/dngn_closed_door.png");
                isPassable = true;
                break;
            
            case "TURN":
                loadSprite("Sprites/brick_dark2.png");
                isPassable = true;
                break;
                
            default:
                setFill(Color.WHITE);
                isPassable = true;
                break;
        }
    }
    
    /**
     * Loads a sprite image for this tile.
     * 
     * @param imagePath Path to the image file
     */
    private void loadSprite(String imagePath) {
        try {
            // Load image
            Image image = new Image(imagePath);
            
            // Create ImageView with same size as the tile
            imageView = new ImageView(image);
            imageView.setFitWidth(getWidth());
            imageView.setFitHeight(getHeight());
            
            // rectangle transparent since we're showing an image
            setFill(Color.TRANSPARENT);
            
        } catch (Exception e) {
            // If image loading fails, use a default color
            System.err.println("Failed to load image: " + imagePath);
            setFill(Color.DARKGRAY);
            setStroke(Color.BLACK);
            setStrokeWidth(1);
        }
    }
    
    /**
     * Gets the ImageView for this tile if it has a sprite.
     * 
     * @return The ImageView or null if this tile doesn't use a sprite
     */
    public ImageView getImageView() {
        return imageView;
    }
    
    /**
     * Checks if this tile has a sprite image.
     * 
     * @return true if this tile has a sprite image, false otherwise
     */
    public boolean hasSprite() {
        return imageView != null;
    }
    
    /**
     * Checks player can move through a tile.
     * 
     * @return true if the tile is passable, false otherwise
     */
    public boolean isPassable() {
        return isPassable;
    }
    
    /**
     * Gets the tile's type (i.e Terrain, Background ...).
     * 
     * @return The tile type
     */
    public String getType() {
        return TYPE;
    }
}