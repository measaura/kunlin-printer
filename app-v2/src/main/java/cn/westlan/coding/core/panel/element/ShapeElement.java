package cn.westlan.coding.core.panel.element;

import android.graphics.*;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.io.Pack;
import cn.westlan.coding.core.panel.Area;
import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.panel.attribute.BaseAttribute;
import cn.westlan.coding.core.panel.attribute.OptionsAttribute;
import cn.westlan.coding.core.panel.attribute.RangeAttribute;
import cn.westlan.coding.core.panel.attribute.StepAttribute;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShapeElement extends Element{
    public static final int originWidth = 200;
    public static final int originHeight = 100;
    public static final int shape = 1;

    public final RangeAttribute lineWidth;
    public final BaseAttribute<Boolean> fill;

    public ShapeElement() {
        this(new Rect(0, 0, originWidth, originHeight));
    }

    public ShapeElement(Rect bound) {
        super(bound);
        this.lineWidth = new RangeAttribute(R.string.thickness, 4, 1, 9, val->{invalidate();return true;});
        this.fill = new BaseAttribute<>(R.string.fill_shape, false, val -> {invalidate();return true;});
    }

    public ShapeElement(Rect bound, float lineWidth, boolean fill) {
        super(bound);
        this.lineWidth = new RangeAttribute(R.string.thickness, lineWidth, 1, 9, val->{invalidate();return true;});
        this.fill = new BaseAttribute<>(R.string.fill_shape, fill, val -> {invalidate();return true;});
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        super.readFrom(inputStream);
        int lineWidth = (int)IOUtil.readInt(inputStream);
        boolean fill = IOUtil.readByte(inputStream)!=0x00;
        this.lineWidth.set((float)lineWidth);
        this.fill.set(fill);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        super.writeTo(outputStream);
        IOUtil.writeInt(outputStream, lineWidth.get().intValue());
        IOUtil.writeByte(outputStream, (byte) (fill.get()?0x01:0x00));
    }

    @Override
    public Element clone() {
        return new ShapeElement(this.bound, this.lineWidth.get(), this.fill.get());
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        if(getSelected() == Status.Single && !isLocked()){
            Bitmap dragXBitmap = PrinterApplication.dragXBitmap;
            canvas.drawBitmap(dragXBitmap, bound.right- dragXBitmap.getWidth()/2f, bound.centerY()-dragXBitmap.getHeight()/2f, this.paint);
            Bitmap dragYBitmap = PrinterApplication.dragYBitmap;
            canvas.drawBitmap(dragYBitmap, bound.centerX()- dragYBitmap.getWidth()/2f, bound.bottom-dragYBitmap.getHeight()/2f, this.paint);
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
            Bitmap dragYBitmap = PrinterApplication.dragYBitmap;
            RectF dragYBound = new RectF(bound.centerX()- dragYBitmap.getWidth()/2f,
                    bound.bottom-dragYBitmap.getHeight()/2f,
                    bound.centerX()+ dragYBitmap.getWidth()/2f, bound.bottom+dragYBitmap.getHeight()/2f);
            if(dragYBound.contains(point.x, point.y)){
                return Area.DragY;
            }
        }
        return super.pointIn(point);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int i = shape;
        if (i == 1) {
            canvas.drawRect(bound.left, bound.top, bound.right, bound.bottom, this.paint);
//        } else if (i == 2) {
//            RectF rectF = new RectF(bound.left, bound.top, bound.right, bound.bottom);
//            int i2 = this.cornerRatio;
//            canvas.drawRoundRect(rectF, (float) i2, (float) i2, this.paint);
//        } else if (i == 3) {
//            canvas.drawOval(new RectF(bound.left, bound.top, bound.right, bound.bottom), this.paint);
        }
    }


    @Override
    public void refresh(CanvasFeature canvasFeature) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(this.lineWidth.get());
        if (this.fill.get()) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setColor(0xff000000);
        this.paint = paint;
    }
}
