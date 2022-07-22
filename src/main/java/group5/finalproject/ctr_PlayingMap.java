package group5.finalproject;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ctr_PlayingMap implements EventHandler<KeyEvent> {
    boolean isPlayer1BlockPlaced = false; /* 玩家1 的方塊是否已放好 */
    boolean isPlayer2BlockPlaced = false; /* 玩家2 的方塊是否已放好 */
    boolean isPlayer1DeadHandled = false; /* 玩家1 死亡後的處理是否完成 */
    boolean isPlayer2DeadHandled = false; /* 玩家2 死亡後的處理是否完成 */
    boolean isPlayer1Jumping = false; /* 玩家1 是否正在跳 */
    boolean isPlayer2Jumping = false; /* 玩家2 是否正在跳 */

    /* 定時器，腳色可移動後每0.01秒執行一次 */
    Timeline timerUpdate = new Timeline(new KeyFrame(Duration.seconds(0.01), ev -> {
        /* 更新腳色位置 */
        updateAllPlayersOnMap();
        /* Player 1 */
        if (isPlayer1Jumping) {  /* 彈跳獨立出來判斷才能實現斜上跳躍 */
            Main.player1.jump();
        }
        switch (Main.player1.getStatus()) {
            case Dead:
                if (!isPlayer1DeadHandled) {
                    /* 死亡後處理 */
                    isPlayer1Jumping = false;
                    setRoleDeadImage(Main.player1);
                    isPlayer1DeadHandled = true;
                }
                break;
            case LeftMove:
                Main.player1.moveLeft();
                break;
            case RightMove:
                Main.player1.moveRight();
                break;
        }
        /* Player 2 */
        if (isPlayer2Jumping) {  /* 彈跳獨立出來判斷才能實現斜上跳躍 */
            Main.player2.jump();
        }
        switch (Main.player2.getStatus()) {
            case Dead:
                if (!isPlayer2DeadHandled) {
                    /* 死亡後處理 */
                    isPlayer2Jumping = false;
                    setRoleDeadImage(Main.player2);
                    isPlayer2DeadHandled = true;
                }
                break;
            case LeftMove:
                Main.player2.moveLeft();
                break;
            case RightMove:
                Main.player2.moveRight();
                break;
        }
        /* 判斷回合是否結束 */
        if (Main.player1.getStatus() == Player.Status.Dead || Main.player1.getStatus() == Player.Status.Finish) {
            if (Main.player2.getStatus() == Player.Status.Dead || Main.player2.getStatus() == Player.Status.Finish) {
                showResult();
            }
        }
    }));

    @FXML
    ImageView imagePlayer1; /* 玩家1腳色圖片 */
    @FXML
    ImageView imagePlayer2; /* 玩家2腳色圖片 */
    @FXML
    GridPane gridPaneBlocks; /* 地圖中放置方塊的表格 */
    @FXML
    AnchorPane scene;
    @FXML
    Pane resultTable; /* 回合結果視窗 */
    @FXML
    Label resultTitle; /* 回合結果視窗標題 */
    @FXML
    Label player1Name; /* 回合結果-玩家1名稱 */
    @FXML
    Label player2Name; /* 回合結果-玩家2名稱 */
    @FXML
    Label player1Score; /* 回合結果-玩家1分數 */
    @FXML
    Label player2Score; /* 回合結果-玩家2分數 */
    @FXML
    ProgressBar player1ScoreBar; /* 回合結果-玩家1分數進度條 */
    @FXML
    ProgressBar player2ScoreBar; /* 回合結果-玩家2分數進度條 */
    @FXML
    Button btnNextRound; /* 回合結果-下一回合按鈕 */
    @FXML
    Button btnExit; /* 回合結果-結束遊戲 */

    @FXML
    public void initialize() {
        /* 場景初始化 */
        resultTable.setVisible(false);
        Main.player1.blockIndex = new int[]{0, 0};
        Main.player2.blockIndex = new int[]{2, 0};
        setRoleDirection(Main.player1, "Right");
        setRoleDirection(Main.player2, "Right");
        updateBlocksOnMap();
        previewBlockOnMap(Main.player1);
        previewBlockOnMap(Main.player2);
        /* 初始化腳色狀態 */
        Main.player1.reset();
        Main.player2.reset();
        /* 腳色起點設定 */
        Main.player1.setInitialPosition(60);
        Main.player2.setInitialPosition(10);
        updateAllPlayersOnMap();
    }

    boolean placedBlockOnMap(Player player) {
        /* 將確定位置的方塊更新到Main.blockDistribution中 */
        int iOrigin = player.blockIndex[0];
        int jOrigin = player.blockIndex[1];
        /* 先檢查有無與目前方塊重疊(非炸藥方塊) */
        if (player.currentBlock.getType() != BlockType.Tnt) {
            for (int[] temp : player.currentBlock.getShape().getDistribution()) {
                int iIndex = iOrigin + temp[0];
                int jIndex = jOrigin + temp[1];
                if (Main.blockDistribution[iIndex][jIndex] != 0) {
                    /* 方塊與原地圖方塊重疊，放置失敗 */
                    return false;
                }
            }
        }
        /* 設置方塊於地圖中 */
        for (int[] temp : player.currentBlock.getShape().getDistribution()) {
            int iIndex = iOrigin + temp[0];
            int jIndex = jOrigin + temp[1];
            if (player.currentBlock.getType() == BlockType.Tnt) {
                Main.blockDistribution[iIndex][jIndex] = 0;
            } else {
                Main.blockDistribution[iIndex][jIndex] = player.currentBlock.getType().getNumber();
            }
        }
        return true;
    }

    void previewBlockOnMap(Player player) {
        /* 預覽還沒確定放置的方塊位置 */
        if (player.currentBlock == null) {
            return;
        }
        List<Node> nodes = gridPaneBlocks.getChildren();
        int blockIndex = 0;
        int iOrigin;
        int jOrigin;
        /* 超出邊界檢查 */
        if (player.blockIndex[0] < 0) {
            player.blockIndex[0] = 0;
        } else if (player.blockIndex[0] + player.currentBlock.getShape().getHeight() > 9) {
            player.blockIndex[0] = 9 - player.currentBlock.getShape().getHeight();
        }
        if (player.blockIndex[1] < 0) {
            player.blockIndex[1] = 0;
        } else if (player.blockIndex[1] + player.currentBlock.getShape().getWidth() > 11) {
            player.blockIndex[1] = 11 - player.currentBlock.getShape().getWidth();
        }
        iOrigin = player.blockIndex[0];
        jOrigin = player.blockIndex[1];

        int iPlaced = iOrigin + player.currentBlock.getShape().getDistribution()[blockIndex][0];
        int jPlaced = jOrigin + player.currentBlock.getShape().getDistribution()[blockIndex][1];
        for (int i = 0; i < Main.blockDistribution.length; i++) {
            for (int j = 0; j < Main.blockDistribution[0].length; j++) {
                if (i == iPlaced && j == jPlaced) {
                    Rectangle rectangleTemp = (Rectangle) nodes.get(j + i * Main.blockDistribution[0].length);
                    rectangleTemp.setStroke(player.currentBlock.getType().getFrameColor());
                    rectangleTemp.setFill(player.currentBlock.getType().getFillColor());
                    rectangleTemp.setOpacity(0.5);
                    rectangleTemp.setVisible(true);
                    blockIndex += 1;
                    if (blockIndex == 4) {
                        //System.out.println("完成方塊預覽列印");
                        return;
                    }
                    iPlaced = iOrigin + player.currentBlock.getShape().getDistribution()[blockIndex][0];
                    jPlaced = jOrigin + player.currentBlock.getShape().getDistribution()[blockIndex][1];
                }
            }
        }
    }

    void updateBlocksOnMap() {
        /* 根據Main.blockDistribution更新方塊地圖 */
        List<Node> nodes = gridPaneBlocks.getChildren();
        for (int i = 0; i < Main.blockDistribution.length; i++) {
            for (int j = 0; j < Main.blockDistribution[0].length; j++) {
                Rectangle rectangleTemp = (Rectangle) nodes.get(j + i * Main.blockDistribution[0].length);
                switch (Main.blockDistribution[i][j]) {
                    case 1 -> { /* Normal */
                        rectangleTemp.setStroke(BlockType.Normal.getFrameColor());
                        rectangleTemp.setFill(BlockType.Normal.getFillColor());
                        rectangleTemp.setOpacity(1);
                        rectangleTemp.setVisible(true);
                    }
                    case 2 -> { /* Ice */
                        rectangleTemp.setStroke(BlockType.Ice.getFrameColor());
                        rectangleTemp.setFill(BlockType.Ice.getFillColor());
                        rectangleTemp.setOpacity(1);
                        rectangleTemp.setVisible(true);
                    }
                    case 3 -> { /* Honey */
                        rectangleTemp.setStroke(BlockType.Honey.getFrameColor());
                        rectangleTemp.setFill(BlockType.Honey.getFillColor());
                        rectangleTemp.setOpacity(1);
                        rectangleTemp.setVisible(true);
                    }
                    case 4 -> { /* Trap */
                        rectangleTemp.setStroke(BlockType.Trap.getFrameColor());
                        rectangleTemp.setFill(BlockType.Trap.getFillColor());
                        rectangleTemp.setOpacity(1);
                        rectangleTemp.setVisible(true);
                    }
                    default -> /* case 0 */
                            rectangleTemp.setVisible(false);
                }
            }
        }
    }

    @Override
    public void handle(KeyEvent e) {
        /* 初始(選擇方塊放置位置時)處理鍵盤事件 */
        if (!isPlayer1BlockPlaced) {
            /* 玩家1 更新方塊位置 */
            switch (e.getCode()) {
                case W:
                    /* 玩家1 方塊向上移動 */
                    Main.player1.blockIndex[0] -= 1;
                    break;
                case S:
                    /* 玩家1 方塊向下移動 */
                    Main.player1.blockIndex[0] += 1;
                    break;
                case D:
                    /* 玩家1 方塊向右移動 */
                    Main.player1.blockIndex[1] += 1;
                    break;
                case A:
                    /* 玩家1 方塊向左移動 */
                    Main.player1.blockIndex[1] -= 1;
                    break;
                case F:
                    /* 玩家1 旋轉*/
                    Main.player1.currentBlock.getShape().clockwiseRotate();
                    break;
                case SPACE:
                    /* 玩家1 確認 */
                    if (placedBlockOnMap(Main.player1)) {
                        Main.player1.currentBlock = null;
                        isPlayer1BlockPlaced = true;
                    }
                    break;
            }
        }
        if (!isPlayer2BlockPlaced) {
            /* 玩家2 更新方塊位置 */
            switch (e.getCode()) {
                case UP:
                    /* 玩家2 方塊向上移動 */
                    Main.player2.blockIndex[0] -= 1;
                    break;
                case DOWN:
                    /* 玩家2 方塊向下移動 */
                    Main.player2.blockIndex[0] += 1;
                    break;
                case RIGHT:
                    /* 玩家2 方塊向右移動 */
                    Main.player2.blockIndex[1] += 1;
                    break;
                case LEFT:
                    /* 玩家2 方塊向左移動 */
                    Main.player2.blockIndex[1] -= 1;
                    break;
                case DELETE:
                    /* 玩家2 旋轉*/
                    Main.player2.currentBlock.getShape().clockwiseRotate();
                    break;
                case ENTER:
                    /* 玩家2 確認 */
                    if (placedBlockOnMap(Main.player2)) {
                        Main.player2.currentBlock = null;
                        isPlayer2BlockPlaced = true;
                    }
                    break;
            }
        }
        if (!(isPlayer1BlockPlaced && isPlayer2BlockPlaced)) {
            /* 還有玩家沒放好方塊，進行放方塊地圖更新 */
            updateBlocksOnMap();
            previewBlockOnMap(Main.player1);
            previewBlockOnMap(Main.player2);
        } else {
            /* 兩個玩家都已放好方塊，進行遊玩功能 */
            updateBlocksOnMap();
            /* 去除方塊放置區域格線 */
            gridPaneBlocks.setGridLinesVisible(false);
            /* 定時器(更新腳色位置)開始，循環次數: 無限 */
            timerUpdate.setCycleCount(Animation.INDEFINITE);
            timerUpdate.play();
            /* 轉交鍵盤事件控制權 */
            scene.setOnKeyPressed(handlePressPlay);
            scene.setOnKeyReleased(handleReleasePlay);
        }
    }

    private final EventHandler<KeyEvent> handlePressPlay = new EventHandler<KeyEvent>() {
        /* (按下)腳色在地圖中自由移動時使用這個處理鍵盤事件 */
        @Override
        public void handle(KeyEvent e) {
            /* Player 1 */
            if (Main.player1.getStatus() != Player.Status.Dead && Main.player1.getStatus() != Player.Status.Finish) {
                switch (e.getCode()) {
                    case A:
                        /* 左移 */
                        setRoleDirection(Main.player1, "left");
                        Main.player1.moveLeft();
                        break;
                    case D:
                        /* 右移 */
                        setRoleDirection(Main.player1, "right");
                        Main.player1.moveRight();
                        break;
                    case W:
                        /* 跳躍 */
                        isPlayer1Jumping = true;
                        break;
                    case Q:
                        /* 放棄 */
                        Main.player1.giveUp();
                        break;
                }
//            System.out.println("Player1 Pos: " + imagePlayer1.getLayoutX() + " " + imagePlayer1.getLayoutY());
            }
            /* Player 2 */
            if (Main.player2.getStatus() != Player.Status.Dead && Main.player2.getStatus() != Player.Status.Finish) {
                switch (e.getCode()) {
                    case LEFT:
                        /* 左移 */
                        setRoleDirection(Main.player2, "left");
                        Main.player2.moveLeft();
                        break;
                    case RIGHT:
                        /* 右移 */
                        setRoleDirection(Main.player2, "right");
                        Main.player2.moveRight();
                        break;
                    case UP:
                        /* 跳躍 */
                        isPlayer2Jumping = true;
                        break;
                    case END:
                        /* 放棄 */
                        Main.player2.giveUp();
                        break;
                }
            }
        }
    };

    private final EventHandler<KeyEvent> handleReleasePlay = new EventHandler<>() {
        /* (鬆開)腳色在地圖中自由移動時使用這個處理鍵盤事件 */
        @Override
        public void handle(KeyEvent e) {
            // System.out.println("KeyRelease: " + e.getCode());
            /* Player 1 */
            if (Main.player1.getStatus() != Player.Status.Dead) {
                switch (e.getCode()) {
                    case A:
                        /* 停止左移 */
                        Main.player1.stopMoveLeft();
                        break;
                    case D:
                        /* 停止右移 */
                        Main.player1.stopMoveRight();
                        break;
                    case W:
                        /* 停止跳躍 */
                        isPlayer1Jumping = false;
                        break;
                }
            }
            /* Player 2 */
            if (Main.player2.getStatus() != Player.Status.Dead) {
                switch (e.getCode()) {
                    case LEFT:
                        /* 停止左移 */
                        Main.player2.stopMoveLeft();
                        break;
                    case RIGHT:
                        /* 停止右移 */
                        Main.player2.stopMoveRight();
                        break;
                    case UP:
                        /* 停止跳躍 */
                        isPlayer2Jumping = false;
                        break;
                }
            }
        }
    };

    void updateAllPlayersOnMap() {
        imagePlayer1.setLayoutX(Main.player1.position[0]);
        imagePlayer1.setLayoutY(Main.player1.position[1]);
        imagePlayer2.setLayoutX(Main.player2.position[0]);
        imagePlayer2.setLayoutY(Main.player2.position[1]);
    }

    void setRoleDirection(Player player, String left_right) {
        /* 設定腳色圖片面對方向 Input 1: "Right" | "Left, Input2: player */
        String imagePath = "/" + player.type + left_right + ".png";
        if (player.getNumber() == 1) {
            imagePlayer1.setImage(new Image(imagePath));
        } else {
            imagePlayer2.setImage(new Image(imagePath));
        }
    }

    void setRoleDeadImage(Player player) {
        /* 設定腳色圖片死亡圖片 */
        String imagePath = "/" + player.type + "Dead.png";
        if (player.getNumber() == 1) {
            imagePlayer1.setImage(new Image(imagePath));
        } else {
            imagePlayer2.setImage(new Image(imagePath));
        }
    }

    void showResult() {
        /* 延遲數秒後顯示結果 */
        timerUpdate.stop(); /* 停下自動更新腳色位置 */
        int delayedSecond = 2; /* 延遲秒數 */
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delayedSecond), ev -> {
            /* 結束頁面 */
            resultTitle.setText("第" + Main.roundCount + "回合結束~");
            player1Name.setText(Main.player1.name);
            player2Name.setText(Main.player2.name);
            String p1ScoreText;
            String p2ScoreText;
            if (Main.player1.getStatus() == Player.Status.Finish && Main.player2.getStatus() == Player.Status.Finish) {
                /* 兩個玩家都到終點 */
                p1ScoreText = Main.player1.score + " +1";
                Main.player1.score += 1;
                p2ScoreText = Main.player2.score + " +1";
                Main.player2.score += 1;
            } else if (Main.player1.getStatus() == Player.Status.Finish) {
                /* 只有玩家1到終點 */
                p1ScoreText = Main.player1.score + " +2";
                Main.player1.score += 2;
                p2ScoreText = Main.player2.score + " +0";
            } else if (Main.player2.getStatus() == Player.Status.Finish) {
                /* 只有玩家2到終點 */
                p1ScoreText = Main.player1.score + " +0";
                p2ScoreText = Main.player2.score + " +2";
                Main.player2.score += 2;
            } else {
                /* 兩個玩家都死亡 */
                p1ScoreText = Main.player1.score + " +0";
                p2ScoreText = Main.player2.score + " +0";
            }
            player1Score.setText(p1ScoreText);
            player2Score.setText(p2ScoreText);
            player1ScoreBar.setProgress(Main.player1.score / 10.0);
            player2ScoreBar.setProgress(Main.player2.score / 10.0);
            resultTable.setVisible(true);

            /* 遊戲結束 */
            if (Main.player1.score >= 10 && Main.player2.score >= 10) {
                /* 玩家平手 */
                resultTitle.setText(resultTitle.getText() + "平手！");
                btnNextRound.setVisible(false);
                btnExit.setVisible(true);
            } else if (Main.player1.score >= 10) {
                /* 玩家1獲勝 */
                resultTitle.setText(Main.player1.name + " 獲勝！");
                btnNextRound.setVisible(false);
                btnExit.setVisible(true);
            } else if (Main.player2.score >= 10) {
                /* 玩家2獲勝 */
                resultTitle.setText(Main.player2.name + " 獲勝！");
                btnNextRound.setVisible(false);
                btnExit.setVisible(true);
            } else if (Main.roundCount >= 10) {
                /* 已達回合上限(10回合) */
                if (Main.player1.score > Main.player2.score) {
                    /* 玩家1獲勝 */
                    resultTitle.setText(Main.player1.name + " 獲勝！");
                    btnNextRound.setVisible(false);
                    btnExit.setVisible(true);
                } else if (Main.player1.score < Main.player2.score) {
                    /* 玩家2獲勝 */
                    resultTitle.setText(Main.player2.name + " 獲勝！");
                    btnNextRound.setVisible(false);
                    btnExit.setVisible(true);
                } else {
                    /* 玩家平手 */
                    resultTitle.setText(resultTitle.getText() + "平手！");
                    btnNextRound.setVisible(false);
                    btnExit.setVisible(true);
                }
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    @FXML
    void nextRound() {
        /* 執行下一回合選方塊 */
        Main.roundCount += 1; /* 回合數+1 */

        /* 不延遲的簡單方法 */
        FXMLLoader fxmlLoaderBlock = new FXMLLoader(Main.class.getResource("selectBlock.fxml"));
        try {
            Main.selectBlockScene = new Scene(fxmlLoaderBlock.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.selectBlockScene.getRoot().requestFocus();
        Main.currentStage.setScene(Main.selectBlockScene);
    }

    @FXML
    public void onExitPressed() {
        Main.currentStage.close();
        System.exit(0);
    }
}
