package cn.westlan.coding.core.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public interface Writable {
    void writeTo(ByteArrayOutputStream outputStream) throws IOException;
    default Pack[] pack(final int headLength) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.writeTo(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if(bytes.length == 0){
            Pack[] packs = new Pack[1];
            packs[0] = new Pack(65535, new byte[0]);
            return packs;
        }
        //两字节position
        int maxPackLength = 200-headLength-2;
        int size = (bytes.length+maxPackLength-1)/maxPackLength;
        //留一个结束pack
        Pack[] packs = new Pack[size+1];
        int i = 0;
        for(; i < size-1; ++i){
            int position = i*maxPackLength;
            packs[i] = new Pack(position, Arrays.copyOfRange(bytes, position, position+maxPackLength));
        }
        int position = i*maxPackLength;
        packs[i] = new Pack(position, Arrays.copyOfRange(bytes, position, bytes.length));
        packs[size] = new Pack(65535, new byte[0]);
        return packs;
    }
}
