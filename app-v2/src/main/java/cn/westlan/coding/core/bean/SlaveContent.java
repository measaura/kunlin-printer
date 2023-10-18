package cn.westlan.coding.core.bean;


import android.graphics.Bitmap;
import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.panel.block.DynamicNumber;
import cn.westlan.coding.core.io.Writable;
import cn.westlan.coding.core.panel.block.NumberType;
import cn.westlan.coding.util.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class SlaveContent implements Writable {
    private final byte identity = (byte) 0xdd;
    private final Bitmap bitmap;
    private final List<DynamicNumber> dynamicNumbers;

    public SlaveContent(Bitmap bitmap, List<DynamicNumber> dynamicNumbers) {
        this.bitmap = bitmap;
        this.dynamicNumbers = dynamicNumbers;
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        byte[] head = new byte[32];
        Matrix matrix = Matrix.readFrom(bitmap);
        byte[][] dynamics = convertDynamic(dynamicNumbers);
        int dynamicOffset = head.length+matrix.getData().length;
        int dynamicSize = dynamics.length*16;
        head[0] = identity;
        head[1] = getByte(matrix.getStartY()>>8);
        head[2] = getByte(matrix.getStartY());
        head[3] = getByte(matrix.getEndY()>>8);
        head[4] = getByte(matrix.getEndY());
        head[5] = getByte(matrix.getData().length>>8);
        head[6] = getByte(matrix.getData().length);
        head[7] = getByte(dynamicSize>>8);
        head[8] = getByte(dynamicSize);
        head[9] = getByte(dynamicOffset>>8);
        head[10] = getByte(dynamicOffset);
        head[11] = getByte(dynamics.length);
        outputStream.write(head);
        outputStream.write(matrix.getData());
        for(int i = 0; i < dynamics.length; ++i){
            outputStream.write(dynamics[i]);
        }
    }

    private byte[][] convertDynamic(List<DynamicNumber> dynamicNumbers){
        byte[][] dynamics = new byte[dynamicNumbers.size()][16];
        int index = 0;
        for(DynamicNumber dynamicNumber : dynamicNumbers){
            dynamics[index][0] = getByte(index);
            dynamics[index][1] = getByte(dynamicNumber.getNumberType().getValue());
            dynamics[index][2] = getByte(dynamicNumber.getStyle().getIdentifier());
            dynamics[index][3] = getByte(dynamicNumber.getPoint().x>>8);
            dynamics[index][4] = getByte(dynamicNumber.getPoint().x);
            dynamics[index][5] = getByte(dynamicNumber.getPoint().y>>8);
            dynamics[index][6] = getByte(dynamicNumber.getPoint().y);
            if(NumberType.Serial.equals(dynamicNumber.getNumberType())){
                dynamics[index][7] = getByte(dynamicNumber.getAccum().getStep());
                dynamics[index][8] = getByte(dynamicNumber.getAccum().getStart()>>24);
                dynamics[index][9] = getByte(dynamicNumber.getAccum().getStart()>>16);
                dynamics[index][10] = getByte(dynamicNumber.getAccum().getStart()>>8);
                dynamics[index][11] = getByte(dynamicNumber.getAccum().getStart());
                dynamics[index][12] = getByte(dynamicNumber.getAccum().getEnd()>>24);
                dynamics[index][13] = getByte(dynamicNumber.getAccum().getEnd()>>16);
                dynamics[index][14] = getByte(dynamicNumber.getAccum().getEnd()>>8);
                dynamics[index][15] = getByte(dynamicNumber.getAccum().getEnd());
            }else{
                dynamics[index][7] = getByte(dynamicNumber.getAdjustDate().getYearOffset());
                dynamics[index][8] = getByte(dynamicNumber.getAdjustDate().getMonthOffset());
                dynamics[index][9] = getByte(dynamicNumber.getAdjustDate().getDayOffset());
            }
            index++;
        }
        return dynamics;
    }

//    private byte getHighByte(int val){
//        return (byte) (0xff&(val>>8));
//    }

    private byte getByte(int val){
        return (byte) (0xff&(val));
    }


}
