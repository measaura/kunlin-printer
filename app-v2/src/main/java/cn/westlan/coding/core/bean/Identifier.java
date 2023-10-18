package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.io.IOUtil;

public enum Identifier {
    Head32(new byte[]{(byte)0x32, (byte)0x01}, "3201", new CanvasFeature(32, 20, 12),"https://coding-machine.oss-accelerate.aliyuncs.com/firmware/32/version.json"),
//    Head32(new byte[]{(byte)0x32, (byte)0x01}, "2401", new CanvasFeature(24, 20, 8, 4)),
    Head24(new byte[]{(byte)0x24, (byte)0x01}, "2401", new CanvasFeature(24, 20, 8), "https://coding-machine.oss-accelerate.aliyuncs.com/firmware/24/version.json"),
    Head25(new byte[]{(byte)0x25, (byte)0x01}, "2501", new CanvasFeature(24, 20, 8), "https://coding-machine.oss-accelerate.aliyuncs.com/firmware/24/version.json");

    Identifier(byte[] value, String name, CanvasFeature canvasFeature, String checkUpdateUrl) {
        this.value = value;
        this.name = name;
        this.canvasFeature = canvasFeature;
        this.checkUpdateUrl = checkUpdateUrl;
    }

    private final byte[] value;
    private final String name;
    private final CanvasFeature canvasFeature;
    private final String checkUpdateUrl;

    public String getName() {
        return name;
    }

    public CanvasFeature getCanvasFeature() {
        return canvasFeature;
    }

    public String getCheckUpdateUrl() {
        return checkUpdateUrl;
    }

    public static Identifier parse(ByteArray byteArray){
        for(Identifier identifier : values()){
            if(IOUtil.equals(byteArray.value(), identifier.value)){
                return identifier;
            }
        }
        return Head32;
    }
}
