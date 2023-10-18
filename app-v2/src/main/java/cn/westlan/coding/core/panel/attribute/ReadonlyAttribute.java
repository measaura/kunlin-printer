package cn.westlan.coding.core.panel.attribute;

public class ReadonlyAttribute<T> implements Attribute<T>{
    private final Integer name;
    private final T value;

    public ReadonlyAttribute(Integer name, T value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int name() {
        return name;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public boolean set(T t) {
        return false;
    }

    @Override
    public boolean readonly() {
        return true;
    }
}
