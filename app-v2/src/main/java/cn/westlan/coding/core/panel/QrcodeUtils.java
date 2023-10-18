package cn.westlan.coding.core.panel;

import android.graphics.*;
import cn.westlan.coding.PrinterApplication;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class QrcodeUtils {

    public static Bitmap createQRCode(String content, int size) {
        try {
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size,
                    size, hints);
            int margin = getMargin(bitMatrix);
            int realSize = Integer.max(1, size-margin*2);
            int[] pixels = new int[realSize * realSize];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < realSize; y++) {
                for (int x = 0; x < realSize; x++) {
                    if (bitMatrix.get(x+margin, y+margin)) {
                        pixels[y * realSize + x] = 0xff000000;
                    } else {
                        pixels[y * realSize + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(realSize, realSize, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, realSize, 0, 0, realSize, realSize);
            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap;
        } catch (WriterException e) {
            return scaleBitmap(PrinterApplication.errorBitmap, size, size);
        }
    }

    /**
     * 将指定的内容生成成二维码
     *
     * @param content 将要生成二维码的内容
     * @return 返回生成好的二维码事件
     * @throws WriterException WriterException异常
     */
    public static Bitmap createBarcode(String content, BarcodeFormat format, int qrWidth, int qrHeight) throws WriterException {
        //文字的高度
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix result = new MultiFormatWriter().encode(content, format, qrWidth, qrHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int margin = getMargin(result);
        int contentWidth = Integer.max(width-margin*2, 1);
        int[] pixels = new int[contentWidth * height];
        for (int y = 0; y < height; y++) {
            int offset = y * contentWidth;
            for (int x = 0; x < contentWidth; x++) {
                if(result.get(x+margin, y)){
                    pixels[offset + x] = 0xff000000;
                }
            }
        }
        Bitmap qrBitmap = Bitmap.createBitmap(contentWidth, height, Bitmap.Config.ARGB_8888);
        qrBitmap.setPixels(pixels, 0, contentWidth, 0, 0, contentWidth, height);

        float textSize = contentWidth/150f*16;
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setFilterBitmap(true);
        //字体大小
        p.setTextSize(textSize);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        content = content.trim();
        float textHeight = p.getFontMetrics().descent-p.getFontMetrics().ascent;

        float textWidth = p.measureText(content, 0, content.length());
        //大的bitmap
        int maxWidth = Integer.max((int)Math.ceil(textWidth), contentWidth);
        Bitmap bigBitmap = Bitmap.createBitmap(maxWidth, qrHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bigBitmap);
        Rect srcRect = new Rect(0, 0, contentWidth, height);
        Rect dstRect = new Rect((maxWidth-contentWidth)/2, 0, (maxWidth-contentWidth)/2+contentWidth, qrHeight-(int)textHeight);
        canvas.drawBitmap(qrBitmap, srcRect, dstRect, null);
        canvas.translate(maxWidth/2f, textHeight);
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(content, 0, content.length(), 0, qrHeight-textHeight, p);
        return bigBitmap;
    }

    public static Bitmap createErrorBarcode(String content, int width, int height) {
        try {
            Bitmap bitmap = createBarcode(content, BarcodeFormat.CODE_128, width, height);
            Canvas v12 = new Canvas(bitmap);
            int v0 = bitmap.getWidth() / 2;
            int v1 = height / 2;
            int v2 = Math.min(v0, v1);
            Bitmap v2_1 = scaleBitmap(PrinterApplication.errorBitmap, v2, v2);
            v12.drawBitmap(v2_1, ((float)(v0 - v2_1.getWidth() / 2)), ((float)(v1 - v2_1.getHeight() / 2)), null);
            return bitmap;
        }
        catch(Exception e) {
            return scaleBitmap(PrinterApplication.errorBitmap, width, height);
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        if(bitmap == null) {
            return null;
        }
        int originH = bitmap.getHeight();
        int originW = bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.setScale((((float)width)) / (((float)originW)), (((float)height)) / (((float)originH)));
        return Bitmap.createBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true), 0, 0, originW, originH, matrix, true);
    }

    private static int getMargin(BitMatrix matrix){
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                if(matrix.get(x, y))
                    return x;
            }
        }
        return width-1;
    }

//    private static int getEndX(BitMatrix matrix){
//        int width = matrix.getWidth();
//        int height = matrix.getHeight();
//        for(int x = width-1; x >= 0; --x){
//            for(int y = 0; y < height; ++y){
//                if(matrix.get(x, y))
//                    return x;
//            }
//        }
//        return 0;
//    }
}
