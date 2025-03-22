import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Enemy extends Rectangle {
    private static final double MOVE_SPEED = 2.0;
    private int direction = 1; // 1 = right, -1 = left
    private Game game;
    
    // Add damage properties
    private static final int DAMAGE_AMOUNT = 10;
    private static final double DAMAGE_COOLDOWN = 1.0; // 1 second between damage applications
    private double lastDamageTime = 0;
    
    public Enemy(double x, double y, double width, double height, Game game) {
        super(width, height);
        setX(x);
        setY(y);
        this.setFill(Color.RED);
        this.game = game;
    }
    
    public void update(double deltaTime) {
        // First check if we need to turn around
        if (shouldTurn()) {
            direction *= -1; // Reverse direction
        }
        
        // Move the enemy
        setX(getX() + (MOVE_SPEED * direction));
        
        // Update damage cooldown timer
        lastDamageTime += deltaTime;
    }
    
    private boolean shouldTurn() {
        int tileSize = GameMap.TILE_SIZE;
        
        // Calculate the position to check based on direction
        double checkX = (direction > 0) ? 
                         getX() + getWidth() + 5 : // Look ahead to the right
                         getX() - 5;              // Look ahead to the left
        
        // Get tile coordinates for the position ahead
        int tileX = (int)(checkX / tileSize);
        
        // Get the tile coordinates for the ground position (one tile below feet)
        int groundTileY = (int)((getY() + getHeight() + 5) / tileSize);
        
        // Get the tile at ground level ahead
        Tile groundTile = game.getCurrentMap().getTileAt(tileX, groundTileY);
        
        // Get the tile at body level ahead
        Tile bodyTile = game.getCurrentMap().getTileAt(tileX, (int)(getY() / tileSize));
        
        // Turn if there's no ground ahead OR if there's a wall ahead
        boolean noGroundAhead = (groundTile == null || groundTile.isPassable());
        boolean wallAhead = (bodyTile != null && !bodyTile.isPassable());
        
        return noGroundAhead || wallAhead;
    }
    
    public void checkInteraction(Player player) {
        // Only apply damage if we hit the player AND the cooldown period has elapsed
        if (this.getBoundsInParent().intersects(player.getBoundsInParent()) && lastDamageTime >= DAMAGE_COOLDOWN) {
            player.applyDamage(DAMAGE_AMOUNT);
            // Reset cooldown timer
            lastDamageTime = 0;
        }
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
}