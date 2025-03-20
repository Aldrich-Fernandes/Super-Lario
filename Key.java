import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Key class represents a key that can be collected in the game.
 * It is represented by a simple rectangle with a key-like shape.
 */
public class Key extends Item {
    private int requiredCoins = 10;
    
    /**
     * Creates a new Key at the specified position with the given size.
     * 
     * @param centerX The x-coordinate of the center of the key
     * @param centerY The y-coordinate of the center of the key
     * @param size The size of the key
     */
    public Key(double centerX, double centerY) {
        super("KEY", centerX, centerY);
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
    @Override
    public boolean checkCollection(Player player) {
        if (super.isCollected()) return false;
        
        Rectangle key = super.getKey();
        
        // Calculate distance between player center and key center
        double keyBounds = Math.max(key.getWidth(), key.getHeight()) / 2;
        double playerX = player.getCenterX();
        double playerY = player.getCenterY();
        double keyX = key.getX() + key.getWidth()/2;
        double keyY = key.getY() + key.getHeight()/2;
        
        double dx = playerX - keyX;
        double dy = playerY - keyY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is close enough to collect
        return distance < player.getRadius() + keyBounds;
    }
}