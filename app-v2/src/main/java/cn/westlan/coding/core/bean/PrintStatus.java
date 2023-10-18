package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.IOUtil;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Data
public class PrintStatus implements Readable {
    private RunningState runningState;
    private Integer currentPrintCount;
    private Integer printCount;
    private Integer packPerMinus;
    private Byte remainLength;

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        byte[] bytes = new byte[12];
        int size = inputStream.read(bytes, 0, 12);
        if(size != 12){
            throw new IOException();
        }
        this.runningState = RunningState.parse(bytes[0]);
        this.currentPrintCount = (int) IOUtil.readInt(bytes, 1);
        this.printCount = (int) IOUtil.readInt(bytes, 5);
        this.packPerMinus = IOUtil.readShort(bytes, 9);
        this.remainLength = bytes[11];
    }
}
