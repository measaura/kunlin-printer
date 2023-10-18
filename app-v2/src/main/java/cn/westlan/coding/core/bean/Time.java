package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.Writable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class Time implements Writable {

    private final Date date;

    public Time(Date date) {
        this.date = date;
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        outputStream.write(covert2Bytes(date));
    }

    private byte[] covert2Bytes(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        byte[] bytes = new byte[7];
        byte second = bytes[0] = (byte)calendar.get(Calendar.SECOND);
        byte minute = bytes[1] = (byte)calendar.get(Calendar.MINUTE);
        byte hour = bytes[2] = (byte)calendar.get(Calendar.HOUR_OF_DAY);
        byte day = bytes[3] = (byte)calendar.get(Calendar.DAY_OF_MONTH);
        byte month = bytes[4] = (byte)(calendar.get(Calendar.MONTH)+1);
        byte dayOfWeek = bytes[5] = (byte)calendar.get(Calendar.DAY_OF_WEEK);
        byte year = bytes[6] = (byte)(calendar.get(Calendar.YEAR)%100);
        return bytes;
    }
}
