package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Data
public class Result implements Readable {
    private boolean success = false;
//    0X01,收到数据，并且数据能被正确解析，
//    0X02,收到数据，但是数据有错
    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        byte value = (byte)inputStream.read();
        success = value == 0x01;
    }

    public Boolean isSuccess(){
        return success;
    }
}
