package cn.westlan.coding.core.panel.block;

import android.util.Log;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;
import cn.westlan.coding.core.panel.block.impl.AdjustDateBlock;
import cn.westlan.coding.core.panel.block.impl.SimpleBlock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface Block extends Writable, Readable {
    boolean backspace();
    List<Slice> slices();
    String demo();
}
