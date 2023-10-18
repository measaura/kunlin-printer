package cn.westlan.coding.core.panel;

import cn.westlan.coding.core.panel.block.impl.AdjustDateBlock;
import cn.westlan.coding.core.panel.block.impl.SerialNumberBlock;
import cn.westlan.coding.core.panel.block.impl.SimpleBlock;
import cn.westlan.coding.core.panel.element.*;

import java.util.HashMap;
import java.util.Map;

public class TypeMap {
    public static Map<Byte, Class<?>> Code2Element =new HashMap<>();
    public static Map<Class<?>, Byte> Element2Code =new HashMap<>();
    public static Map<Byte, Class<?>> Code2Block =new HashMap<>();
    public static Map<Class<?>, Byte> Block2Code =new HashMap<>();

    static {
       Class<?>[] classes = {BarcodeElement.class, DateElement.class, LineElement.class, QrcodeElement.class, SerialNumberElement.class, ShapeElement.class, TextElement.class, TimeElement.class};
       for(byte i = 0; i < classes.length; ++i){
           Code2Element.put(i, classes[i]);
           Element2Code.put(classes[i], i);
       }
        Class<?>[] classes2 = {SimpleBlock.class, AdjustDateBlock.class, SerialNumberBlock.class};
        for(byte i = 0; i < classes2.length; ++i){
            Code2Block.put(i, classes2[i]);
            Block2Code.put(classes2[i], i);
        }
    }
}
