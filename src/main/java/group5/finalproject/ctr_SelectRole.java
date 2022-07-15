package group5.finalproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;

public class SelectRole implements EventHandler<KeyEvent> {
    int player1Index = 0; /* 玩家1 框選方塊位置指標 */
    int player2Index = 0; /* 玩家2 框選方塊位置指標 */
    boolean isP1Selected = false; /* 玩家1 是否已確定角色 */
    boolean isP2Selected = false; /* 玩家2 是否已確定角色 */

    @FXML
    Rectangle framePlayer1Selector; /* 玩家1 框選方塊 */
    @FXML
    Rectangle framePlayer2Selector; /* 玩家2 框選方塊 */
    @FXML
    Rectangle frameBothSelector; /* 玩家1、2 同時框選方塊 */
    @FXML
    Label labelPlayer1Name; /* 玩家1顯示名稱 */
    @FXML
    Label labelPlayer2Name; /* 玩家2顯示名稱 */

    @FXML
    public void initialize() {
        /* 場景初始化 */
        labelPlayer1Name.setText(Main.player1.name); /* 設定玩家1 顯示名稱 */
        labelPlayer2Name.setText(Main.player2.name); /* 設定玩家2 顯示名稱 */
        framePlayer1Selector.setVisible(false);
        framePlayer2Selector.setVisible(false);
    }

    @FXML
    public void onBackPress() {
        /* 按下上一鍵按鈕 */
        previousScene();
    }

    @Override
    public void handle(KeyEvent e) {
        // System.out.println(e.getCode());
        if (e.getCode() == KeyCode.ESCAPE) {
            previousScene();
        }
        if (e.getCode() == KeyCode.SPACE) {
            // Player 1 決定
            Main.player1.type = animalIndex(player1Index);
            isP1Selected = true;
            framePlayer1Selector.setFill(Paint.valueOf("#0088ff3f"));
            if (isP2Selected) {
                // 延遲2秒後轉場
                nextSceneDelayed();
            } else if (player2Index == player1Index) {
                player2Index += 1;
                player2Index = checkRowLimit(player2Index);
            }
        }
        if (e.getCode() == KeyCode.ENTER) {
            // Player 2 決定
            Main.player2.type = animalIndex(player2Index);
            isP2Selected = true;
            framePlayer2Selector.setFill(Paint.valueOf("#ff00003f"));
            if (isP1Selected) {
                // 延遲1秒後轉場
                nextSceneDelayed();
            } else if (player1Index == player2Index) {
                player1Index += 1;
                player1Index = checkRowLimit(player1Index);
            }
        }
        if (!isP1Selected) {
            if (e.getCode() == KeyCode.A) {
                // Player1 左移
                player1Index -= 1;
                // ----超出邊界處理----
                player1Index = checkRowLimit(player1Index);
                // ----跳過另一玩家已選角色----
                if (isP2Selected && player1Index == player2Index) {
                    player1Index -= 1;
                }
                // ----超出邊界處理----
                player1Index = checkRowLimit(player1Index);
            }
            if (e.getCode() == KeyCode.D) {
                // Player1 右移
                player1Index += 1;
                // ----超出邊界處理----
                player1Index = checkRowLimit(player1Index);
                // ----跳過另一玩家已選角色----
                if (isP2Selected && player1Index == player2Index) {
                    player1Index += 1;
                }
                // ----超出邊界處理----
                player1Index = checkRowLimit(player1Index);
            }
        }
        if (!isP2Selected) {
            if (e.getCode() == KeyCode.LEFT) {
                // Player2 左移
                player2Index -= 1;
                // ----超出邊界處理----
                player2Index = checkRowLimit(player2Index);
                // ----跳過另一玩家已選角色----
                if (isP1Selected && player2Index == player1Index) {
                    player2Index -= 1;
                }
                // ----超出邊界處理----
                player2Index = checkRowLimit(player2Index);
            }
            if (e.getCode() == KeyCode.RIGHT) {
                // Player2 右移
                player2Index += 1;
                // ----超出邊界處理----
                player2Index = checkRowLimit(player2Index);
                // ----跳過另一玩家已選角色----
                if (isP1Selected && player2Index == player1Index) {
                    player2Index += 1;
                }
                // ----超出邊界處理----
                player2Index = checkRowLimit(player2Index);
            }
        }
        updatePosition();
    }

    int checkRowLimit(int index) {
        if (index < 0)
            return 3;
        if (index > 3)
            return 0;
        return index;
    }

    void updatePosition() {
        /* 更新圈選框的位置 */
        if (player1Index == player2Index) {
            GridPane.setColumnIndex(frameBothSelector, player1Index);
            frameBothSelector.setVisible(true);
            framePlayer1Selector.setVisible(false);
            framePlayer2Selector.setVisible(false);
        } else {
            GridPane.setColumnIndex(framePlayer1Selector, player1Index);
            GridPane.setColumnIndex(framePlayer2Selector, player2Index);
            frameBothSelector.setVisible(false);
            framePlayer1Selector.setVisible(true);
            framePlayer2Selector.setVisible(true);
        }
    }

    String animalIndex(int idx) {
        return switch (idx) {
            case 0 -> "Chicken";
            case 1 -> "Horse";
            case 2 -> "Monkey";
            case 3 -> "Raccoon";
            default -> "undefined";
        };
    }

    void nextScene() throws IOException {
        /* 不延遲直接轉至下一場景 */
        FXMLLoader fxmlLoaderBlock = new FXMLLoader(Main.class.getResource("selectBlock.fxml"));
        Main.selectBlockScene = new Scene(fxmlLoaderBlock.load());
        Main.selectBlockScene.getRoot().requestFocus();
        Main.currentStage.setScene(Main.selectBlockScene);
    }

    public void nextSceneDelayed() {
        /* 延遲數秒後轉至下一場景 */
        int delayedSecond = 1; /* 延遲秒數 */
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delayedSecond), ev -> {
            FXMLLoader fxmlLoaderBlock = new FXMLLoader(Main.class.getResource("selectBlock.fxml"));
            try {
                Main.selectBlockScene = new Scene(fxmlLoaderBlock.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Main.selectBlockScene.getRoot().requestFocus();
            Main.currentStage.setScene(Main.selectBlockScene);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    void previousScene() {
        Main.currentStage.setScene(Main.setRoleNameScene);
    }
}
