package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;
import cn.westlan.coding.core.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Number2B implements Readable, Writable {
    private int value;

    public Number2B() {
    }

    public Number2B(short value) {
        this.value = value;
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        this.value = IOUtil.readShort(inputStream);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        IOUtil.writeShort(outputStream, value);
    }

    public int getValue() {
        return value;
    }
}
