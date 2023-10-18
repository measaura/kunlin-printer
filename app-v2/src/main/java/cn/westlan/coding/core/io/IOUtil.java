package cn.westlan.coding.core.io;

import android.util.Log;
import cn.westlan.coding.core.panel.TypeMap;
import cn.westlan.coding.core.panel.block.Block;
import cn.westlan.coding.core.panel.block.impl.AdjustDateBlock;
import cn.westlan.coding.core.panel.block.impl.SerialNumberBlock;
import cn.westlan.coding.core.panel.block.impl.SimpleBlock;
import cn.westlan.coding.core.panel.element.DateElement;
import cn.westlan.coding.core.panel.element.Element;
import cn.westlan.coding.core.panel.element.LineElement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class IOUtil {

    public static boolean equals(byte[] array1, byte[] array2){
        if(array2.length == 0)return false;
        if(array1.length != array2.length)return false;
        for(int i = 0; i < array2.length; ++i){
            if(array1[i] != array2[i])return false;
        }
        return true;
    }

    public static boolean startWith(byte[] array1, byte[] array2){
        if(array2.length == 0)return false;
        if(array1.length < array2.length)return false;
        for(int i = 0; i < array2.length; ++i){
            if(array1[i] != array2[i])return false;
        }
        return true;
    }

    public static long readInt(byte[] array, int offset){
        if(array.length < offset+4){
            return 0;
        }
        return (long)(0x00ff000000&array[offset]<<24)|(0x00ff0000&array[offset+1]<<16)|(0x00ff00&array[offset+2]<<8)|(0x00ff&array[offset+3]);
    }

    public static boolean readBool(ByteArrayInputStream inputStream){
        byte value = (byte)inputStream.read();
        return value == 0x01;
    }

    public static void writeBool(ByteArrayOutputStream outputStream, boolean value){
        outputStream.write(value?0x01:0x00);
    }

    public static long readInt(ByteArrayInputStream inputStream){
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0, 4);
        return readInt(bytes, 0);
    }

    public static void writeInt(ByteArrayOutputStream outputStream, long value){
        byte[] bytes = new byte[]{(byte)(value>>24&0xff), (byte)(value>>16&0xff), (byte)(value>>8&0xff), (byte)(value&0xff)};
        outputStream.write(bytes, 0, 4);
    }

    public static int readShort(byte[] array, int offset){
        if(array.length < offset+2){
            return 0;
        }
        return (int) (0x00ff00&array[offset]<<8)|(0x00ff&array[offset+1]);
    }

    public static int readShort(ByteArrayInputStream inputStream){
        byte[] bytes = new byte[2];
        inputStream.read(bytes, 0, 2);
        return readShort(bytes, 0);
    }

    public static void writeShort(ByteArrayOutputStream outputStream, int value){
        byte[] bytes = new byte[]{(byte)(value>>8&0xff), (byte)(value&0xff)};
        outputStream.write(bytes, 0, 2);
    }

    public static byte readByte(ByteArrayInputStream inputStream){
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        return bytes[0];
    }

    public static void writeByte(ByteArrayOutputStream outputStream, byte value){
        byte[] bytes = new byte[]{value};
        outputStream.write(bytes, 0, 1);
    }

    public static byte[] readBytes(ByteArrayInputStream inputStream, int size){
        byte[] bytes = new byte[size];
        inputStream.read(bytes, 0, size);
        return bytes;
    }

    public static void writeBytes(ByteArrayOutputStream outputStream, byte[] value){
        outputStream.write(value, 0, value.length);
    }

    public static String readString(ByteArrayInputStream inputStream) throws IOException {
        int size = readShort(inputStream);
        byte[] bytes = new byte[size];
        int length = inputStream.read(bytes, 0, size);
        return new String(bytes, 0, length, "GBK");
    }

    public static void writeString(ByteArrayOutputStream outputStream, String value) throws IOException {
        byte[] bytes = value.getBytes("GBK");
        writeShort(outputStream, (short) bytes.length);
        outputStream.write(bytes);
    }

    public static Block readBlock(ByteArrayInputStream inputStream) throws IOException {
        int size = readShort(inputStream);
        byte[] bytes = readBytes(inputStream, size);
        return bytes2Block(bytes);
    }

    public static void writeBlock(ByteArrayOutputStream outputStream, Block block) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtil.writeByte(byteArrayOutputStream, TypeMap.Block2Code.get(block.getClass()));
        block.writeTo(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        writeShort(outputStream, (short) bytes.length);
        outputStream.write(bytes);
    }

    private static Block bytes2Block(byte[] bytes) throws IOException {
        byte code = bytes[0];
        try {
            Block block  = (Block) TypeMap.Code2Block.get(code).newInstance();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes, 1, bytes.length-1);
            block.readFrom(byteArrayInputStream);
            return block;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SimpleBlock();
    }

    public static Element readElement(ByteArrayInputStream inputStream) throws IOException {
        int size = readShort(inputStream);
        byte[] bytes = readBytes(inputStream, size);
        return bytes2Element(bytes);
    }

    public static void writeElement(ByteArrayOutputStream outputStream, Element element) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Byte code = TypeMap.Element2Code.get(element.getClass());
        IOUtil.writeByte(byteArrayOutputStream, code);
        element.writeTo(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        writeShort(outputStream, (short) bytes.length);
        outputStream.write(bytes);
    }

    private static Element bytes2Element(byte[] bytes) throws IOException {
        byte code = bytes[0];
        try {
            Element element  = (Element) TypeMap.Code2Element.get(code).newInstance();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes, 1, bytes.length-1);
            element.readFrom(byteArrayInputStream);
            return element;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LineElement();
    }
}
