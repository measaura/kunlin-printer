package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Data
public class Firmware implements Readable {
    private String version;
    private String date;


    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        byte[] bytes = new byte[15];
        int length = inputStream.read(bytes, 0, 15);
        this.version = new String(bytes, 0, 5, "GBK");
        this.date = new String(bytes, 5, 10, "GBK");
    }
}
