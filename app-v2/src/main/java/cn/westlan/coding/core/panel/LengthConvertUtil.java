package cn.westlan.coding.core.panel;

import kotlin.jvm.internal.Intrinsics;

import java.text.NumberFormat;

public final class LengthConvertUtil {
    public static final LengthConvertUtil INSTANCE = new LengthConvertUtil();
    private static double ratio;

    private LengthConvertUtil() {
    }

    public final void init(float f, int i) {
        ratio = (double) (((float) i) / f);
    }

    public final float px2mm(int i) {
        float ii = (float) (((double) i) / ratio);
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(1);
        String iii = numberInstance.format(Float.valueOf(ii));
        Intrinsics.checkNotNullExpressionValue(iii, "format(mm)");
        return Float.parseFloat(iii);
    }

    public final int mm2px(float f) {
        return (int) ((((double) f) * ratio) + ((double) 0.5f));
    }
}