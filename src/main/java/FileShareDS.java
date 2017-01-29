/**
 * Created by nifras on 1/12/17.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class FileShareDS extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileShareDS.fxml"));
        Parent root = fxmlLoader.load();
        
        root.setCache(true);
        root.setCacheHint(CacheHint.DEFAULT);
        
        Scene scene = new Scene(root);//, 550, 605);
//        scene.getStylesheets().add(getClass().getResource("theme.css").toExternalForm());


//        Image ico = new Image(getClass().getResourceAsStream("appIcon.png"));
//        primaryStage.getIcons().add(ico);
        
        
        System.out.println("show");
        //FlatterFX.style();
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        
        primaryStage.setResizable(false);
        primaryStage.setTitle("FileShareDS");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FileShareDSController fileShareDSController = fxmlLoader.<FileShareDSController>getController();
                fileShareDSController.init();
                fileShareDSController.setStage(primaryStage);
                
            }
        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
