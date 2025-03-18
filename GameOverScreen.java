import javafx.geometry.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Write a description of class TitleScreen here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class GameOverScreen extends BaseScreen
{
    private Label lbl_title;
    private Label lbl_cause;
    private Button btn_return;
    private Button btn_exit;

    /**
     * Constructor for objects of class TitleScreen
     */
    public GameOverScreen(GameManager gameManager, int width, int height)
    {
        super(gameManager, width, height);
        setContent();
    }
    
    @Override
    protected void setContent(){
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        
        lbl_title = new Label("GAME OVER!");
        lbl_title.getStyleClass().add("title_lbl");
        
        lbl_cause = new Label("Cause: ");
        lbl_cause.getStyleClass().add("normal_lbl");

        btn_return = new Button("MAIN MENU");
        btn_return.getStyleClass().add("button");
        btn_return.setOnAction(event -> gameManager.showTitleScreen());

        btn_exit = new Button("EXIT");
        btn_exit.getStyleClass().add("button");
        btn_exit.setOnAction(event -> System.exit(0));
        
        container.getChildren().addAll(lbl_title, lbl_cause, btn_return, btn_exit);
        
        root.getChildren().addAll(container);
    } 
    
    public void setCause(String cause){
        if (cause == null){
            cause = "";
        }
        lbl_cause.setText("Cause: "+cause);
    }
}
