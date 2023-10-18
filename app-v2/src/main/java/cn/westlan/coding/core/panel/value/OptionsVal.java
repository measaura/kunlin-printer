package cn.westlan.coding.core.panel.value;

import java.util.Arrays;
import java.util.List;

public class OptionsVal<T> {
    private final List<T> options;
    private int index;

    public OptionsVal(T[] options) {
        this(Arrays.asList(options), 0);
    }

    public OptionsVal(List<T> options) {
        this(options, 0);
    }

    public OptionsVal(List<T> options, int index) {
        this.options = options;
        this.index = index;
    }

    public List<T> getOptions() {
        return options;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index) {
        if(index < 0 || index >= options.size())return;
        this.index = index;
    }

    public T value(){
        return options.get(index);
    }
}
