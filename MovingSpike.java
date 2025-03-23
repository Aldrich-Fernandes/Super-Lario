import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class MovingSpike extends Trap {
    private static final double MOVE_SPEED = 1.0;
    private double direction = -1.0;
    
    private final double turnCooldown = 1; // 1 second cooldown
    private double lastTurn = 0.0;
    
    public MovingSpike(double x, double y, double size)
    {
        super(x, y, size, 10, Color.SILVER);
        
    }
    
    public void turn() {
        if (lastTurn > turnCooldown){
            direction *= -1;
            lastTurn = 0.0;
        }
    }
    
    public void update(Player player, double deltaTime) {
        super.update(player, deltaTime);
        // check if we need to turn around
        
        // Move  enemy
        baseCenterX += MOVE_SPEED * direction * deltaTime * 60;
        
        drawTriangle();
        lastDamageTime += deltaTime;
        lastTurn += deltaTime;
    }
}