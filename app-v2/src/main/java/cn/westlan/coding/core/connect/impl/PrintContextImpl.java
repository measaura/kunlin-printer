package cn.westlan.coding.core.connect.impl;

import android.util.Log;
import cn.westlan.coding.core.connect.BleDevice;
import cn.westlan.coding.core.connect.PrintContext;
import cn.westlan.coding.core.io.Pack;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.connect.protocol.*;
import cn.westlan.coding.core.io.IOUtil;
import com.jakewharton.rxrelay2.PublishRelay;
import io.reactivex.Completable;
import io.reactivex.Observable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PrintContextImpl implements PrintContext, BleDevice.Receiver {

    private final PublishRelay<ReplyMsg<?>> replyRelay = PublishRelay.create();
    private final List<Pack> packList = new ArrayList<>();
    private final BleDevice bleDevice;
    private final AtomicReference<Reply<?>> lastReply = new AtomicReference<>(Protocol.Undefined);


    public PrintContextImpl(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
        this.bleDevice.register(this);
        this.replyRelay.subscribe(message -> Log.i(getClass().getSimpleName(), "add Message "+message));
    }

    @Override
    public BleDevice getBleDevice() {
        return this.bleDevice;
    }

    @Override
    public <T extends Readable> Observable<ReplyMsg<T>> observeReply(Reply<T> reply) {
        return replyRelay.filter(message -> message.getProtocol().equals(reply)).map(replyMsg -> (ReplyMsg<T>)replyMsg);
    }

    @Override
    public Completable sendMessage(RequestMsg<?> message) {
        Log.i(getClass().getSimpleName(), "sendMessage() ");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(message.getProtocol().key());
            message.getContent().writeTo(outputStream);
            return bleDevice.write(outputStream.toByteArray());
        } catch (IOException e) {
            return Completable.error(e);
        }
    }

    @Override
    public void close() {
        this.bleDevice.disconnect();
    }

    private <T extends Readable> void onMessage(ReplyMsg<T> message) {
        replyRelay.accept(message);
    }

    private <T extends Readable> ReplyMsg<T> generateReply(Reply<T> reply, byte[] data, int offset, int length) throws Exception{
        try {
            T readable = reply.getContentClass().newInstance();
            readable.readFrom(new ByteArrayInputStream(data, offset, length));
            return reply.message(readable);
        }catch (IOException | IllegalAccessException | InstantiationException e){
            Log.e(getClass().getSimpleName(), "read reply exception", e);
            throw e;
        }
    }

    private <T extends Readable> ReplyMsg<T> generateReply(Reply<T> reply, List<Pack> packList) throws Exception{
        try {
            T readable = reply.getContentClass().newInstance();
            readable.readFrom(packList);
            return reply.message(readable);
        }catch (IOException | IllegalAccessException | InstantiationException e){
            Log.e(getClass().getSimpleName(), "read reply exception", e);
            throw e;
        }
    }

    @Override
    public void onReceived(byte[] bytes) {
        Log.i(getClass().getSimpleName(), "onReceived() ");
        Reply<?> reply = Protocol.getReply(bytes);
        try {
            int headLen = reply.key().length;
            if(reply.isMulti()){
                Pack pack = new Pack(IOUtil.readShort(bytes, headLen), Arrays.copyOfRange(bytes, headLen+2, bytes.length));
                if(!IOUtil.equals(lastReply.get().key(), reply.key())){
                    packList.clear();
                }
                packList.add(pack);
                if(pack.getPosition() == 0x00ffff){
                    this.onMessage(generateReply(reply, packList));
                    packList.clear();
                }
                lastReply.set(reply);
            }else{
                lastReply.set(reply);
                this.onMessage(generateReply(reply, bytes, headLen, bytes.length-headLen));
            }
        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "onReceived exception", e);
        }
    }



}
