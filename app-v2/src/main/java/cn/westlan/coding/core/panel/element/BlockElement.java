package cn.westlan.coding.core.panel.element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.panel.attribute.Attribute;
import cn.westlan.coding.core.panel.attribute.DelegateAttribute;
import cn.westlan.coding.core.panel.attribute.Property;
import cn.westlan.coding.core.panel.attribute.StepAttribute;
import cn.westlan.coding.core.panel.block.Block;
import cn.westlan.coding.core.panel.block.DynamicNumber;
import cn.westlan.coding.core.panel.block.Slice;
import cn.westlan.coding.core.panel.block.Style;
import cn.westlan.coding.core.panel.block.slice.NumberSlice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public abstract class BlockElement<T extends Block> extends Element{
    protected T block;
    protected Style style;

    public BlockElement(Rect bound, T block, Style style) {
        super(bound);
        this.block = block;
        this.style = style;
    }

    @Override
    public List<Attribute<?>> attributes() {
        LinkedList<Attribute<?>> attributes = new LinkedList<>();
        int index = 0;
        Float[] array = Style.fontSizes().toArray(new Float[0]);
        for(int i = 0; i < array.length; ++i){
            if(array[i].equals(style.getFontSize())){
                index = i;
            }
        }
        attributes.add(new StepAttribute<Float>(R.string.font_size, array, index, new Predicate<Float>() {
            @Override
            public boolean test(Float newVal) {
                if(newVal.equals(style.getFontSize()))return false;
                style = Style.style(newVal, style.isBold());
                invalidate();
                return true;
            }
        }));
        attributes.add(new DelegateAttribute<>(R.string.text_bold, new Property<Boolean>() {
            @Override
            public Boolean get() {
                return style.isBold();
            }

            @Override
            public boolean set(Boolean newVal) {
                if(newVal.equals(style.isBold()))return false;
                style = Style.style(style.getFontSize(), newVal);
                invalidate();
                return true;
            }
        }));
        return attributes;
    }

    protected void setBlock(T block) {
        this.block = block;
    }

    protected T getBlock() {
        return block;
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        super.readFrom(inputStream);
        if(inputStream.available() < 3)return;
        byte identity = IOUtil.readByte(inputStream);
        this.style = Style.style(identity);
        this.block = (T)IOUtil.readBlock(inputStream);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        super.writeTo(outputStream);
        IOUtil.writeByte(outputStream, style.getIdentifier());
        IOUtil.writeBlock(outputStream, block);
    }

    @Override
    public void refresh(CanvasFeature canvasFeature) {
        this.paint.setTypeface(this.style.isBold() ? PrinterApplication.boldFont : PrinterApplication.normalFont);
        this.paint.setTextSize(this.style.getFontSize() * canvasFeature.getPpi());
        this.paint.setFakeBoldText(this.style.isBold());
        String text = block.demo();
        float width = this.paint.measureText(text, 0, text.length());
        Paint.FontMetrics fontMetrics = this.paint.getFontMetrics();
        float height = fontMetrics.descent-fontMetrics.ascent;
        setBound(new Rect(bound.left, bound.top, (int)(bound.left+width), (int)(bound.top+height)));
    }

    @Override
    public void onDraw(Canvas canvas, List<DynamicNumber> dynamicNumbers) {
        String text = "";
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textMiddle = (fontMetrics.ascent+fontMetrics.descent)/2;
        for(Slice slice : block.slices()){
            String demo = slice.demo();
            float x = bound.left + paint.measureText(text,0, text.length());
            if(slice.isDynamic()&&slice instanceof NumberSlice){
                NumberSlice numberSlice = (NumberSlice)slice;
                dynamicNumbers.add(new DynamicNumber(numberSlice.getNumberType(), numberSlice.getAdjustDate(), numberSlice.getAccum(), new Point((int)x, bound.top), style));
            }else{
                canvas.drawText(demo, x, bound.centerY()-textMiddle,  paint);
            }
            text = text + demo;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textMiddle = (fontMetrics.ascent+fontMetrics.descent)/2;
        canvas.drawText(block.demo(), bound.left, bound.centerY()-textMiddle,  this.paint);
    }
}
