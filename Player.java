import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import java.util.List;

/**
 * Player class for a tile-based platformer game.
 * Represents the player as a red circle that can move left/right and jump.
 */
public class Player extends Circle {
    // Physics 
    private static final double GRAVITY = 0.5;
    private static final double JUMP_FORCE = -15;
    private static final double MOVE_SPEED = 5;
    
    // Movement 
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean isOnGround = false;
    
    // Keys
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    
    // Properties
    private static final double radius = 5;
    
    /**
     * Creates a new player at the specified position with the given radius
     */
    public Player(double centerX, double centerY) {
        super(centerX, centerY, radius, Color.RED);
    }
    
    /**
     * Updates key state when a key is pressed
     */
    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.LEFT) 
            leftPressed = true;
        if (code == KeyCode.RIGHT) 
            rightPressed = true;
        if (code == KeyCode.SPACE) 
            jumpPressed = true;
    }
    
    /**
     * Updates key state when a key is released
     */
    public void handleKeyReleased(KeyCode code) {
        if (code == KeyCode.LEFT) 
            leftPressed = false;
        if (code == KeyCode.RIGHT) 
            rightPressed = false;
        if (code == KeyCode.SPACE) 
            jumpPressed = false;
    }
    
    /**
     * Updates player position and velocity based on input and physics
     */
    public void update() {
        // Apply horizontal movement
        if (leftPressed) 
            velocityX = -MOVE_SPEED;
        else if (rightPressed) 
            velocityX = MOVE_SPEED;
        else 
            velocityX = 0;
        
        // Apply jump if on ground
        if (jumpPressed && isOnGround) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
        }
        
        // Apply gravity
        velocityY += GRAVITY;
        
        // Update player position with collision detection TODO
       
        
        // Handle screen boundaries TODO
    }
}