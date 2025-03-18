import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Exit class, represents the completion of the game by the player!
 */

public class Exit extends Rectangle {
    private boolean exitOpen = false;
    
    /**
     * Create a new Exit with a given position and size
     */
    public Exit(double centerX, double centerY, double size) {
        super(size, size * 1.5);
        
        setX(centerX - size/2);
        setY(centerY - size/2);
        
        // Door shape
        setFill(Color.DARKGREEN);
        setStroke(Color.BLACK);
        setStrokeWidth(2);
        setArcWidth(size * 0.1);
        setArcHeight(size * 0.1);
    }
    
    /**
     * Check if player has interacted with the exit.
     */
    public boolean checkInteraction(Player player, boolean hasKey) {
        if(!hasKey)
            return false;
        
        return player.getBoundsInParent().intersects(this.getBoundsInParent());
    }
}