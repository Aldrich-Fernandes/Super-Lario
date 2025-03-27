import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles level generation and management for the game.
 */
public class LevelManager {
    private List<String> levelPaths;
    private final Random rand;
    
    /**
     * Creates a level manager that loads available levels.
     */
    public LevelManager() {
        loadLevelPaths();
        rand = new Random();
    }
    
    /**
     * Scans the Levels directory and collects paths to all .txt files.
     */
    private void loadLevelPaths() {
        try {
            
            // Get the path to the Levels directory
            File levelsDir = new File("Levels");
            
            levelPaths = new ArrayList<>();
            
            // Check if directory exists
            if (!levelsDir.exists() || !levelsDir.isDirectory()) {
                System.err.println("Levels directory not found!");
                return;
            }
            
            // List all files in the directory
            File[] files = levelsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Only add .txt files
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".txt") && !file.getName().equals("playerRoom.txt") && !file.getName().equals("keyRoom.txt") && !file.getName().equals("endRoom.txt")) {
                        levelPaths.add(file.getPath());
                        //System.out.println("Found level: " + file.getPath());
                    }
                }
            }
            
            //System.out.println("Total levels found: " + levelPaths.size());
        } catch (Exception e) {
            System.err.println("Error loading levels: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a random room for available level files.
     */
    public GameMap generateRandomRoom() {
        int randIndex = rand.nextInt(levelPaths.size());
        GameMap newMap = new GameMap(levelPaths.get(randIndex));
        levelPaths.remove(randIndex);
        return newMap;
    }
    
    /**
     * Generate a complete game level ( includes player start, key and exit rooms).
     */
    public GameMap[] generateLevel() {
        GameMap[] levelMaps = new GameMap[Game.NO_OF_SCREENS];
        levelMaps[0] = new GameMap("Levels/playerRoom.txt");
        levelMaps[rand.nextInt(1, Game.NO_OF_SCREENS-1)] = new GameMap("Levels/keyRoom.txt");
        levelMaps[Game.NO_OF_SCREENS-1] = new GameMap("Levels/endRoom.txt");
        
        // Picks individual rooms for the level
        for (int i = 0; i < Game.NO_OF_SCREENS; i++) {
            if (levelMaps[i] == null) {
                levelMaps[i] = generateRandomRoom();
            }
        }
        
        return levelMaps;
    }
    
}