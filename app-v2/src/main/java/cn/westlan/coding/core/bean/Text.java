package cn.westlan.coding.core.bean;

import androidx.annotation.NonNull;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Text implements Readable, Writable {
    private String value;

    public Text() {
    }

    public Text(String value) {
        this.value = value;
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        byte[] bytes = new byte[16];
        int length = inputStream.read(bytes);
        this.value = new String(bytes, 0, length, "GBK");
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        byte[] bytes = this.value.getBytes("GBK");
        outputStream.write(bytes);
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
