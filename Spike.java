import javafx.scene.paint.Color;
/**
 * A static trap which when the player makes contact with applies some damage
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Spike extends Trap
{
    private boolean damageApplied = false;
    /**
     * Constructor for objects of class Spike
     */
    public Spike(double x, double y, double size)
    {
        super(x, y, size, 15, Color.BROWN);
    }

    public void checkInteraction(Player player){
        if (player.getBoundsInParent().intersects(this.getBoundsInParent())){
            // Prevents damage from being applied to the player constantly
            if (! damageApplied){
                player.applyDamage(damage);
                damageApplied = true;
            }
        }
        else{
            damageApplied = false;
        }
    }

}
