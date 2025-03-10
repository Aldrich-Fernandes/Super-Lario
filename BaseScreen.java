import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

/**
 * The opening Screen when the user starts the game
 */
public abstract class BaseScreen
{
    protected GameManager gameManager;
    protected Scene scene;
    protected VBox root;
    
    private int width;
    private int height;
    
    public BaseScreen(GameManager gameManager, int width, int height)
    {
        this.gameManager = gameManager;
        this.width = width;
        this.height = height;
        
        root = new VBox(10);
        root.getChildren().add(makeMenuBar());
    }
    
    private MenuBar makeMenuBar(){        
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem quitOpt = new MenuItem("Quit");
        quitOpt.setOnAction(event -> System.exit(0));
        fileMenu.getItems().add(quitOpt);
        
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutOpt = new MenuItem("About");
        quitOpt.setOnAction(event -> showAboutDialog());
        MenuItem controlOpt = new MenuItem("Controls");
        controlOpt.setOnAction(event -> showControls());
        helpMenu.getItems().addAll(aboutOpt, controlOpt);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }   
    
    /**
     * Where subclasses add more elements to the scene
     */
    protected abstract void setContent();
    
    public Scene getScene(){
        scene = new Scene(root, width, height);        
        return scene;
    }
    
    private void showAboutDialog(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setContentText("Some information about the game I am to lazy to write now.");
        
        alert.showAndWait();
    }
    
    private void showControls(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Controls");
        alert.setContentText("Some information about the controls I am also to lazy to write now.");
        
        alert.showAndWait();
    }
}
