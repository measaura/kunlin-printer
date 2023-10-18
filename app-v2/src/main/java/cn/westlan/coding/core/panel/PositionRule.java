package cn.westlan.coding.core.panel;


public final class PositionRule {
    public static final PositionRule INSTANCE = new PositionRule();
    private static int changeX;
    private static int changeY;
    private static int labelHeightInPx;
    private static int labelWidthInPx;
    private static int offsetX;
    private static int offsetY;
    private static int startX;
    private static int startY;
    private static int timesX;
    private static int timesY;

    private PositionRule() {
    }

    public final void init(float f, float f2) {
        labelWidthInPx = LengthConvertUtil.INSTANCE.mm2px(f);
        int px = LengthConvertUtil.INSTANCE.mm2px(f2);
        labelHeightInPx = px;
        changeX = labelWidthInPx / 180;
        changeY = px / 180;
        timesX = 0;
        timesY = 0;
    }

    public final int getStartXInPx() {
        int changeX = (labelWidthInPx / 7) + getChangeX();
        startX = changeX;
        return changeX + offsetX;
    }

    private final int getChangeX() {
        int i = changeX;
        int i2 = timesX;
        timesX = i2 + 1;
        i *= i2;
        if (i > labelWidthInPx / 3) {
            timesX = 0;
            timesY = 0;
        }
        return i;
    }

    private final int getChangeY() {
        int i = changeY;
        int i2 = timesY;
        timesY = i2 + 1;
        i *= i2;
        if (i > labelHeightInPx / 3) {
            timesX = 0;
            timesY = 0;
        }
        return i;
    }

    public final int getStartYInPx() {
        int changeY = (labelHeightInPx / 9) + getChangeY();
        startY = changeY;
        return changeY + offsetY;
    }

    public final int getEndXInPx() {
        return startX + (labelWidthInPx / 3);
    }

    public final int getEndYInPx() {
        return startY;
    }

    public final int getElementWidthInpX() {
        return labelWidthInPx / 2;
    }

    public final int getElementHeightInpX() {
        return labelHeightInPx / 3;
    }

    public final int getBarCodeWidth() {
        return Math.min(labelWidthInPx / 2, (labelHeightInPx / 3) * 2);
    }

    public final int getBarCodeHeight() {
        return (getBarCodeWidth() / 3) * 2;
    }

    public final int getQrCodeWidth() {
        return Math.min(labelWidthInPx / 3, labelHeightInPx / 3);
    }
}