package cn.westlan.coding.core.panel.element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;
import cn.westlan.coding.core.panel.Area;
import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.panel.attribute.Attribute;
import cn.westlan.coding.core.panel.attribute.Attributes;
import cn.westlan.coding.core.panel.block.DynamicNumber;
import cn.westlan.coding.core.panel.select.Selectable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public abstract class Element implements Selectable, Attributes, Writable, Readable {
    protected Rect bound;
    protected Paint paint = new Paint();
    protected boolean locked = false;
    protected Status selected = Status.No;
    private boolean dirty = true;
    private boolean moved = false;
    private boolean rotate = false;

    public Element(Rect bound) {
        this.bound = bound;
    }

    public int x() {
        return bound.left;
    }

    public int y() {
        return bound.top;
    }

    public int width() {
        return bound.width();
    }

    public int height() {
        return bound.height();
    }

    public int centerX() {
        return bound.centerX();
    }

    public int centerY() {
        return bound.centerY();
    }

    public void move(int dx, int dy){
        if(locked)return;
        bound.offset(dx, dy);
        moved = true;
    }

    public void moveTo(int x, int y){
        if(locked)return;
        bound.offsetTo(x, y);
        moved = true;
    }

    public Rect getBound() {
        return bound;
    }

    protected void setBound(Rect bound) {
        this.bound = bound;
    }

    public boolean isRotate() {
        return rotate;
    }

    public boolean isRotatable(){
        return false;
    }

    public boolean rotate180(){
        if(!isRotatable()){
            return false;
        }
        rotate = !rotate;
        return true;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Status getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Status status) {
        this.selected = status;
    }

    public void dragX(int delta){
        if(delta+width() <= 0)return;
        Rect old = getBound();
        this.setBound(new Rect(old.left, old.top, old.right+delta, old.bottom));
    }

    public void dragY(int delta){
        if(delta+height() <= 0)return;
        Rect old = getBound();
        this.setBound(new Rect(old.left, old.top, old.right, old.bottom+delta));
    }

    public abstract Element clone();

    public void invalidate(){
        this.dirty = true;
    }

    public abstract void refresh(CanvasFeature canvasFeature);

    public void beforeDraw(CanvasFeature canvasFeature){
        if(this.moved){
            int width = canvasFeature.getWidth()*canvasFeature.getPpi();
            int height = canvasFeature.getHeight()*canvasFeature.getPpi();
            int dx = 0;
            int dy = 0;
            if(getBound().left < 0){
                dx = -getBound().left;
            }else if(getBound().right > width){
                dx = width-getBound().right;
            }
            if(getBound().top < 0){
                dy = -getBound().top;
            }else if(getBound().bottom > height){
                dy = height-getBound().bottom;
            }
            this.bound.offset(dx, dy);
            this.moved = false;
        }
        if(this.dirty){
            refresh(canvasFeature);
            this.dirty = false;
        }
    }

    public void onDraw(Canvas canvas, List<DynamicNumber> dynamicNumbers){
        this.onDraw(canvas);
    }

    public abstract void onDraw(Canvas canvas);

    public void onDrawForeground(Canvas canvas){
        if(!Status.No.equals(selected)){
            Paint paint = new Paint();
            if (isLocked()) {
                paint.setColor(0xff888888);
            } else {
                paint.setColor(0xffff0000);
            }
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            canvas.drawRect(bound, paint);
        }
    }

    public Area pointIn(Point point) {
        if(this.bound.contains(point.x, point.y)){
            return Area.Inside;
        }
        return Area.Outside;
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        bound.left = (int)IOUtil.readShort(inputStream);
        bound.top = (int)IOUtil.readShort(inputStream);
        bound.right = (int)IOUtil.readShort(inputStream);
        bound.bottom = (int)IOUtil.readShort(inputStream);
        this.rotate = IOUtil.readBool(inputStream);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        IOUtil.writeShort(outputStream, bound.left);
        IOUtil.writeShort(outputStream, bound.top);
        IOUtil.writeShort(outputStream, bound.right);
        IOUtil.writeShort(outputStream, bound.bottom);
        IOUtil.writeBool(outputStream, rotate);
    }

    @Override
    public List<Attribute<?>> attributes() {
        LinkedList<Attribute<?>> attributes = new LinkedList<>();
        Field[] array = getClass().getFields();
        for(Field field : array){
            if(Attribute.class.isAssignableFrom(field.getType())){
                field.setAccessible(true);
                try {
                    Attribute<?> attribute = (Attribute<?>)field.get(this);
                    attributes.addFirst(attribute);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return attributes;
    }
}
