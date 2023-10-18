package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
public class PrintParams implements Writable, Readable {
    private PrintTemp temperature;
    private byte pressure;
    private PrintDirection direction;
    private byte delay;
    private byte position;

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        byte[] bytes = new byte[5];
        int size = inputStream.read(bytes, 0, 5);
        if(size != 5){
            throw new IOException();
        }
        temperature = PrintTemp.parse(bytes[0]);
        pressure = bytes[1];
        direction = PrintDirection.parse(bytes[2]);
        delay = bytes[3];
        position = bytes[4];
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        byte[] bytes = new byte[]{temperature.value(), pressure, direction.value(), delay, position};
        outputStream.write(bytes);
    }
}
