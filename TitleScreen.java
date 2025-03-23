import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Write a description of class TitleScreen here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class TitleScreen extends BaseScreen
{
   
    private Label lbl_title;
    private Button btn_play;
    private Button btn_exit;
    private Button btn_resume;

    /**
     * Constructor for objects of class TitleScreen
     */
    public TitleScreen(GameManager gameManager, int width, int height)
    {
        super(gameManager, width, height);
        setContent();
    }
    
    @Override
    protected void setContent(){
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        
        lbl_title = new Label("SUPER LARIO");
        lbl_title.getStyleClass().add("title_lbl");

        btn_play = new Button("PLAY");
        btn_play.getStyleClass().add("button");
        btn_play.setOnAction(event -> gameManager.startGame(false));
        
        btn_resume = new Button("RESUME");
        btn_resume.getStyleClass().add("button");
        btn_resume.setOnAction(event -> gameManager.startGame(true));
        
        btn_exit = new Button("EXIT");
        btn_exit.getStyleClass().add("button");
        btn_exit.setOnAction(event -> System.exit(0));
        
        container.getChildren().addAll(lbl_title, btn_play, btn_resume, btn_exit);
        
        root.getChildren().addAll(container);
    }    
}
