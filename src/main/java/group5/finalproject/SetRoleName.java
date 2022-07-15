package group5.finalproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SetRoleName {
    @FXML
    TextField tfPlayer1Name;
    @FXML
    Button btnPlayer1Set;
    @FXML
    Button btnPlayer1Cancel;
    @FXML
    TextField tfPlayer2Name;
    @FXML
    Button btnPlayer2Set;
    @FXML
    Button btnPlayer2Cancel;

    void nextScene() throws IOException {
        FXMLLoader fxmlLoaderRole = new FXMLLoader(Main.class.getResource("selectRole.fxml"));
        Main.selectRoleScene = new Scene(fxmlLoaderRole.load());
        Main.selectRoleScene.getRoot().requestFocus();
        Main.currentStage.setScene(Main.selectRoleScene);
    }

    @FXML
    public void onBackPress() {
        previousScene();
    }

    @FXML
    public void onPlayer1SetPressed() throws IOException {
        tfPlayer1Name.setEditable(false);
        Main.player1.name = tfPlayer1Name.getText();
        btnPlayer1Set.setDisable(true);
        btnPlayer1Cancel.setDisable(false);

        if (btnPlayer2Set.isDisable()) {
            nextScene();
        }
    }

    @FXML
    public void onPlayer1ModifyPressed() throws IOException {
        tfPlayer1Name.setEditable(true);
        Main.player1.name = tfPlayer1Name.getText();
        btnPlayer1Set.setDisable(false);
        btnPlayer1Cancel.setDisable(true);
    }

    @FXML
    public void onPlayer2SetPressed() throws IOException {
        tfPlayer2Name.setEditable(false);
        Main.player2.name = tfPlayer2Name.getText();
        btnPlayer2Set.setDisable(true);
        btnPlayer2Cancel.setDisable(false);

        if (btnPlayer1Set.isDisable()) {
            nextScene();
        }
    }

    @FXML
    public void onPlayer2ModifyPressed() throws IOException {
        tfPlayer2Name.setEditable(true);
        Main.player2.name = tfPlayer2Name.getText();
        btnPlayer2Set.setDisable(false);
        btnPlayer2Cancel.setDisable(true);
    }

    void previousScene() {
        Main.currentStage.setScene(Main.startMenuScene);
    }
}
