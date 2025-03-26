import javafx.scene.shape.Circle;
import javafx.animation.*;
/**
 * Coin class represents a collectible coin in the game.
 */
public class Coin extends Item {
    
    /**
     * Creates a new Coin at the specified position with the given radius.
     * 
     * @param centerX The x-coordinate of the center of the coin
     * @param centerY The y-coordinate of the center of the coin
     * @param radius The radius of the coin
     */
    public Coin(double centerX, double centerY) {
        super("COIN", centerX, centerY);
    }
    
    /**
     * Checks if the player has collected this coin based on proximity.
     * 
     * @param player The player to check against
     * @return true if the coin should be collected, false otherwise
     */
    @Override
    public boolean checkCollection(Player player) {
        if (super.isCollected()) return false;
        
        Circle coin = super.getCoin();
        
        // Calculate distance between player and coin centers
        double dx = coin.getCenterX() - player.getCenterX();
        double dy = coin.getCenterY() - player.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is close enough to collect
        return distance < player.getRadius() + coin.getRadius();
    }
}