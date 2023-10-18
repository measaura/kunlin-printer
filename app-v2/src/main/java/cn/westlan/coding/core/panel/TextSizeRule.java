package cn.westlan.coding.core.panel;

public final class TextSizeRule {
    public static final TextSizeRule INSTANCE = new TextSizeRule();
    private static int defaultSizeInPx;
    private static float textSizeChange;

    private TextSizeRule() {
    }

    public final void init(int i) {
        defaultSizeInPx = i / 15;
        textSizeChange = LengthConvertUtil.INSTANCE.px2mm(i / 150);
    }

    public final int defaultSizeInPx() {
        return defaultSizeInPx;
    }

    public final float add(float f) {
        return f + textSizeChange;
    }

    public final float sub(float f) {
        float f2 = textSizeChange;
        return f - f2 > ((float) 0) ? f - f2 : f;
    }
}