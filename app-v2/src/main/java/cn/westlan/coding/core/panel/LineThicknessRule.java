package cn.westlan.coding.core.panel;

import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;

public final class LineThicknessRule {
    public static final LineThicknessRule INSTANCE = new LineThicknessRule();
    private static float minLevelForMM;

    private LineThicknessRule() {
    }

    public final void init(float f, float f2) {
        minLevelForMM = RangesKt.coerceAtMost(f, f2) / ((float) 2.52E-43f);
    }

    public final float getMMThicknessRuleRule(int i) {
        return ((float) i) * minLevelForMM;
    }

    public final int getPxThicknessByLevel(int i) {
        return LengthConvertUtil.INSTANCE.mm2px(((float) i) * minLevelForMM);
    }

    public final int getDefaultPxThickness() {
        return LengthConvertUtil.INSTANCE.mm2px(((float) 3) * minLevelForMM);
    }

    public final int getLevelByThicknessPx(int i) {
        return MathKt.roundToInt(LengthConvertUtil.INSTANCE.px2mm(i) / minLevelForMM);
    }
}