import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Coin class represents a collectible coin in the game.
 * It extends Circle to provide visual representation.
 */
public class Coin extends Circle {
    private boolean collected = false;
    
    /**
     * Creates a new Coin at the specified position with the given radius.
     * 
     * @param centerX The x-coordinate of the center of the coin
     * @param centerY The y-coordinate of the center of the coin
     * @param radius The radius of the coin
     */
    public Coin(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        
        // Set appearance
        setFill(Color.GOLD);
        setStroke(Color.DARKGOLDENROD);
        setStrokeWidth(2);
    }
    
    /**
     * Checks if this coin has been collected.
     * 
     * @return true if the coin has been collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * Collects this coin, making it invisible.
     */
    public void collect() {
        collected = true;
        collectionAnimation();
    }
    
    private void collectionAnimation(){
        TranslateTransition ascend = new TranslateTransition(Duration.millis(300), this);
        TranslateTransition fall = new TranslateTransition(Duration.millis(300), this);
        FadeTransition fade = new FadeTransition(Duration.millis(300), this);
        ParallelTransition fadeOut = new ParallelTransition(fall, fade);
        
        ascend.setByY(-20);
        fall.setByY(30);
        fade.setFromValue(1);
        fade.setToValue(0);
        
        SequentialTransition transition = new SequentialTransition(ascend,fadeOut);
        transition.setOnFinished(event -> setVisible(false));
        transition.play();
    }
    
    /**
     * Checks if the player has collected this coin based on proximity.
     * 
     * @param player The player to check against
     * @return true if the coin should be collected, false otherwise
     */
    public boolean checkCollection(Player player) {
        if (collected) return false;
        
        // Calculate distance between player and coin centers
        double dx = getCenterX() - player.getCenterX();
        double dy = getCenterY() - player.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Check if player is close enough to collect
        return distance < player.getRadius() + getRadius();
    }
}