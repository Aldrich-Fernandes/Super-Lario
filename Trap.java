import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Exit class, represents the completion of the game by the player!
 */

public abstract class Trap extends Polygon {
    protected final int damage;
    
    // Used in collision calculation
    protected final double baseCenterX;
    protected final double baseCenterY;
    protected final double size;
    
    /**
     * Create a new Exit with a given position and size
     */
    public Trap(double x, double y, double size, int damage, Color color) {        
        // Triangle that faces up by default
        getPoints().addAll( // From center
            x-(size/2), y+(size/2),     // Bottom left
            x+(size/2), y+(size/2),     // Bottom right
            x, y-(size/2)      // Top point            
        );
        
        baseCenterX = x;
        baseCenterY = y;
        this.size = size;
        this.damage = damage;
        
        // Coloring the trap in
        setFill(color);
        setStroke(Color.BLACK);
        setStrokeWidth(2);
    }
    
    /**
     * Check if player has interacted with the exit.
     */
    public abstract void checkInteraction(Player player);
}