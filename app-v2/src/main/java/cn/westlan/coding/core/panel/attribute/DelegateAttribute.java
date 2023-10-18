package cn.westlan.coding.core.panel.attribute;

public class DelegateAttribute<T> implements Attribute<T>{

    private final Integer name;
    private final Property<T> property;

    public DelegateAttribute(Integer name, Property<T> property) {
        this.name = name;
        this.property = property;
    }

    @Override
    public int name() {
        return name;
    }

    @Override
    public T get() {
        return property.get();
    }

    @Override
    public boolean set(T t) {
        return property.set(t);
    }

    @Override
    public boolean readonly() {
        return false;
    }
}
