import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.animation.*;
import javafx.util.Duration;

public abstract class Item {
    private boolean collected = false;
    private String type;
    private Rectangle key;
    private Circle coin;
    
    public Item(String type, double x, double y) {
        this.type = type;
        setType(x, y);
    }
    
    private void setType(double x, double y) {
        switch (type) {
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
     * Collects this key, making it invisible.
     */
    public void collect() {
        collected = true;
        collectionAnimation();
    }
    
    private void collectionAnimation(){
        final Shape item = (type.equals("KEY")) ? key : coin;
        
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
    
    public abstract boolean checkCollection(Player player);
    
    protected Rectangle getKey() {
        return key;
    }
    
    protected Circle getCoin() {
        return coin;
    }
}