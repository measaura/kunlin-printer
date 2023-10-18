package cn.westlan.coding.core.panel.block;

public enum NumberType {
    HighYear((byte) 0x01),
    LowYear((byte) 0x02),
    Month((byte) 0x03),
    Month_enGB((byte) 0x07),
    Month_idID((byte) 0x08),
    Day((byte) 0x04),
    Hour((byte) 0x05),
    Minute((byte) 0x06),
    Serial((byte) 0x10);
    private final byte value;

    NumberType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
