package group5.finalproject;

import java.util.Random;

public class Block {
    private final BlockShape shape;
    private final BlockType type;

    public Block() {
        shape = BlockShape.values()[new Random().nextInt(BlockShape.values().length)];
        shape.setRotateQuarter(0); /* 初始化方塊旋轉角度 */
        type = BlockType.values()[new Random().nextInt(BlockType.values().length)];
    }

    public BlockShape getShape() {
        return shape;
    }

    public BlockType getType() {
        return type;
    }
}
