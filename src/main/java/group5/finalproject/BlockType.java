package group5.finalproject;

import javafx.scene.paint.Color;

public enum BlockType {
    Normal(1, Color.GREEN, Color.FORESTGREEN),
    Ice(2, Color.AQUA, Color.FORESTGREEN),
    Honey(3, Color.YELLOW, Color.FORESTGREEN),
    Trap(4, Color.RED, Color.LIGHTGRAY),
    Tnt(5, Color.BLACK, Color.BLACK);

    BlockType(int number, Color frameColor, Color fillColor) {
        this.number = number;
        this.frameColor = frameColor;
        this.fillColor = fillColor;
    }

    private final int number;
    private final Color frameColor;
    private final Color fillColor;

    public Color getFrameColor() {
        return frameColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public int getNumber() {
        return number;
    }
}
