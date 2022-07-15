package group5.finalproject;

import javafx.fxml.FXML;

import java.io.IOException;

public class ctr_StartMenu {
    @FXML
    public void onStartPressed() throws IOException {
        Main.setRoleNameScene.getRoot().requestFocus();
        Main.currentStage.setScene(Main.setRoleNameScene);
    }

    @FXML
    public void onExitPressed() throws IOException {
        Main.currentStage.close();
        System.exit(0);
    }
}