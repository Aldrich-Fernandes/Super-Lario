import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;

/**
 * The opening Screen when the user starts the game
 */
public class TitleScreen
{
    // Elements
    private Scene introScreen;

    /**
     * Constructor for objects of class StartScreen
     */
    public void StartScreen()
    {
        VBox root = new VBox();
        root.getChildren().add(makeMenuBar());

        Scene introScreen = new Scene(root, 600, 600);
        
    }
    
    private VBox makeMenuBar(){        
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem quitOpt = new MenuItem("Quit");
        fileMenu.getItems().add(quitOpt);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutOpt = new MenuItem("About");
        helpMenu.getItems().add(aboutOpt);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return new VBox(menuBar);
    }
    
    public Scene getScene(){
        return introScreen;
    }
   
}
