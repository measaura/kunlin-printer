package cn.westlan.coding.core.panel.value;

import lombok.Getter;

@Getter
public final class StepVal {
    private int index;
    private final int size;
    private final float min;
    private final float stride;

    public StepVal(float min, float max, float stride) {
        this(min, min, max, stride);
    }

    public StepVal(float val, float min, float max, float stride) {
        this.index = (int)((val-min)/stride);
        this.size = (int)((max-min)/stride);
        this.min = min;
        this.stride = stride;
    }

    public int status(){
        if(index == 0)return -1;
        if(index == size)return 1;
        return 0;
    }

    public float forward(){
        if(index >= size)return min+size*stride;
        return min+(++index)*stride;
    }

    public float backward(){
        if(index <= 0)return min;
        return min+(--index)*stride;
    }

    public float value(){
        return min+index*stride;
    }
}
