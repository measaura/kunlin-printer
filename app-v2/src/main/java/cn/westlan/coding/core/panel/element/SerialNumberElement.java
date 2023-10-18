package cn.westlan.coding.core.panel.element;

import android.graphics.Rect;
import cn.westlan.coding.R;
import cn.westlan.coding.core.panel.attribute.Attribute;
import cn.westlan.coding.core.panel.attribute.DelegateAttribute;
import cn.westlan.coding.core.panel.attribute.Property;
import cn.westlan.coding.core.panel.block.Accum;
import cn.westlan.coding.core.panel.block.Style;
import cn.westlan.coding.core.panel.block.impl.SerialNumberBlock;

import java.util.LinkedList;
import java.util.List;

public class SerialNumberElement extends BlockElement<SerialNumberBlock> {
    public SerialNumberElement() {
        this(new Rect(), new SerialNumberBlock(), Style._2_N_);
    }

    public SerialNumberElement(Rect bound, SerialNumberBlock block, Style style) {
        super(bound, block, style);
    }

    @Override
    public SerialNumberElement clone() {
        return new SerialNumberElement(this.bound, this.block, this.style);
    }

    @Override
    public List<Attribute<?>> attributes() {
        LinkedList<Attribute<?>> attributes = new LinkedList<>(super.attributes());
        attributes.addFirst(new DelegateAttribute<Integer>(R.string.serial_addend, new Property<Integer>() {

            @Override
            public Integer get() {
                return getBlock().getStep();
            }

            @Override
            public boolean set(Integer newVal) {
                if(getBlock().getStep().equals(newVal))return false;
                SerialNumberBlock block = getBlock();
                setBlock(new SerialNumberBlock(new Accum(block.getStart(), block.getEnd(), newVal)));
                invalidate();
                return true;
            }
        }){
            @Override
            public int hint() {
                return R.string.hint_addend;
            }

            @Override
            public boolean isLegal(Integer o) {
                return o>=0&&o<=9;
            }

            @Override
            public int errorMessage() {
                return R.string.hint_addend;
            }
        });
        attributes.addFirst(new DelegateAttribute<Integer>(R.string.serial_end, new Property<Integer>() {
            @Override
            public Integer get() {
                return getBlock().getEnd();
            }

            @Override
            public boolean set(Integer newVal) {
                if(getBlock().getEnd().equals(newVal))return false;
                SerialNumberBlock block = getBlock();
                setBlock(new SerialNumberBlock(new Accum(block.getStart(), newVal, block.getStep())));
                invalidate();
                return true;
            }
        }){
            @Override
            public int hint() {
                return R.string.hint_start_or_end;
            }

            @Override
            public boolean isLegal(Integer o) {
                return o >= getBlock().getStart();
            }//&&o<999999999

            @Override
            public int errorMessage() {
                return R.string.hint_start_or_end;
            }
        });
        attributes.addFirst(new DelegateAttribute<Integer>(R.string.serial_start, new Property<Integer>() {
            @Override
            public Integer get() {
                return getBlock().getStart();
            }

            @Override
            public boolean set(Integer newVal) {
                if(getBlock().getStart().equals(newVal))return false;
                SerialNumberBlock block = getBlock();
                setBlock(new SerialNumberBlock(new Accum(newVal, block.getEnd(), block.getStep())));
                invalidate();
                return true;
            }
        }){

            @Override
            public boolean isLegal(Integer o) {
                return o <= getBlock().getEnd()&&o>=0;
            }

            @Override
            public int errorMessage() {
                return R.string.hint_start_or_end;
            }
        });
        return attributes;
    }
}
