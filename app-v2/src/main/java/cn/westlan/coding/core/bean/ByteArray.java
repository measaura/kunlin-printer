package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@Accessors(fluent = true)
public class ByteArray implements Readable, Writable {
    private byte[] value;

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.available()];
        int length = inputStream.read(bytes, 0, bytes.length);
        this.value = bytes;
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        outputStream.write(value);
    }
}
