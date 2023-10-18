package cn.westlan.coding.core.bean;

public enum RunningState {
    Running((byte) 0x01),
    Stopped((byte) 0x02),
    Fault((byte) 0x03),
    Locked((byte) 0x04);

    private final byte value;

    RunningState(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public static RunningState parse(byte val){
        for(RunningState pressure : values()){
            if(pressure.value == val){
                return pressure;
            }
        }
        return Running;
    }
}
