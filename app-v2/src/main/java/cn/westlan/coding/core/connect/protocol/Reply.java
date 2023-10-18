package cn.westlan.coding.core.connect.protocol;

import cn.westlan.coding.core.io.Readable;

public class Reply<T extends Readable> implements Protocol<T> {
    private final byte[] key;
    private final Class<T > aClass;
    private final boolean multi;

    public Reply(byte[] key, Class<T> aClass) {
        this(key, aClass, false);
    }

    public Reply(byte[] key, Class<T> aClass, boolean multi) {
        this.key = key;
        this.aClass = aClass;
        this.multi = multi;
        if(key != null && key.length > 0){
            Replies.add(this);
        }
    }

    @Override
    public byte[] key() {
        return key;
    }

    @Override
    public Direction direction() {
        return Direction.Receive;
    }

    @Override
    public Class<T> getContentClass() {
        return aClass;
    }

    public boolean isMulti() {
        return multi;
    }

    public ReplyMsg<T> message(T content){
        return new ReplyMsg<>(this, content);
    }

}