import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.shape.Rectangle;

public class FileReader {
    private File level_definition;
    private int Width;
    private int Height;
    
    public FileReader() {
        level_definition = new File("level.txt");
        
    }
    
    private void loadinfo() {
        int line_number = 1;
        try {
            Scanner file = new Scanner(level_definition);
            while (file.hasNextLine()) {
                String line = file.nextLine();
                line.trim();
                if (line_number == 1) {
                    Width = line.length();
                    Width = Width * 50;
                }
                else {
                    line.substring(1, line.length());
                    if (line.isEmpty()){
                        line_number++;
                        continue;
                    }
                    for (char c : line.toCharArray()) {
                        if (c == 'X') {
                            Rectangle terrain = new Rectangle(50, 50);
                            
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("The level file does not exit");
            e.printStackTrace();
        }
    }
}