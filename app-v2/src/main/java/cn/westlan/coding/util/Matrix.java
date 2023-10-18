package cn.westlan.coding.util;

import android.graphics.Bitmap;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public class Matrix {
    private int startY;
    private int endY;
    private int width;
    private int height;
    private byte[] data;

    public static Matrix readFrom(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int startY = getStartY(pixels, width, height);
        int endY = height;
        if(startY < height-1){
            endY = getEndY(pixels, width, height);
        }
        return new Matrix(startY, endY, width, height, convert2Bytes(pixels, width, startY, endY));
    }

    public Bitmap toBitmap(){
        int[] pixels = new int[width*height];
        Arrays.fill(pixels, 0xffffffff);
        int idx = startY*width;
        int mIdx = 0;
        for(int m = startY; m < endY; ++m){
            for(int i = 0; i < width/8; ++i){
                byte val = data[mIdx++];
                for(int j = 0; j < 8; j++){
                    if((val&0x80) == 0x80){
                        pixels[idx++] = 0xff000000;
                    }else{
                        idx++;
                    }
                    val = (byte)(val<<1);
                }
            }
            if(width%8 != 0){
                byte val = data[mIdx++];
                for(int j = 0; j < width%8; j++){
                    if((val&0x80) == 0x80){
                        pixels[idx++] = 0xff000000;
                    }{
                        idx++;
                    }
                    val = (byte)(val<<1);
                }
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    private static byte[] convert2Bytes(int[] pixels, int width, int startY, int endY){
        byte[] bits = new byte[(width+7)/8*(endY-startY)];
        int idx = 0;
        for(int m = startY; m < endY; ++m){
            int n = 0;
            for(; n < width/8; ++n){
                bits[idx++]= collect2byte(pixels, m*width+n*8, 8);
            }
            if(width%8 != 0){
                bits[idx++] = collect2byte(pixels, m*width+n*8, width%8);
            }
        }
        return bits;
    }

    private static byte collect2byte(int[] pixels, final int offset, int length){
        byte b = 0x00;
        int end = Math.min(8, length)+offset;
        int n = offset;
        for(; n < end; ++n){
            b = (byte) (b<<1|(isBlank(pixels[n])?(byte)0x00:(byte)0x01));
        }
        for(; n < offset+8; ++n){
            b = (byte) (b<<1);
        }
        return b;
    }

    private static int getStartY(int[] pixels, int width, int height){
        for(int m = 0; m < height; ++m){
            for(int n = 0; n < width; ++n){
                if(!isBlank(pixels[m*width+n]))
                    return m;
            }
        }
        return height;
    }

    private static int getEndY(int[] pixels, int width, int height){
        for(int m = height-1; m >= 0; --m){
            for(int n = 0; n < width; ++n){
                if(!isBlank(pixels[m*width+n]))
                    return m+1;
            }
        }
        return 0;
    }

    //透明和纯白都为空
    private static boolean isBlank(int pixel){
        if(pixel == 0x0) return true;
        if((pixel&0x00ff) < 0x007f)return false;
        if((pixel>>8&0x00ff) < 0x007f)return false;
        if((pixel>>16&0x00ff) < 0x007f)return false;
        return true;
    }
}
