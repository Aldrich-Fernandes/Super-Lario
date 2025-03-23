import javafx.scene.paint.Color;

import java.lang.Math;

/**
 * A static trap which when the player makes contact with applies some damage
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Spike extends Trap
{
    private boolean damageApplied = false;
    private double lastDamageTime = 0;
    private final double damageCooldown = 1000; // 1 second cooldown
    /**
     * Constructor for objects of class Spike
     */
    public Spike(double x, double y, double size)
    {
        super(x, y, size, 15, Color.BROWN);
    }

    public void checkInteraction(Player player){
        double currentTime = System.currentTimeMillis();
        if (checkCollision(player)){
            // Prevents damage from being applied to the player constantly
            if (! damageApplied){
                player.applyDamage(damage);
                damageApplied = true;
                lastDamageTime = currentTime;
            }
        }
        else{
            if ((currentTime - lastDamageTime) > damageCooldown/2){
               damageApplied = false;
            }
        }
    }
    
    /**
     * Uses trigonometry to calculated roughly if the player is colliding with the trap.
     * 
     * This is because collsion with bounds uses a rectangle hence, doesn't work well with triangles.
     */
    private boolean checkCollision(Player player){      
        // Measurements from triangle center base to circle's center
        double adjacent = Math.abs(player.getCenterX() - baseCenterX);
        double opposite = Math.abs(baseCenterY - player.getCenterY());
        
        double theta = Math.atan(opposite / adjacent);
        double dist_baseToCircleEdge = Math.hypot(opposite, adjacent) - player.getRadius(); 

        // Angles in triangle
        double alpha = Math.toRadians(63.44);       // Roughly the base angle of side of any trap 
        double beta = Math.PI - (alpha + theta);    // Angles in a triangle add to 180 (pi)
        
        double dist_baseToTriangleEdge = (size/2) * (Math.sin(alpha) / Math.sin(beta));
        
        return dist_baseToCircleEdge <= dist_baseToTriangleEdge;
    }

}
