package cn.westlan.coding.core.panel.element;

import android.graphics.Rect;
import cn.westlan.coding.R;
import cn.westlan.coding.core.panel.attribute.Attribute;
import cn.westlan.coding.core.panel.attribute.DelegateAttribute;
import cn.westlan.coding.core.panel.attribute.Property;
import cn.westlan.coding.core.panel.block.Style;
import cn.westlan.coding.core.panel.block.impl.AdjustDateBlock;

import java.util.LinkedList;
import java.util.List;

public class DateElement extends BlockElement<AdjustDateBlock>{

    public DateElement() {
        this(new Rect(), new AdjustDateBlock(), Style._2_N_);
    }

    public DateElement(Rect bound, AdjustDateBlock block, Style style) {
        super(bound, block, style);
    }

    @Override
    public DateElement clone() {
        return new DateElement(this.bound, this.block, this.style);
    }

    @Override
    public List<Attribute<?>> attributes() {
        LinkedList<Attribute<?>> attributes = new LinkedList<>(super.attributes());
        attributes.addFirst(new DelegateAttribute<>(R.string.date_setting, new Property<AdjustDateBlock>() {
            @Override
            public AdjustDateBlock get() {
                return getBlock();
            }

            @Override
            public boolean set(AdjustDateBlock newVal) {
                setBlock(newVal);
                invalidate();
                return true;
            }
        }));
        return attributes;
    }
}
