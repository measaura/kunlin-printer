package cn.westlan.coding.core.panel.element;

import android.graphics.*;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.panel.Area;
import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.panel.attribute.RangeAttribute;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LineElement extends Element{

    public final Path path = new Path();
    public RangeAttribute lineWidth;

    public LineElement() {
        this(new Rect(0, 0 ,50, 4));
    }

    public LineElement(Rect bound) {
        super(bound);
        this.lineWidth = new RangeAttribute(R.string.thickness, 4, 1, 9, val->{invalidate();return true;});
    }

    public LineElement(Rect bound, float lineWidth) {
        super(bound);
        this.lineWidth = new RangeAttribute(R.string.thickness, lineWidth, 1, 9, val->{invalidate();return true;});
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        super.readFrom(inputStream);
        int lineWidth = (int)IOUtil.readInt(inputStream);
        this.lineWidth.set((float) lineWidth);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        super.writeTo(outputStream);
        IOUtil.writeInt(outputStream, lineWidth.get().intValue());
    }

    @Override
    public Element clone() {
        return new LineElement(this.bound, lineWidth.get());
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        if(getSelected() == Status.Single && !isLocked()){
            Bitmap dragXBitmap = PrinterApplication.dragXBitmap;
            canvas.drawBitmap(dragXBitmap, bound.right- dragXBitmap.getWidth()/2f, bound.centerY()-dragXBitmap.getHeight()/2f, this.paint);
        }

    }

    @Override
    public Area pointIn(Point point) {
        if(getSelected() == Status.Single && !isLocked()){
            Bitmap dragXBitmap = PrinterApplication.dragXBitmap;
            RectF dragXBound = new RectF(bound.right- dragXBitmap.getWidth()/2f,
                    bound.centerY()-dragXBitmap.getHeight()/2f,
                    bound.right+ dragXBitmap.getWidth()/2f, bound.centerY()+dragXBitmap.getHeight()/2f);
            if(dragXBound.contains(point.x, point.y)){
                return Area.DragX;
            }
        }
        return super.pointIn(point);
    }

    @Override
    public void onDraw(Canvas canvas) {
        float y = (bound.bottom+bound.top)/2f;
        this.path.moveTo(bound.left, y);
        this.path.lineTo(bound.right, y);
        canvas.drawPath(this.path, this.paint);
        this.path.reset();
    }

    @Override
    public void refresh(CanvasFeature canvasFeature) {
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(this.lineWidth.get());
        this.paint.setColor(0xff000000);
        this.setBound(new Rect(bound.left, bound.top, bound.right, bound.top+lineWidth.get().intValue()));
    }
}
