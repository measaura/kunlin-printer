package cn.westlan.coding.core.bean;

public enum PrintDirection {
    Positive((byte) 0x01),
    Negative((byte) 0x02);

    private final byte value;

    PrintDirection(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static PrintDirection parse(byte val){
        for(PrintDirection direction : values()){
            if(direction.value == val){
                return direction;
            }
        }
        return Positive;
    }
}
