import javafx.geometry.*;

import javafx.scene.control.*;
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
    private Label lbl_score;
    private Label lbl_comment;
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
        
        lbl_title = new Label("");
        lbl_title.getStyleClass().add("title_lbl");
        
        lbl_score = new Label("");
        lbl_score.getStyleClass().add("normal_lbl");
        
        lbl_comment = new Label("");
        lbl_comment.setWrapText(true);
        lbl_comment.getStyleClass().add("comment_lbl");
        

        btn_return = new Button("MAIN MENU");
        btn_return.getStyleClass().add("button");
        btn_return.setOnAction(event -> gameManager.showTitleScreen());

        btn_exit = new Button("EXIT");
        btn_exit.getStyleClass().add("button");
        btn_exit.setOnAction(event -> System.exit(0));
        
        container.getChildren().addAll(lbl_title, lbl_score, lbl_comment, btn_return, btn_exit);
        
        root.getChildren().addAll(container);
    } 
    
    public void displayOutcome(boolean win, int score, String comment){
        if (win){
            lbl_title.setText("YOU ESCAPED!");
        }
        else{
            lbl_title.setText("GAME OVER!");
        }
        
        lbl_score.setText("Score: "+score);
        lbl_comment.setText(comment);
    }

}
