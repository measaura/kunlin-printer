package cn.westlan.coding.core.panel.block;

import lombok.Data;

@Data
public class Accum {
    private final Integer start;
    private final Integer end;
    private final Integer step;

    public Accum() {
        this(0, 0,0);
    }

    public Accum(Integer start, Integer end, Integer step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }
}
