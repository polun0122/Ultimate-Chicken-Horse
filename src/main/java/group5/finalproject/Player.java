package group5.finalproject;

import java.util.Timer;
import java.util.TimerTask;

public class Player {
    // Role Picture Reference
    // https://pngtree.com/triberion_18334375/6?type=1
    public enum Status {
        Idle, RightMove, LeftMove, Dead, Finish
    }

    public Status status;
    private final int number; /* 玩家編號 */
    public String name; /* 玩家名稱 */
    public String type; /* 玩家腳色型態 */
    public int score = 0; /* 玩家分數 */
    public Block currentBlock; /* 紀錄玩家已選且尚未放置於地圖的方塊 */
    public int[] blockIndex; /* 暫存玩家目前方塊(尚未放進地圖)擺放的座標 */
    public float[] position = {0, 0};  /* 腳色在地圖中位置(i,j)(已開始遊戲) */
    private int blockTypeNumberUnderFoot = 1;

    /* 物理模擬引擎變數 */
    private boolean isRoleOnAir = false; /* 腳色是否在空中 */
    private final int timeInterval = 10; /* 物理模擬引擎更新週期，單位：千分之一秒(ms) */
    private float velocity_y; /* y軸(i軸)方向速度*/
    private float velocity_x; /* x軸(j軸)方向速度*/
    private final float gravity = 500; /* 重力加速度設定，單位: pixel per second square*/
    private Timer gravityTimer = null;
    private Timer frictionTimer = null;

    /* 遊戲畫面大小設定 */
    private final int gameHeight = 600; /* 畫面高度 */
    private final int gameWidth = 900; /* 畫面寬度 */
    public final int roleHeight = 48; /* 腳色高度 */
    public final int roleWidth = 48; /* 腳色寬度 */

    public Player(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setInitialPosition(float i) {
        /* 設定腳色初始位置，不能超出起點方塊 */
        /* 起點方塊長度: 150, 高度: 450-腳色高度 */
        position[0] = i;
        position[1] = 450 - roleHeight;
        if (i < 0)
            position[0] = 0;
        else if (i > 150 - roleWidth)
            position[0] = 150 - roleWidth;
    }

    public void reset() {
        /* 腳色狀態初始化，並關閉所有Timer */
        status = Status.Idle;
        removeGravity();
        removeFriction();
        velocity_x = 0;
        velocity_y = 0;
        blockTypeNumberUnderFoot = 1;
        isRoleOnAir = false;
    }

    public void giveUp() {
        /* 放棄遊玩，原地死亡 */
        status = Status.Dead;
        removeGravity();
        removeFriction();
    }

    public void moveRight() {
        if (!isRoleOnAir) { /* 腳色不在空中才可以左右移動 */
            if (frictionTimer == null) { /* 沒有正在煞車才可以移動 */
                /* 依地面材質決定速度 */
                switch (blockTypeNumberUnderFoot) {
                    case 2 -> velocity_x = 90; /* Ice */
                    case 3 -> velocity_x = 30; /* Honey */
                    default -> velocity_x = 70; /* Normal */
                }
                this.move((float) (velocity_x / 100), 0);
            }
        }
    }

    public void moveLeft() {
        if (!isRoleOnAir) { /* 腳色不在空中才可以左右移動 */
            if (frictionTimer == null) { /* 沒有正在煞車才可以移動 */
                /* 依地面材質決定速度 */
                switch (blockTypeNumberUnderFoot) {
                    case 2 -> velocity_x = -90; /* Ice */
                    case 3 -> velocity_x = -30; /* Honey */
                    default -> velocity_x = -70; /* Normal */
                }
                this.move((float) (velocity_x / 100), 0);
            }
        }
    }

    public void jump() {
        float verticalVelocity0;
        /* 依地面材質決定跳躍高度 */
        switch (blockTypeNumberUnderFoot) {
            case 2 -> verticalVelocity0 = -260; /* Ice */
            case 3 -> verticalVelocity0 = -230; /* Honey */
            default -> verticalVelocity0 = -260; /* Normal */
        }
        addGravity(verticalVelocity0);
    }

    private boolean stopVxWhenLanded = false; /* 等跳躍結束是否加入摩擦力 */

    public void addFriction() {
        /* 加入摩擦力實現水平煞車 */
        if (frictionTimer == null) {
            if (!isRoleOnAir) {
                frictionTimer = new Timer();
                frictionTimer.schedule(new timerTaskFriction(), 0, timeInterval);
            } else {
                /* 腳色在空中，等待跳躍結束 */
                stopVxWhenLanded = true;
            }
//            System.out.println(name + "摩擦力開始");
        }
    }

    private void removeFriction() {
        /* 移除水平摩擦力 */
        /* 會從Timer中呼叫，若減速至0則呼叫 */
        if (frictionTimer != null) {
            frictionTimer.cancel();
            frictionTimer = null;
//            System.out.println(name + "摩擦力結束");
        }
    }

    private void addGravity(float initialVelocity) {
        /* Input: 初速度(原則上跳躍時才有) */
        if (gravityTimer == null) {
            isRoleOnAir = true;
            velocity_y = initialVelocity;
            gravityTimer = new Timer();
            gravityTimer.schedule(new timerTaskGravity(), 0, timeInterval);
//            System.out.println(name + "重力開始");
        }
    }

    private void removeGravity() {
        if (gravityTimer != null) {
            isRoleOnAir = false;
            gravityTimer.cancel();
            gravityTimer = null;
            if (stopVxWhenLanded) {
                /* 腳色落地後移除水平摩擦力 */
                addFriction();
                stopVxWhenLanded = false;
            }
//            System.out.println(name + "重力結束");
        }
    }

    private void move(float xIncrement, float yIncrement) {
        /* 腳色移動 */
        float temp_x = position[0] + xIncrement;
        float temp_y = position[1] + yIncrement;
        double halfRoleWidth = roleWidth / 2.0;
        /* 不要超出遊戲邊界 */
        if (temp_x < 0)
            temp_x = 0;
        else if (temp_x > gameWidth - roleWidth)
            temp_x = gameWidth - roleWidth;
        else if (temp_y < 0) {
            /* 往上碰撞至頂 */
            temp_y = 0;
            removeGravity();
            addGravity(0);
        } else if (temp_y > gameHeight - roleHeight) {
            /* 觸底死亡 */
            removeGravity();
            temp_y = gameHeight - roleHeight;
            status = Status.Dead;
            removeGravity();
            removeFriction();
        }

        /* 地圖碰撞設定 */
        int newBlockIndex_x = (int) (temp_x + halfRoleWidth) / 50;
        int oldBlockIndex_x = (int) (position[0] + halfRoleWidth) / 50;
        /* 往下碰撞 */
        int newDownIndex_y = (int) (temp_y + roleHeight - 0.01) / 50;
        int oldDownIndex_y = (int) (position[1] + roleHeight - 0.01) / 50; /* -1 為了在平行移動時不要忽略檢查 */
        if (newDownIndex_y > oldDownIndex_y) {
//            System.out.println("往下碰撞檢查");
            blockTypeNumberUnderFoot = getMapDistribution(newDownIndex_y, newBlockIndex_x);
            if (blockTypeNumberUnderFoot != 0) {
                removeGravity();
                temp_y = newDownIndex_y * 50 - roleWidth;
                if (blockTypeNumberUnderFoot == 4) {
                    status = Status.Dead;
                    removeFriction();
                }
            }
        }
        /* 往上碰撞 */
        int newUpIndex_y = (int) (temp_y / 50);
        int oldUpIndex_y = (int) (position[1] / 50);
        if (newUpIndex_y < oldUpIndex_y) {
//            System.out.println("往上碰撞檢查");
            if (getMapDistribution(newUpIndex_y, newBlockIndex_x) != 0) {
                removeGravity();
                temp_y = oldUpIndex_y * 50;
                addGravity(0);
                if (getMapDistribution(newUpIndex_y, newBlockIndex_x) == 4) {
                    status = Status.Dead;
                }
            }
        }
        /* 往左碰撞 */
        if (newBlockIndex_x < oldBlockIndex_x) {
//            System.out.println("往左碰撞檢查");
            if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) != 0) {
                removeFriction();
                temp_x = (float) (oldBlockIndex_x * 50 - halfRoleWidth);
                if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) == 4) {
                    status = Status.Dead;
                    removeGravity();
                    removeFriction();
                }
            }
            else {
                addGravity(0);
            }
        }
        /* 往右碰撞 */
        if (newBlockIndex_x > oldBlockIndex_x) {
//            System.out.println("往右碰撞檢查");
            if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) != 0) {
                removeFriction();
                temp_x = (float) (newBlockIndex_x * 50 - halfRoleWidth - 0.01);
                if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) == 4) {
                    status = Status.Dead;
                    removeGravity();
                    removeFriction();
                }
            }
            else {
                addGravity(0);
            }
        }

        /* 抵達終點 */
        if ((750 < temp_x && temp_x < 900) && temp_y == 250 - roleHeight) {
            status = Status.Finish;
        }

        /* 更新位置 */
        position[0] = temp_x;
        position[1] = temp_y;
    }


    private int getMapDistribution(int i, int j) {
        /* 檢查地圖中該位置是存在物件 */
//        System.out.printf("getMapDistribution: i = %d, j = %d\n", i, j);
        /* 起點方塊 */
        if (j <= 2) {
            if (i >= 9)
                return 1;
            else
                return 0;
        }
        /* 終點方塊 */
        if (j >= 15) {
            if (i >= 5)
                return 1;
            else
                return 0;
        }
        /* 不可放置方塊的上下邊界 */
        if (i == 0 || i == 11) {
            return 0;
        }
        /* 可放置方塊區就直接回傳該方塊狀態 */
        return Main.blockDistribution[i - 1][j - 3];
    }

    class timerTaskFriction extends TimerTask {
        /* 摩擦力模擬引擎 */
        @Override
        public void run() {
            if (status != Status.Finish && status != Status.Dead) {
                float dt = (float) (timeInterval / 1000.0); /* 物理引擎執行週期 */
                /* 讓腳色在x軸煞車 */
                float friction; /* 摩擦力設定，單位: pixel per second square*/
                /* 判定摩擦力方向 */
                if (velocity_x > 0)
                    friction = -1;
                else
                    friction = 1;
                /* 判定摩擦力大小 */
                switch (blockTypeNumberUnderFoot) {
                    case 2 -> friction *= 150; /* Ice */
                    case 3 -> friction *= 2000; /* Honey*/
                    default -> friction *= 800; /* Normal */
                }
//                /* 空中不同摩擦力版 */
//                if (!isRoleOnAir) {
//                    /* 不在空中的話，依目前地面材質乘上倍數 */
//                    switch (blockTypeNumberUnderFoot) {
//                        case 2 -> friction *= 100; /* Ice */
//                        case 3 -> friction *= 2000; /* Honey*/
//                        default -> friction *= 800; /* Normal */
//                    }
//                } else
//                    friction *= 100; /* 在空中的水平阻力 */
                /* 新的速度 */
                float newVelocity_x = velocity_x + friction * dt; // V = V0 + at
                if (newVelocity_x * velocity_x < 0 || newVelocity_x == 0) {
                    /* 速度異號或新的速度為0，停止摩擦力 */
                    velocity_x = 0;
                    removeFriction();
                } else
                    velocity_x = newVelocity_x;
                /* 進行腳色位移 */
                float decrement_x = velocity_x * dt; // S = V * t
                move(decrement_x, 0);
//                System.out.println("水平速度: " + velocity_x);
//                System.out.println("本次水平位移量: " + decrement_x);
            }
        }
    }

    class timerTaskGravity extends TimerTask {
        /* 重力模擬引擎 */
        @Override
        public void run() {
            /* 讓腳色Y軸依重力上升(圖片位置下降) */
            if (status != Status.Finish && status != Status.Dead) {
                float dt = (float) (timeInterval / 1000.0); /* 物理引擎執行週期 */
                float decrement_y = velocity_y * dt; // S = V * t
                float increment_x = velocity_x * dt; // S = V * t 水平速度
                velocity_y += gravity * dt; // V = V0 + at
                move(0, decrement_y);
                move(increment_x, 0);
//                System.out.println("本次位移量: " + decrement_y);
            }
        }
    }
}
