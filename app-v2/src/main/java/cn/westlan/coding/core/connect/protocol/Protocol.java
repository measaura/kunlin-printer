package cn.westlan.coding.core.connect.protocol;

import cn.westlan.coding.core.bean.*;
import cn.westlan.coding.core.io.Pack;
import cn.westlan.coding.core.io.IOUtil;

import java.util.ArrayList;
import java.util.List;


public interface Protocol<T> {
    List<Request<?>> Requests = new ArrayList<>();
    List<Reply<?>> Replies = new ArrayList<>();

    enum Direction{
        Send,
        Receive
    }

    static Reply<?> getReply(byte[] data){
        for(Reply<?> value : Replies){
            if(IOUtil.startWith(data, value.key())){
                return value;
            }
        }
        return Undefined;
    }

    byte[] key();
    Direction direction();
    Class<T> getContentClass();

    Request<PrintParams>  WritePrintParams = new Request<>(new byte[]{0X11}, PrintParams.class);
    Request<Empty>  Print = new Request<>(new byte[]{0X12,0x01,0x00}, Empty.class);
    Request<Empty>  SwitchOn = new Request<>(new byte[]{0X12,0x00,0x01}, Empty.class);
    Request<Empty>  SwitchOff = new Request<>(new byte[]{0X12,0x00,0x02}, Empty.class);
    Request<Text>  WriteDeviceName = new Request<>(new byte[]{0x13,0x01}, Text.class);
    Request<Text>  WriteProductLine = new Request<>(new byte[]{0x13,0x02}, Text.class);
    Request<Text>  WriteFactoryNo = new Request<>(new byte[]{0x13,0x03}, Text.class);
    Request<Number2B>  WriteResistance = new Request<>(new byte[]{0x13,0x04}, Number2B.class);
    Request<Empty>  ReadPrintParams = new Request<>(new byte[]{0x14, 0x21}, Empty.class);
    Request<Empty> ReadPrintContent = new Request<>(new byte[]{0x14, 0x25}, Empty.class);
    Request<Empty>  ReadDeviceName = new Request<>(new byte[]{0x14, 0x31}, Empty.class);
    Request<Empty>  ReadProductLine = new Request<>(new byte[]{0x14, 0x32}, Empty.class);
    Request<Empty>  ReadFactoryNo = new Request<>(new byte[]{0x14, 0x33}, Empty.class);
    Request<Empty>  ReadResistance = new Request<>(new byte[]{0x14, 0x34}, Empty.class);
    Request<Empty>  ReadPrintStatus = new Request<>(new byte[]{0x14, 0x41}, Empty.class);
    Request<Empty>  ReadSerialNumber = new Request<>(new byte[]{0x14, 0x51}, Empty.class);
    Request<Empty>  ReadFirmware = new Request<>(new byte[]{0x14, 0x52}, Empty.class);
    Request<Empty>  ReadIdentifier = new Request<>(new byte[]{0x14, 0x53}, Empty.class);
    Request<Pack> WriteSlaveContent = new Request<>(new byte[]{0x15}, Pack.class);
    Request<Pack> WritePrintContent = new Request<>(new byte[]{0x16}, Pack.class);
    Request<Time>  SyncTime = new Request<>(new byte[]{0x18}, Time.class);
    Request<Text>  Unlock = new Request<>(new byte[]{0x19}, Text.class);
    Request<Text>  VerifyPassword = new Request<>(new byte[]{0x1C}, Text.class);
    Request<Text>  ModifyPassword = new Request<>(new byte[]{0x1D}, Text.class);

    Reply<PrintParams>  ReplyPrintParams = new Reply<>(new byte[]{0X21}, PrintParams.class);
    Reply<PrintContent> ReplyPrintContent = new Reply<>(new byte[]{0X25}, PrintContent.class, true);
    Reply<UnlockResult>  ReplyUnlock = new Reply<>(new byte[]{0X29}, UnlockResult.class);
    Reply<Text> ReplyDeviceName = new Reply<>(new byte[]{0x31}, Text.class);
    Reply<Text> ReplyProductLine = new Reply<>(new byte[]{0x32}, Text.class);
    Reply<Text> ReplyFactoryNo = new Reply<>(new byte[]{0x33}, Text.class);
    Reply<Number2B> ReplyResistance = new Reply<>(new byte[]{0x34}, Number2B.class);
    Reply<PrintStatus> ReplyPrintStatus = new Reply<>(new byte[]{0x41}, PrintStatus.class);
    Reply<Text> ReplySerialNumber = new Reply<>(new byte[]{0x51}, Text.class);
    Reply<Firmware> ReplyFirmware = new Reply<>(new byte[]{0x52}, Firmware.class);
    Reply<ByteArray> ReplyIdentifier = new Reply<>(new byte[]{0x53}, ByteArray.class);
    Reply<Result> ReplyResult = new Reply<>(new byte[]{(byte) 0X8f}, Result.class);
    Reply<Empty> Undefined = new Reply<>(new byte[0], Empty.class);
}

