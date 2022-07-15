package group5.finalproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage currentStage;
    public static Scene startMenuScene;
    public static Scene setRoleNameScene;
    public static Scene selectRoleScene;
    public static Scene selectBlockScene;
    public static Scene playingMapScene;
    public static Player player1;
    public static Player player2;
    public static int roundCount = 1;
    public static int[][] blockDistribution = new int[10][12]; /* 以陣列儲存地圖方塊 */

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoaderMenu = new FXMLLoader(Main.class.getResource("startMenu.fxml"));
        FXMLLoader fxmlLoaderSetName = new FXMLLoader(Main.class.getResource("setRoleName.fxml"));

        player1 = new Player(1);
        player2 = new Player(2);

        currentStage = primaryStage;
        startMenuScene = new Scene(fxmlLoaderMenu.load());
        setRoleNameScene = new Scene(fxmlLoaderSetName.load());
        currentStage.setTitle("Ultimate Chicken Horse");
        currentStage.setScene(startMenuScene);
        currentStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}