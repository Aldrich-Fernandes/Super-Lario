import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TilePlatformer extends Application {
    
    // Game constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_FORCE = -15;
    private static final double MOVE_SPEED = 5;
    private static final int TILE_SIZE = 40;
    
    // Game objects
    private Circle player;
    private List<Rectangle> platforms = new ArrayList<>();
    
    // Player physics
    private double velocityY = 0;
    private double velocityX = 0;
    private boolean isOnGround = false;
    
    // Key states
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    
    @Override
    public void start(Stage primaryStage) {
        // Create game container
        Pane root = new Pane();
        
        // Load level from file
        loadLevelFromFile("level.txt", root);
        
        // Create scene
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);
        
        // Handle keyboard input
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = true;
            if (e.getCode() == KeyCode.SPACE) jumpPressed = true;
        });
        
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = false;
            if (e.getCode() == KeyCode.SPACE) jumpPressed = false;
        });
        
        // Game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
        
        // Set up stage
        primaryStage.setTitle("Tile Platformer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void loadLevelFromFile(String filename, Pane root) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int row = 0;
            
            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char tile = line.charAt(col);
                    
                    if (tile == 'X') {
                        // Create platform tile
                        Rectangle platform = new Rectangle(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        platform.setFill(Color.WHITE);
                        platform.setStroke(Color.BLACK);
                        platforms.add(platform);
                        root.getChildren().add(platform);
                    } else if (tile == 'P') {
                        // Create player at this position
                        player = new Circle(col * TILE_SIZE + TILE_SIZE / 2, 
                                           row * TILE_SIZE + TILE_SIZE / 2, 
                                           TILE_SIZE / 2 - 2, Color.RED);
                        root.getChildren().add(player);
                    }
                }
                row++;
            }
        } catch (IOException e) {
            System.err.println("Error loading level: " + e.getMessage());
            
            // Create default player if level loading fails
            if (player == null) {
                player = new Circle(WIDTH / 2, HEIGHT / 2, TILE_SIZE / 2 - 2, Color.RED);
                root.getChildren().add(player);
            }
        }
    }
    
    private void update() {
        // Apply horizontal movement
        if (leftPressed) velocityX = -MOVE_SPEED;
        else if (rightPressed) velocityX = MOVE_SPEED;
        else velocityX = 0;
        
        // Apply jump if on ground
        if (jumpPressed && isOnGround) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
        }
        
        // Apply gravity
        velocityY += GRAVITY;
        
        // Update player position (with collision checking)
        moveWithCollision();
        
        // Handle screen boundaries
        if (player.getCenterX() < player.getRadius()) player.setCenterX(player.getRadius());
        if (player.getCenterX() > WIDTH - player.getRadius()) player.setCenterX(WIDTH - player.getRadius());
        if (player.getCenterY() < player.getRadius()) player.setCenterY(player.getRadius());
        if (player.getCenterY() > HEIGHT - player.getRadius()) {
            player.setCenterY(HEIGHT - player.getRadius());
            velocityY = 0;
            isOnGround = true;
        }
    }
    
    private void moveWithCollision() {
        // Reset ground state
        isOnGround = false;
        
        // Handle X-axis movement and collision
        player.setCenterX(player.getCenterX() + velocityX);
        for (Rectangle platform : platforms) {
            if (isColliding(player, platform)) {
                // Determine collision side on X-axis
                if (velocityX > 0) {
                    // Moving right, collision on right side of player
                    player.setCenterX(platform.getX() - player.getRadius());
                } else if (velocityX < 0) {
                    // Moving left, collision on left side of player
                    player.setCenterX(platform.getX() + platform.getWidth() + player.getRadius());
                }
                velocityX = 0;
                break;
            }
        }
        
        // Handle Y-axis movement and collision
        player.setCenterY(player.getCenterY() + velocityY);
        for (Rectangle platform : platforms) {
            if (isColliding(player, platform)) {
                // Determine collision side on Y-axis
                if (velocityY > 0) {
                    // Moving down, collision on bottom of player (landing)
                    player.setCenterY(platform.getY() - player.getRadius());
                    isOnGround = true;
                } else if (velocityY < 0) {
                    // Moving up, collision on top of player (hitting ceiling)
                    player.setCenterY(platform.getY() + platform.getHeight() + player.getRadius());
                }
                velocityY = 0;
                break;
            }
        }
    }
    
    private boolean isColliding(Circle player, Rectangle platform) {
        // Find closest point on rectangle to circle center
        double closestX = Math.max(platform.getX(), Math.min(player.getCenterX(), platform.getX() + platform.getWidth()));
        double closestY = Math.max(platform.getY(), Math.min(player.getCenterY(), platform.getY() + platform.getHeight()));
        
        // Calculate distance between closest point and circle center
        double distanceX = player.getCenterX() - closestX;
        double distanceY = player.getCenterY() - closestY;
        double distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        
        // Check if distance is less than circle radius
        return distanceSquared < (player.getRadius() * player.getRadius());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}