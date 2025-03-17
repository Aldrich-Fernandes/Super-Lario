import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import java.util.List;
import java.util.HashMap;

/**
 * Player class for a tile-based platformer game.
 * Represents the player as a red circle that can move left/right and jump.
 */
public class Player extends Circle {
    // Physics 
    private static final double GRAVITY = 0.8;
    private static final double MAX_VELOCITY = 10;
    private static final double JUMP_FORCE = -17;       // Player can jump up 4 tiles
    private static final double MOVE_SPEED = 3.5;       // Player can jump 4 wide gaps
    
    // Movement 
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean isOnGround = true;
    
    // Keys
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    
    
    /**
     * Creates a new player at the specified position with the given radius
     */
    public Player(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius, Color.RED);
        //this.isOnGround = false; // REMOVE WHEN COLLISIONS ARE WORKING!!
    }
    
    /**
     * Updates key state when a key is pressed
     */
    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.A ) 
            leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D ) 
            rightPressed = true;
        if (code == KeyCode.SPACE) 
            jumpPressed = true;
    }
    
    /**
     * Updates key state when a key is released
     */
    public void handleKeyReleased(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.A ) 
            leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D ) 
            rightPressed = false;
        if (code == KeyCode.SPACE) 
            jumpPressed = false;
    }
    
    /**
     * Updates player position and velocity based on input and physics
     */
    public void update() {
        // Apply horizontal movement
        if      (leftPressed)       velocityX = -MOVE_SPEED;
        else if (rightPressed)      velocityX = MOVE_SPEED;
        else                        velocityX = 0;
        
        // Apply jump if on ground
        if (jumpPressed && isOnGround) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
        }
        
        // Apply gravity
        if(!isOnGround) 
            velocityY += GRAVITY;
            if (velocityY > MAX_VELOCITY){
                velocityY = MAX_VELOCITY;
            }
            
        setCenterX(getCenterX() + velocityX);
        setCenterY(getCenterY() + velocityY);
        
        
    }
    
    /**
     * Getter and setter methods for GameScreen to use
     */
    public double getVelocityX() {
        return velocityX;
    }
    
    public double getVelocityY() {
        return velocityY;
    }
    
    public boolean isOnGround() {
        return isOnGround;
    }
    
    public void setIsOnGround(boolean onGround) {
        this.isOnGround = onGround;
    } 
    
    public void stopVerticleMovement(){
        velocityY = 0.0;
    }
    public void stopHorizontalMovement(){
        velocityX = 0.0;
    }
    
    /**
     * Returns a hashmap of the 4 coordinates of each pole of the player
     */
    public HashMap<String, Double> getEdges(){
        HashMap<String, Double> edges = new HashMap<>();
        edges.put("top", this.getBoundsInParent().getMinY());
        edges.put("bottom", this.getBoundsInParent().getMaxY());
        edges.put("left", this.getBoundsInParent().getMinX());
        edges.put("right", this.getBoundsInParent().getMaxX());
        return edges;
    }
    
    /**
     * Resets all input state (for use when pausing the game or showing alerts)
     */
    public void resetInputState() {
        // Reset all input flags
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        
        // Reset velocity (optional, depending on your preference)
        velocityX = 0;
        velocityY = 0;
    }
    
}