import javafx.scene.control.*;
import javafx.scene.control.Label;

/**
 * Main screen where the game is rendered and run.
 * 
 * // Will pass contain other paramters such as player and worldMap
 * 
 * IT will contain the entities in order to reder them within the same screen
 */
public class GameScreen extends BaseScreen
{    
    private int currentScene;
    
    public GameScreen(GameManager gameManager, Player player, int width, int height)
    {
        super(gameManager, width, height);
        setContent();
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int sampleMethod(int y)
    {
        return 0;
    }
    
    protected void setContent(){
        Label temp = new Label("SUPER LARIO");
        temp.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        
        root.getChildren().add(temp);
    }
}
