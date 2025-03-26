import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.animation.*;
import javafx.util.Duration;

/**
 * Abstract class for the collectivle game items (coins and the key).
 */
public abstract class Item {
    private boolean collected = false;
    private final String TYPE;
    private Rectangle key;
    private Circle coin;
    
    /**
     * Create a new item of the specified type at a given position.
     */
    public Item(String type, double x, double y) {
        this.TYPE = type;
        setType(x, y);
    }
    
    /**
     * Set up visual representation based on item type.
     */
    private void setType(double x, double y) {
        switch (TYPE) {
            case "KEY":
                key = new Rectangle(GameMap.TILE_SIZE/2, GameMap.TILE_SIZE/2 * 1.5);
                
                // Position the key (Rectangle coordinates are top-left, not center)
                key.setX(x - GameMap.TILE_SIZE/4);
                key.setY(y - GameMap.TILE_SIZE/2 * 0.75);
                
                // Set appearance
                key.setFill(Color.GOLD);
                key.setStroke(Color.DARKORANGE);
                key.setStrokeWidth(2);
                key.setArcWidth(GameMap.TILE_SIZE * 0.5);  // Rounded corners
                key.setArcHeight(GameMap.TILE_SIZE * 0.5);
                
                break;
            
            case "COIN":
                coin = new Circle(x, y, GameMap.TILE_SIZE/3);
                // Set appearance
                coin.setFill(Color.GOLD);
                coin.setStroke(Color.DARKGOLDENROD);
                coin.setStrokeWidth(2);
        }
    }
    
    /**
     * Checks if this key has been collected.
     * 
     * @return true if the key has been collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * Collects this key and plays collection animation.
     */
    public void collect() {
        collected = true;
        collectionAnimation();
    }
    
    /**
     * Plays animation when the item is collected.
     */
    private void collectionAnimation(){
        Shape item = (TYPE.equals("KEY")) ? key : coin;
        
        TranslateTransition ascend = new TranslateTransition(Duration.millis(300), item);
        TranslateTransition fall = new TranslateTransition(Duration.millis(300), item);
        FadeTransition fade = new FadeTransition(Duration.millis(300), item);
        ParallelTransition fadeOut = new ParallelTransition(fall, fade); 
        
        ascend.setByY(-20);
        fall.setByY(30);
        fade.setFromValue(1);
        fade.setToValue(0);
        
        SequentialTransition transition = new SequentialTransition(ascend,fadeOut);
        transition.setOnFinished(event -> item.setVisible(false));
        transition.play();
    }
    
    /**
     * Check if the player has collected this item.
     */
    public abstract boolean checkCollection(Player player);
    
    /**
     * Return the key for rendering.
     */
    protected Rectangle getKey() {
        return key;
    }
    
    /**
     * Return the coin for rendering.
     */
    protected Circle getCoin() {
        return coin;
    }
}