package cn.westlan.coding.core.panel.select;

import java.util.List;

public interface SelectListener<T extends Selectable> {
    void onSelectedChange(List<T> selected);
    default void onModeChange(boolean multi){}
}
