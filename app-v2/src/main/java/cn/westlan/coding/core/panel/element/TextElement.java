package cn.westlan.coding.core.panel.element;

import android.graphics.Rect;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.panel.attribute.Attribute;
import cn.westlan.coding.core.panel.attribute.DelegateAttribute;
import cn.westlan.coding.core.panel.attribute.Property;
import cn.westlan.coding.core.panel.block.Style;
import cn.westlan.coding.core.panel.block.impl.SimpleBlock;

import java.util.LinkedList;
import java.util.List;

public class TextElement extends BlockElement<SimpleBlock>{

    public TextElement() {
        this(new Rect(), new SimpleBlock(PrinterApplication.getInstance().getString(R.string.default_text)), Style._2_N_);
    }

    public TextElement(Rect bound, SimpleBlock block, Style style) {
        super(bound, block, style);
    }

    @Override
    public TextElement clone() {
        return new TextElement(this.bound, this.block, this.style);
    }

    @Override
    public List<Attribute<?>> attributes() {
        LinkedList<Attribute<?>> attributes = new LinkedList<>(super.attributes());
        attributes.addFirst(new DelegateAttribute<>(R.string.content, new Property<String>() {
            @Override
            public String get() {
                return getBlock().demo();
            }

            @Override
            public boolean set(String newVal) {
                if(getBlock().demo().equals(newVal))return false;
                setBlock(new SimpleBlock(newVal));
                invalidate();
                return true;
            }
        }));
        return attributes;
    }
}
