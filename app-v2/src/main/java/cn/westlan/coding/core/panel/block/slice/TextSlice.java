package cn.westlan.coding.core.panel.block.slice;

import cn.westlan.coding.core.panel.block.Slice;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextSlice implements Slice {
    private String value;

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public String demo() {
        return value;
    }

    @Override
    public int length() {
        return value.length();
    }
}
