import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Abstract class for all the traps that can damage the player.
 */
public abstract class Trap extends Polygon {
    protected final int damage;
    protected final double damageCooldown = 1.0; // 1 second cooldown
    protected double lastDamageTime = 0.0;
    
    // Trap properties
    protected double centerX;
    protected final double centerY;
    protected final double size;
    
    /**
     * Create a triangular trap with specified properties.
     */
    public Trap(double x, double y, double size, int damage, Color color) {        
        // Triangle that faces up by default
        centerX = x;
        centerY = y;
        this.size = size;
        this.damage = damage;
        
        this.drawTriangle();
        
        // Coloring the trap in
        setFill(color);
        setStroke(Color.BLACK);
        setStrokeWidth(2);
    }
    
    /**
     * Update the triangle vertices based on current position.
     */
    protected void drawTriangle(){
        this.getPoints().clear();
        this.getPoints().addAll( // From center
            centerX-(size/2), centerY+(size/2),     // Bottom left
            centerX+(size/2), centerY+(size/2),     // Bottom right
            centerX         , centerY-(size/2)      // Top point            
        );
    }
    
    /**
     * Applies damage to player when collision occurs and cooldown has passed.
     */
    private void checkInteraction(Player player){
        if (checkCollision(player) && lastDamageTime >= damageCooldown){
            player.applyDamage(damage);
            lastDamageTime = 0;
        }
    }
    
    /**
     * Uses trigonometry to calculated roughly if the player is colliding with the trap.
     * 
     * This is because collsion with bounds uses a rectangle hence, doesn't work well with triangles.
     */
    private boolean checkCollision(Player player){
        if (player == null){
            return false;
        }
        // Measurements from triangle center base to circle's center
        double adjacent = Math.abs(player.getCenterX() - centerX);
        double opposite = Math.abs((centerY + size/2) - player.getCenterY());
        
        double theta = Math.atan(opposite / adjacent);
        double dist_baseToCircleEdge = Math.hypot(opposite, adjacent) - player.getRadius(); 

        // Angles in triangle
        double alpha = Math.toRadians(63.44);       // Roughly the base angle of side of any trap 
        double beta = Math.PI - (alpha + theta);    // Angles in a triangle add to 180 (pi)
        
        double dist_baseToTriangleEdge = (size/2) * (Math.sin(alpha) / Math.sin(beta));
        
        return dist_baseToCircleEdge <= dist_baseToTriangleEdge;
    }
    
    /**
     * Updates trap state and checks for player interaction.
     */
    public void update(Player player, double deltaTime){
        this.checkInteraction(player);
        lastDamageTime += deltaTime;
    }
}