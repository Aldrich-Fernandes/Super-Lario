import javafx.scene.paint.Color;
import java.lang.Math;

/**
 * A stationary trap that applies damage on the player on contact.
 */
public class Spike extends Trap
{
    /**
     * Create a new spike trap at the specified position.
     */
    public Spike(double x, double y, double size)
    {
        super(x, y, size, 15, Color.BROWN);
    }

}