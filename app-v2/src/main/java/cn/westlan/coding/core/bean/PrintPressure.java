package cn.westlan.coding.core.bean;

public enum PrintPressure {
    Low((byte) 0x01),
    Medium((byte) 0x02),
    High((byte) 0x03);

    private final byte value;

    PrintPressure(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static PrintPressure parse(byte val){
        for(PrintPressure pressure : values()){
            if(pressure.value == val){
                return pressure;
            }
        }
        return Low;
    }
}
