package cn.westlan.coding.core.panel.attribute;

import cn.westlan.coding.R;

import java.util.function.Predicate;

public interface Attribute<T> {

    int name();

    T get();

    default int hint(){return 0;}

    default boolean isLegal(T t){return true;}

    default int errorMessage(){return R.string.illegal_input;}

    boolean set(T t);

    boolean readonly();
}
