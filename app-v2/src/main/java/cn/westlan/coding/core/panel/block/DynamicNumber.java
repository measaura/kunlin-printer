package cn.westlan.coding.core.panel.block;

import android.graphics.Point;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DynamicNumber {
    private NumberType numberType;
    private AdjustDate adjustDate;
    private Accum accum;
    private Point point;
    private Style style;
}
