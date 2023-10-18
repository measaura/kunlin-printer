package cn.westlan.coding.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface Readable {
    void readFrom(ByteArrayInputStream inputStream) throws IOException;

    default void readFrom(List<Pack> packList) throws IOException {
        int maxSize = packList.size()*200;
        byte[] bytes = new byte[maxSize];
        int length = 0;
        for(Pack pack : packList){
            if(pack.getPosition() == 0x00ffff)break;
            if(pack.getPosition() != length){
                readFrom(new ByteArrayInputStream(new byte[0]));
                return;
            }
            System.arraycopy(pack.getData(), 0, bytes, length, pack.getData().length);
            length += pack.getData().length;
        }
        readFrom(new ByteArrayInputStream(bytes, 0, length));
    }
}
