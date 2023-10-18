package cn.westlan.coding.core.connect.protocol;

import cn.westlan.coding.core.io.Writable;

public class Request<T extends Writable> implements Protocol<T>{

    private final byte[] key;
    private final Class<T> aClass;

    public Request(byte[] key, Class<T> aClass) {
        this.key = key;
        this.aClass = aClass;
        Requests.add(this);
    }

    @Override
    public byte[] key() {
        return key;
    }

    @Override
    public Direction direction() {
        return Direction.Send;
    }

    @Override
    public Class<T> getContentClass() {
        return aClass;
    }

    public RequestMsg<T> message(T content){
        return new RequestMsg<>(this, content);
    }
}