package cn.westlan.coding.core.panel.element;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import cn.westlan.coding.R;
import cn.westlan.coding.core.io.IOUtil;
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

public class QrcodeElement extends Element{
    public static final String DEFAULT_CODE = "1234567890";
    public static final String DEFAULT_FORMAT = "QR Code";
    public static final int originWidth = 100;
    public static final int originHeight = 100;
    public static final Map<String, BarcodeFormat> barcodeFormatMap = new LinkedHashMap<String, BarcodeFormat>(){{
        put("QR Code", BarcodeFormat.QR_CODE);
    }};
    public final BaseAttribute<String> text;
    public final OptionsAttribute<BarcodeFormat> format;
    public final RangeAttribute scale;
    private Bitmap bitmap;
    private final Matrix matrix = new Matrix();

    public QrcodeElement() {
        this(new Rect());
    }

    public QrcodeElement(Rect bound) {
        super(bound);
        this.text = new BaseAttribute<>(R.string.content, DEFAULT_CODE, val -> {invalidate();return true;});
        this.format = new OptionsAttribute<>(R.string.format_type, barcodeFormatMap, DEFAULT_FORMAT, val -> {invalidate();return true;});
//        this.scale = StepAttribute.generate("放大系数", 1, 1, 10, 1, stepVal -> {invalidate();return true;});

        this.scale = new RangeAttribute(R.string.scale_ratio, 1, 1, 10, stepVal -> {invalidate();return true;});
    }

    public QrcodeElement(Rect bound, String text, String format, float scale) {
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
        return new QrcodeElement(this.bound, this.text.get(), this.format.getSelected(), this.scale.get());
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
    public void refresh(CanvasFeature canvasFeature) {
        int x = x();
        int y = y();
        double scale = this.scale.get();
        this.bitmap = QrcodeUtils.createQRCode(text.get(), (int)(originWidth*scale));
        setBound(new Rect(x, y, (int)(x+bitmap.getWidth()), (int)(y+bitmap.getHeight())));
    }
}
