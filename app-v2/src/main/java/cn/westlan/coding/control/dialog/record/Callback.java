package cn.westlan.coding.control.dialog.record;

import cn.westlan.coding.core.bean.PrintContent;

public interface Callback {
    PrintContent onSelected(int idx, boolean ret);
}
