package cn.westlan.coding.core.panel.select;

public interface Selectable {
    enum Status{
        No,
        Single,
        Batch
    }

    void setSelected(Status status);
}
