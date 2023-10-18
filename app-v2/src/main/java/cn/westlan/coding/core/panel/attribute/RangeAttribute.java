package cn.westlan.coding.core.panel.attribute;

import java.util.function.Predicate;

public class RangeAttribute extends BaseAttribute<Float>{
    private final float min;
    private final float max;

    public RangeAttribute(Integer name, float min, float max) {
        this(name, min, min , max, null, false);
    }

    public RangeAttribute(Integer name, float val, float min, float max, Predicate<Float> predicate) {
        this(name, val, min , max, predicate, false);
    }

    public RangeAttribute(Integer name, float val, float min, float max, Predicate<Float> predicate, boolean enable) {
        super(name, val, predicate, enable);
        this.min = min;
        this.max = max;
    }

    public float getProgress() {
        return (get()-min)*100/(max-min);
    }

    public boolean setProgress(float progress) {
        return set(min+(max-min)*progress/100f);
    }
}
