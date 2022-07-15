package group5.finalproject;

public enum BlockShape {
    OrangeRicky(new int[][][]{
            {{0, 2}, {1, 0}, {1, 1}, {1, 2}}, /* rotateQuarter = 0 */
            {{0, 0}, {1, 0}, {2, 0}, {2, 1}}, /* rotateQuarter = 1 */
            {{0, 0}, {0, 1}, {0, 2}, {1, 0}}, /* rotateQuarter = 2 */
            {{0, 0}, {0, 1}, {1, 1}, {2, 1}}, /* rotateQuarter = 3 */
    }),
    BlueRicky(new int[][][]{
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}}, /* rotateQuarter = 0 */
            {{0, 0}, {0, 1}, {1, 0}, {2, 0}}, /* rotateQuarter = 1 */
            {{0, 0}, {0, 1}, {0, 2}, {1, 2}}, /* rotateQuarter = 2 */
            {{0, 1}, {1, 1}, {2, 0}, {2, 1}}, /* rotateQuarter = 3 */
    }),
    ClevelandZ(new int[][][]{
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}}, /* rotateQuarter = 0 */
            {{0, 1}, {1, 0}, {1, 1}, {2, 0}}, /* rotateQuarter = 1 */
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}}, /* rotateQuarter = 2 */
            {{0, 1}, {1, 0}, {1, 1}, {2, 0}}, /* rotateQuarter = 3 */
    }),
    RhodeIslandZ(new int[][][]{
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}}, /* rotateQuarter = 0 */
            {{0, 0}, {1, 0}, {1, 1}, {2, 1}}, /* rotateQuarter = 1 */
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}}, /* rotateQuarter = 2 */
            {{0, 0}, {1, 0}, {1, 1}, {2, 1}}, /* rotateQuarter = 3 */
    }),
    Hero(new int[][][]{
            {{0, 0}, {0, 1}, {0, 2}, {0, 3}}, /* rotateQuarter = 0 */
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, /* rotateQuarter = 1 */
            {{0, 0}, {0, 1}, {0, 2}, {0, 3}}, /* rotateQuarter = 2 */
            {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, /* rotateQuarter = 3 */
    }),
    Teewee(new int[][][]{
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}}, /* rotateQuarter = 0 */
            {{0, 0}, {1, 0}, {1, 1}, {2, 0}}, /* rotateQuarter = 1 */
            {{0, 0}, {0, 1}, {0, 2}, {1, 1}}, /* rotateQuarter = 2 */
            {{0, 1}, {1, 0}, {1, 1}, {2, 1}}, /* rotateQuarter = 3 */
    }),
    Smashboy(new int[][][]{
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, /* rotateQuarter = 0 */
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, /* rotateQuarter = 1 */
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, /* rotateQuarter = 2 */
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, /* rotateQuarter = 3 */
    });

    private int rotateQuarter = 0;
    /* rotateQuarter: 旋轉角度(幾個Quarter)
     * 0 -> 順時針0/4圓, 0度
     * 1 -> 順時針1/4圓, 90度
     * 2 -> 順時針2/4圓, 180度
     * 3 -> 順時針3/4圓, 270度
     */
    private final int[][][] distributions;

    BlockShape(int[][][] distributionEachRotation) {
        this.distributions = distributionEachRotation;
    }

    public void clockwiseRotate() {
        this.rotateQuarter += 1;
    }

    public void setRotateQuarter(int rotateQuarterInput) {
        this.rotateQuarter = rotateQuarterInput;
    }

    public int getRotateQuarter() {
        return this.rotateQuarter;
    }

    public int getHeight() {
        int maxHeight = 0;
        for (int i = 0; i < distributions[this.rotateQuarter % 4].length; i++) {
            if (distributions[this.rotateQuarter % 4][i][0] > maxHeight)
                maxHeight = distributions[this.rotateQuarter % 4][i][0];
        }
        return maxHeight;
    }

    public int getWidth() {
        int maxWidth = 0;
        for (int i = 0; i < distributions[this.rotateQuarter % 4].length; i++) {
            if (distributions[this.rotateQuarter % 4][i][1] > maxWidth)
                maxWidth = distributions[this.rotateQuarter % 4][i][1];
        }
        return maxWidth;
    }

    public int[][] getDistribution() {
        return distributions[this.rotateQuarter % 4];
    }
}
