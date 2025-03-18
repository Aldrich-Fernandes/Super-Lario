import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Key class represents a key that can be collected in the game.
 * It is represented by a simple rectangle with a key-like shape.
 */
public class Key extends Rectangle {
    private boolean collected = false;
<<<<<<< HEAD
    private int requiredCoins = 10;
=======
    private int requiredCoins = 5;
    
>>>>>>> 6b1658a3ea8bbefffb1f17c12274911fc4d0fd86
    
    /**
     * Creates a new Key at the specified position with the given size.
     * 
     * @param centerX The x-coordinate of the center of the key
     * @param centerY The y-coordinate of the center of the key
     * @param size The size of the key
     */
    public Key(double centerX, double centerY, double size) {
        super(size, size * 1.5);
        
        // Position the key (Rectangle coordinates are top-left, not center)
        setX(centerX - size/2);
        setY(centerY - size * 0.75);
        
        // Set appearance
        setFill(Color.GOLD);
        setStroke(Color.DARKORANGE);
        setStrokeWidth(2);
        setArcWidth(size * 0.5);  // Rounded corners
        setArcHeight(size * 0.5);
    }
    
    /**
     * Checks if this key has been collected.
     * 
     * @return true if the key has been collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * Collects this key, making it invisible.
     */
    public void collect() {
        collected = true;
        setVisible(false);
    }
    
    /**
     * Returns the amount of coins necessary to pickup the key!
     */
    public int getRequiredCoins() {
        return requiredCoins;
    }
    
    /**
     * Checks if the player has collected this key based on intersection.
     * 
     * @param player The player to check against
     * @return true if the key should be collected, false otherwise
     */
    public boolean checkCollection(Player player) {
        if (collected) return false;
        
        // Calculate distance between player center and key center
        double keyBounds = Math.max(getWidth(), getHeight()) / 2;
        double playerX = player.getCenterX();
        double playerY = player.getCenterY();
        double keyX = getX() + getWidth()/2;
        double keyY = getY() + getHeight()/2;
        
        double dx = playerX - keyX;
        double dy = playerY - keyY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is close enough to collect
        return distance < player.getRadius() + keyBounds;
    }
}