package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Empty implements Readable, Writable {
    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {

    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {

    }
}
