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
        aboutOpt.setOnAction(event -> showAboutDialog());
        MenuItem controlOpt = new MenuItem("Controls");
        controlOpt.setOnAction(event -> showControls());
        MenuItem guideOpt = new MenuItem("Guide");
        guideOpt.setOnAction(event -> showGuideDialog());
        helpMenu.getItems().addAll(aboutOpt, controlOpt, guideOpt);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }   
    
    /**
     * Where subclasses add more elements to the scene
     */
    protected abstract void setContent();
    
    public Scene getScene(){
        // Prevents having to reload a new screen each time it is called.
        if (scene == null){
             scene = new Scene(root, width, height);
             scene.getStylesheets().add(getClass().getResource("/format.css").toExternalForm());
        }       
        return scene;
    }
    
    private void showAboutDialog(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Welcome to SUPER LARIO. \n\nDeveloped by: \n - Aldrich Fernandes\n - Mehdi Belhadj\n - Fadi Mostefai\n - Kishan Prakash");
        alert.showAndWait();
    }
    
    private void showControls(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Controls");
        alert.setHeaderText(null);
        alert.setContentText("  D   - Move right\n"+"  A   - Move left\n"+"SPACE - Jump\n");
        alert.showAndWait();
    }
    
    private void showGuideDialog(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Guide");
        alert.setHeaderText(null);
        alert.setContentText("Goals: \n 1). Locate the Key \n 2). Collect Coins \n 3). Escape ");
        alert.showAndWait();
    }
}
