package cn.westlan.coding.core.io;

import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
public class Pack implements Writable {
    private final int position;
    private final byte[] data;

    public Pack(int position, byte[] data) {
        this.position = position;
        this.data = data;
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        outputStream.write(position>>8&0xff);
        outputStream.write(position&0xff);
        outputStream.write(data);
    }

}
