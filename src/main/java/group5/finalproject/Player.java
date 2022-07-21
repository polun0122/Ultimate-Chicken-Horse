package group5.finalproject;

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
    public int blockTypeNumberUnderFoot = 1;

    /* 物理模擬引擎變數 */
    public float velocity_y; /* y軸(i軸)方向速度*/
    public float velocity_x; /* x軸(j軸)方向速度*/

    private final Gravity gravity = new Gravity(this);
    public final Friction friction = new Friction(this);

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
        gravity.remove();
        friction.remove();
        velocity_x = 0;
        velocity_y = 0;
        blockTypeNumberUnderFoot = 1;
    }

    public void giveUp() {
        /* 放棄遊玩，原地死亡 */
        status = Status.Dead;
        gravity.remove();
        friction.remove();
    }

    public void moveRight() {
        if (!friction.isWorking()) { /* 沒有正在煞車才可以移動 */
            /* 依地面材質決定速度 */
            switch (blockTypeNumberUnderFoot) {
                case 2 -> velocity_x = 90; /* Ice */
                case 3 -> velocity_x = 30; /* Honey */
                default -> velocity_x = 70; /* Normal */
            }
            this.move((float) (velocity_x / 100), 0);
        }
    }

    public void moveLeft() {
        if (!friction.isWorking()) { /* 沒有正在煞車才可以移動 */
            /* 依地面材質決定速度 */
            switch (blockTypeNumberUnderFoot) {
                case 2 -> velocity_x = -90; /* Ice */
                case 3 -> velocity_x = -30; /* Honey */
                default -> velocity_x = -70; /* Normal */
            }
            this.move((float) (velocity_x / 100), 0);
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
        gravity.add(verticalVelocity0);
    }

    public void move(float xIncrement, float yIncrement) {
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
            gravity.remove();
            gravity.add(0);
        } else if (temp_y > gameHeight - roleHeight) {
            /* 觸底死亡 */
            temp_y = gameHeight - roleHeight;
            status = Status.Dead;
            gravity.remove();
            friction.remove();
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
                gravity.remove();
                temp_y = newDownIndex_y * 50 - roleWidth;
                if (blockTypeNumberUnderFoot == 4) {
                    status = Status.Dead;
                    friction.remove();
                }
            }
        }
        /* 往上碰撞 */
        int newUpIndex_y = (int) (temp_y / 50);
        int oldUpIndex_y = (int) (position[1] / 50);
        if (newUpIndex_y < oldUpIndex_y) {
//            System.out.println("往上碰撞檢查");
            if (getMapDistribution(newUpIndex_y, newBlockIndex_x) != 0) {
                gravity.remove();
                temp_y = oldUpIndex_y * 50;
                gravity.add(0);
                if (getMapDistribution(newUpIndex_y, newBlockIndex_x) == 4) {
                    status = Status.Dead;
                }
            }
        }
        /* 往左碰撞 */
        if (newBlockIndex_x < oldBlockIndex_x) {
//            System.out.println("往左碰撞檢查");
            if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) != 0) {
                friction.remove();
                temp_x = (float) (oldBlockIndex_x * 50 - halfRoleWidth);
                if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) == 4) {
                    status = Status.Dead;
                    gravity.remove();
                }
            } else {
                gravity.add(0);
            }
        }
        /* 往右碰撞 */
        if (newBlockIndex_x > oldBlockIndex_x) {
//            System.out.println("往右碰撞檢查");
            if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) != 0) {
                friction.remove();
                temp_x = (float) (newBlockIndex_x * 50 - halfRoleWidth - 0.01);
                if (getMapDistribution(oldDownIndex_y, newBlockIndex_x) == 4) {
                    status = Status.Dead;
                    gravity.remove();
                }
            } else {
                gravity.add(0);
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
}
