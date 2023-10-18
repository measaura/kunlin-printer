package cn.westlan.coding.core.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class PrintTemp {
    private final byte value;

    private PrintTemp(byte value) {
        this.value = value;
    }

    public static PrintTemp parse(byte value){
        return new PrintTemp(value);
    }


}
