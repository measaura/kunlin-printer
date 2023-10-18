package cn.westlan.coding.core.panel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanvasFeature {
    //X尺寸
    private final int width;
    //Y尺寸
    private final int height;
    //1mm 像素数
    private final int ppi;
}
