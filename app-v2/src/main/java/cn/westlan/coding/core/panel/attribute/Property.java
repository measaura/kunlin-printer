package cn.westlan.coding.core.panel.attribute;

public interface Property<T> {
    T get();
    boolean set(T t);
}
