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
    /**
     * Constructor for objects of class Spike
     */
    public Spike(double x, double y, double size)
    {
        super(x, y, size, 15, Color.BROWN);
    }

}
