package cn.westlan.coding.core.panel.attribute;

import java.util.function.Predicate;

public class BaseAttribute<T> implements Attribute<T> {
    private final Integer name;
    private T value;
    private final Predicate<T> predicate;

    public BaseAttribute(Integer name, T value) {
        this(name, value, null, false);
    }

    public BaseAttribute(Integer name, T value, Predicate<T> predicate) {
        this(name, value, predicate, false);
    }

    public BaseAttribute(Integer name, T value, Predicate<T> predicate, boolean enable) {
        this.name = name;
        this.value = value;
        if(predicate != null){
            if(enable){
                predicate.test(value);
            }
        }else {
            predicate = t -> false;
        }
        this.predicate = predicate;
    }

    @Override
    public int name() {
        return name;
    }

    @Override
    public T get(){
        return value;
    }

    @Override
    public boolean readonly() {
        return false;
    }

    public boolean set(T t){
        if(t.equals(value))return false;
        this.value = t;
        return predicate.test(t);
    }
}
