package cn.westlan.coding.core.panel.element;

import android.graphics.Rect;
import cn.westlan.coding.core.panel.block.AdjustDate;
import cn.westlan.coding.core.panel.block.Style;
import cn.westlan.coding.core.panel.block.impl.AdjustDateBlock;

public class TimeElement extends BlockElement<AdjustDateBlock>{

    public TimeElement() {
        this(new Rect(), new AdjustDateBlock("HH:mm", new AdjustDate( 0,0,0)), Style._2_N_);
    }

    protected TimeElement(Rect bound, AdjustDateBlock block, Style style) {
        super(bound, block, style);
    }

    @Override
    public TimeElement clone() {
        return new TimeElement(this.bound, this.block, this.style);
    }
}
