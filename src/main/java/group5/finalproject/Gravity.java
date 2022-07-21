package group5.finalproject;

import java.util.Timer;
import java.util.TimerTask;

public class Gravity {
    private final Player player; /* 腳色資訊 */

    private final int timeInterval = 10; /* 物理模擬引擎更新週期，單位：千分之一秒(ms) */
    private float velocity_y; /* y軸(i軸)方向速度*/
    private float velocity_x; /* x軸(j軸)方向速度*/
    private final float gravity = 500; /* 重力加速度設定，單位: pixel per second square*/
    private volatile Timer gravityTimer = null;


    public Gravity(Player player) {
        this.player = player;
    }


    public void add(float initialVelocity) {
        /* Input: 初速度(原則上跳躍時才有) */
        if (gravityTimer == null) {
            synchronized (Gravity.class) {
                if (gravityTimer == null) {
                    player.blockTypeNumberUnderFoot = 0;
                    velocity_y = initialVelocity;
                    gravityTimer = new Timer();
                    gravityTimer.schedule(new timerTaskGravity(), 0, timeInterval);
//            System.out.println(player.name + "重力開始");
                }
            }
        }
    }

    public void remove() {
        if (gravityTimer != null) {
            gravityTimer.cancel();
            gravityTimer = null;
//            System.out.println(player.name + "重力結束");
        }
    }


    class timerTaskGravity extends TimerTask {
        /* 重力模擬引擎 */
        @Override
        public void run() {
            /* 讓腳色Y軸依重力上升(圖片位置下降) */
            if (player.status != Player.Status.Finish && player.status != Player.Status.Dead) {
                float dt = (float) (timeInterval / 1000.0); /* 物理引擎執行週期 */
                float decrement_y = velocity_y * dt; // S = V * t
                float increment_x = velocity_x * dt; // S = V * t 水平速度
                velocity_y += gravity * dt; // V = V0 + at
                player.move(0, decrement_y);
                player.move(increment_x, 0);
//                System.out.println("本次位移量: " + decrement_y);
            }
        }
    }
}
