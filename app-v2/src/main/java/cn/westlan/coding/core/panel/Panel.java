package cn.westlan.coding.core.panel;

import android.graphics.Bitmap;
import cn.westlan.coding.core.panel.block.DynamicNumber;

import java.util.List;

public interface Panel {

    interface Slave{
        void onShow(Bitmap bitmap, List<DynamicNumber> dynamicNumbers);
    }

    void send(Slave slave);
}
