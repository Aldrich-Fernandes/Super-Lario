import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Enemy extends Rectangle {
    private static final double MOVE_SPEED = 1.0;
    private int direction = 1; // 1 = right, -1 = left
    private Game game;
    
    // Add damage properties
    private static final int DAMAGE_AMOUNT = 10;
    private static final double DAMAGE_COOLDOWN = 1.0; // 1 second between damage application
    private double lastDamageTime = 0;
    
    public Enemy(double x, double y, double width, double height, Game game) {
        super(width, height);
        setX(x);
        setY(y);
        this.setFill(Color.RED);
        this.game = game;
    }
    
    public void update(double deltaTime) {
        // check if we need to turn around
        if (shouldTurn()) {
            direction *= -1;
        }
        
        // Move  enemy
        setX(getX() + (MOVE_SPEED * direction));
        
        lastDamageTime += deltaTime;
    }
    
    private boolean shouldTurn() {
         return !game.canEnemyWalkForward(this, direction);
    }
    
    public void checkInteraction(Player player) {
        // Only apply damage if we hit the player AND the cooldown period has elapsed
        if (this.getBoundsInParent().intersects(player.getBoundsInParent()) && lastDamageTime >= DAMAGE_COOLDOWN) {
            player.applyDamage(DAMAGE_AMOUNT);
            // Reset cooldown timer
            lastDamageTime = 0;
        }
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
}