import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Exit class, represents the completion of the game by the player!
 */

public abstract class Trap extends Polygon {
    protected int damage;
    protected String type; // updates when a player touches then leave so that it is not constant
    
    /**
     * Create a new Exit with a given position and size
     */
    public Trap(double x, double y, double size, int damage, Color color) {        
        
        // Triangle that faces up by default
        getPoints().addAll( // From top left
            x-(size/2), y+(size/2),     // Bottom left
            x+(size/2), y+(size/2),     // Bottom right
            x, y-(size/2)      // Top point            
        );
        
        this.damage = damage;
        this.type = type;
        
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