package cn.westlan.coding.core.panel.element;

import android.graphics.*;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.panel.Area;
import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.panel.QrcodeUtils;
import cn.westlan.coding.core.panel.attribute.BaseAttribute;
import cn.westlan.coding.core.panel.attribute.OptionsAttribute;
import cn.westlan.coding.core.panel.attribute.RangeAttribute;
import com.google.zxing.BarcodeFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BarcodeElement extends Element{
    public static final String DEFAULT_CODE = "1234567890";
    public static final String DEFAULT_FORMAT = "CODE-128";
    public static final int originWidth = 100;
    public static final int originHeight = 100;
    public static final Map<String, BarcodeFormat> barcodeFormatMap = new LinkedHashMap<String, BarcodeFormat>(){{
        put("CODE-128", BarcodeFormat.CODE_128);
        put("CODE-39", BarcodeFormat.CODE_39);
        put("CODE-93", BarcodeFormat.CODE_93);
        put("CODABAR", BarcodeFormat.CODABAR);
        put("ITF", BarcodeFormat.ITF);
        put("EAN-8", BarcodeFormat.EAN_8);
        put("EAN-13", BarcodeFormat.EAN_13);
        put("UPC-A", BarcodeFormat.UPC_A);
        put("UPC-E", BarcodeFormat.UPC_E);
    }};
    public final BaseAttribute<String> text;
    public final OptionsAttribute<BarcodeFormat> format;
    public final RangeAttribute scale;
    private Bitmap bitmap;
    private final Matrix matrix = new Matrix();

    public BarcodeElement() {
        this(new Rect(0, 0, originWidth, originHeight));
    }

    public BarcodeElement(Rect bound) {
        super(bound);
        this.text = new BaseAttribute<>(R.string.content, DEFAULT_CODE, val -> {invalidate();return true;});
        this.format = new OptionsAttribute<>(R.string.format_type, barcodeFormatMap, DEFAULT_FORMAT, val -> {invalidate();return true;});
//        this.scale = StepAttribute.generate("放大系数", 1, 1, 10, 1, stepVal -> {invalidate();return true;});
        this.scale = new RangeAttribute(R.string.scale_ratio, 1, 1, 10, stepVal -> {invalidate();return true;});
    }

    public BarcodeElement(Rect bound, String text, String format, float scale) {
        super(bound);
        this.text = new BaseAttribute<>(R.string.content, text, val -> {invalidate();return true;});;
        this.format = new OptionsAttribute<>(R.string.format_type, barcodeFormatMap, format, val -> {invalidate();return true;});
//        this.scale = StepAttribute.generate("放大系数", scale, 1, 10, 1, stepVal -> {invalidate();return true;});
        this.scale = new RangeAttribute(R.string.scale_ratio, scale, 1, 10, stepVal -> {invalidate();return true;});
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        super.readFrom(inputStream);
        String text = IOUtil.readString(inputStream);
        String format = IOUtil.readString(inputStream);
        int scale = (int)IOUtil.readInt(inputStream);
        this.text.set(text);
        this.format.setSelected(format);
        this.scale.set((float)scale);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        super.writeTo(outputStream);
        IOUtil.writeString(outputStream, text.get());
        IOUtil.writeString(outputStream, format.getSelected());
        IOUtil.writeInt(outputStream, scale.get().intValue());
    }

    @Override
    public Element clone() {
        return new BarcodeElement(this.bound, this.text.get(), this.format.getSelected(), this.scale.get());
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        if(getSelected() == Status.Single && !isLocked()){
            Bitmap dragYBitmap = PrinterApplication.dragYBitmap;
            canvas.drawBitmap(dragYBitmap, bound.centerX()- dragYBitmap.getWidth()/2f, bound.bottom-dragYBitmap.getHeight()/2f, this.paint);
        }

    }

    @Override
    public Area pointIn(Point point) {
        if(getSelected() == Status.Single && !isLocked()){
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
        matrix.setTranslate(x(), y());
        if(isRotate()){
            matrix.postRotate(180, centerX(), centerY());
        }
        canvas.drawBitmap(this.bitmap, matrix, this.paint);
    }

    @Override
    public boolean isRotatable() {
        return true;
    }

    @Override
    public void dragY(int delta) {
        super.dragY(delta);
        int x = x();
        int y = y();
        double scale = this.scale.get();
        BarcodeFormat format = this.format.get();
        try {
            this.bitmap = QrcodeUtils.createBarcode(text.get(), format, (int)(scale*originWidth), getBound().height());
        }catch (Exception e){
            this.bitmap = QrcodeUtils.createErrorBarcode(text.get(), (int)(scale*originWidth), getBound().height());
        }
        setBound(new Rect(x, y, x+bitmap.getWidth(), y+bitmap.getHeight()));
    }

    @Override
    public void refresh(CanvasFeature canvasFeature) {
        int x = x();
        int y = y();
        double scale = this.scale.get();
        BarcodeFormat format = this.format.get();
        try {
            this.bitmap = QrcodeUtils.createBarcode(text.get(), format, (int)(scale*originWidth), getBound().height());
        }catch (Exception e){
            this.bitmap = QrcodeUtils.createErrorBarcode(text.get(), (int)(x+scale*originWidth), getBound().height());
        }
        setBound(new Rect(x, y, x+bitmap.getWidth(), y+bitmap.getHeight()));
    }
}
