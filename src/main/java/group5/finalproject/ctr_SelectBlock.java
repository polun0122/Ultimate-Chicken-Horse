package group5.finalproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class SelectBlock implements EventHandler<KeyEvent> {
    int player1Index = 0; /* 玩家1 框選方塊位置指標 */
    int player2Index = 0; /* 玩家2 框選方塊位置指標 */
    boolean isP1Selected = false; /* 玩家1 是否已選方塊 */
    boolean isP2Selected = false; /* 玩家2 是否已選方塊 */
    int eachRoundBlockNumber = 5; /* 每回合隨機生成方塊數量 */
    Block[] BlockList = new Block[eachRoundBlockNumber]; /* 儲存隨機生成的方塊 */

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
    Label labelRoundCount; /* 標題顯示回合數 */
    @FXML
    GridPane gridPaneSelect; /* 顯示可選擇的方塊表格 */
    @FXML
    Button btnBack; /* 上一步的按鈕 */

    @FXML
    public void initialize() {
        /* 場景初始化 */
        labelPlayer1Name.setText(Main.player1.name + "     分數: " + Main.player1.score);
        labelPlayer2Name.setText(Main.player2.name + "     分數: " + Main.player2.score);
        framePlayer1Selector.setVisible(false);
        framePlayer2Selector.setVisible(false);
        labelRoundCount.setText("回合 " + Main.roundCount); /* 更新標題回合數 */
        if (Main.roundCount != 1) {
            /* 若是已開始遊戲(非第一次選方塊)則不得選上一步回到角色選擇 */
            btnBack.setVisible(false);
        }
        updateBlocksOnGrid(); /* 生成新的五個隨機方塊，並更新顯示畫面 */
    }

    @FXML
    public void onBackPress() {
        /* 按下上一步按鈕 */
        previousScene();
    }

    private void previousScene() {
        /* 切換至上一個場景 */
        Main.currentStage.setScene(Main.selectRoleScene);
    }

    private void updateBlocksOnGrid() {
        /* 生成新的五個隨機方塊，並更新顯示畫面 */
        List<Node> gpList = gridPaneSelect.getChildren(); /* 取得表格內的各自子表格(用來顯示生成的方塊) */
        for (int i = 0; i < BlockList.length; i++) {
            BlockList[i] = new Block();
            // System.out.println(BlockList[i].getShape() + " " + BlockList[i].getType());
            GridPane gpTemp = (GridPane) gpList.get(i);
            List<Node> blockUnits = gpTemp.getChildren();
            for (int j = 0; j < blockUnits.size(); j++) {
                Rectangle rectangleTemp = (Rectangle) blockUnits.get(j);
                rectangleTemp.setStroke(BlockList[i].getType().getFrameColor());
                rectangleTemp.setFill(BlockList[i].getType().getFillColor());
                if (BlockList[i].getShape() == BlockShape.Hero)
                    GridPane.setRowIndex(rectangleTemp, BlockList[i].getShape().getDistribution()[j][0] + 2);
                else
                    GridPane.setRowIndex(rectangleTemp, BlockList[i].getShape().getDistribution()[j][0] + 1);
                if (BlockList[i].getShape() == BlockShape.Smashboy)
                    GridPane.setColumnIndex(rectangleTemp, BlockList[i].getShape().getDistribution()[j][1] + 1);
                else
                    GridPane.setColumnIndex(rectangleTemp, BlockList[i].getShape().getDistribution()[j][1]);
            }
        }
    }

    @Override
    public void handle(KeyEvent e) {
        //System.out.println(e.getCode());
        if (e.getCode() == KeyCode.ESCAPE) {
            /* 回到上一場景，效果同按下"上一步"按鈕 */
            previousScene();
        }
        if (e.getCode() == KeyCode.SPACE) {
            // Player 1 決定
            Main.player1.currentBlock = BlockList[player1Index];
            isP1Selected = true;
            framePlayer1Selector.setFill(Paint.valueOf("#0088ff3f"));
            if (isP2Selected) {
                // 延遲1秒後轉場
                nextSceneDelayed();
            } else if (player2Index == player1Index) {
                player2Index += 1;
                player2Index = checkRowLimit(player2Index);
            }
        }
        if (e.getCode() == KeyCode.ENTER) {
            // Player 2 決定
            Main.player2.currentBlock = BlockList[player2Index];
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
            return 4;
        if (index > 4)
            return 0;
        return index;
    }

    void updatePosition() {
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

    public void nextSceneDelayed() {
        /* 延遲數秒後轉至下一場景 */
        int delayedSecond = 1; /* 延遲秒數 */
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delayedSecond), ev -> {
            FXMLLoader fxmlLoaderMap = new FXMLLoader(Main.class.getResource("playingMap.fxml"));
            try {
                Main.playingMapScene = new Scene(fxmlLoaderMap.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Main.playingMapScene.getRoot().requestFocus();
            Main.currentStage.setScene(Main.playingMapScene);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
