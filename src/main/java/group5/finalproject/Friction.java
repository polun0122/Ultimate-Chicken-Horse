package group5.finalproject;

import java.util.Timer;
import java.util.TimerTask;

public class Friction {
    private final Player player; /* 腳色資訊 */

    private volatile Timer frictionTimer = null;
    private final int timeInterval = 10; /* 物理模擬引擎更新週期，單位：千分之一秒(ms) */

    public Friction(Player player) {
        this.player = player;
    }

    public boolean isWorking() {
        return frictionTimer != null;
    }

    public void add() {
        /* 加入摩擦力實現水平煞車 */
        if (frictionTimer == null) {
            synchronized (Friction.class) {
                if (frictionTimer == null) {
                    frictionTimer = new Timer();
                    frictionTimer.schedule(new timerTaskFriction(), 0, timeInterval);
            System.out.println(player.name + "摩擦力開始");
                }
            }
        }
    }

    public void remove() {
        /* 移除水平摩擦力 */
        /* 會從Timer中呼叫，若減速至0則呼叫 */
        if (frictionTimer != null) {
            frictionTimer.cancel();
            frictionTimer = null;
            System.out.println(player.name + "摩擦力結束");
        }
    }

    class timerTaskFriction extends TimerTask {
        /* 摩擦力模擬引擎 */
        @Override
        public void run() {
            if (player.status != Player.Status.Finish && player.status != Player.Status.Dead) {
                float dt = (float) (timeInterval / 1000.0); /* 物理引擎執行週期 */
                /* 讓腳色在x軸煞車 */
                float friction; /* 摩擦力設定，單位: pixel per second square*/
                /* 判定摩擦力方向 */
                if (player.velocity_x > 0)
                    friction = -1;
                else
                    friction = 1;
                /* 判定摩擦力大小 */
                switch (player.blockTypeNumberUnderFoot) {
                    case 0 -> friction *= 0; /* Air */
                    case 1 -> friction *= 800; /* Normal */
                    case 2 -> friction *= 150; /* Ice */
                    case 3 -> friction *= 2000; /* Honey*/
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
                float newVelocity_x = player.velocity_x + friction * dt; // V = V0 + at
                if (newVelocity_x * player.velocity_x < 0 || newVelocity_x == 0) {
                    /* 速度異號或新的速度為0，停止摩擦力 */
                    player.velocity_x = 0;
                    remove();
                } else
                    player.velocity_x = newVelocity_x;
                /* 進行腳色位移 */
                float decrement_x = player.velocity_x * dt; // S = V * t
                player.move(decrement_x, 0);
//                System.out.println("水平速度: " + velocity_x);
//                System.out.println("本次水平位移量: " + decrement_x);
            }
        }
    }
}
