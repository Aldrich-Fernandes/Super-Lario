import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/**
 * A spike trap that moves horizontally and changes direction with turning points.
 */
public class MovingSpike extends Trap {
    private static final double MOVE_SPEED = 1.0;
    private double direction = -1.0;
    
    private final double turnCooldown = 1; // 1 second cooldown
    private double lastTurn = 0.0;
    
    /**
     * Create a new moving spike trap at the specified position.
     */
    public MovingSpike(double x, double y, double size)
    {
        super(x, y, size, 10, Color.SILVER);
        
    }
    
    /**
     * Changes the direction of movement if cooldown period has passed.
     */
    public void turn() {
        if (lastTurn > turnCooldown){
            direction *= -1;
            lastTurn = 0.0;
        }
    }
    
    /**
     * Update trap position, check for player collision and manage the cooldown.
     */
    public void update(Player player, double deltaTime) {
        super.update(player, deltaTime);
        
        // Move  enemy
        baseCenterX += MOVE_SPEED * direction * deltaTime * 60;
        
        drawTriangle();
        lastDamageTime += deltaTime;
        lastTurn += deltaTime;
    }
}