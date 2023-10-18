package cn.westlan.coding.core.panel.attribute;

import java.util.Map;
import java.util.function.Predicate;

public class OptionsAttribute<T> extends BaseAttribute<T>{
    private final Map<String, T> options;
    private String selected;

    public OptionsAttribute(Integer name, Map<String, T> options) {
        this(name, options, options.keySet().iterator().next(), null);
    }

    public OptionsAttribute(Integer name, Map<String, T>  options, String selected, Predicate<T> predicate) {
        this(name, options, selected, predicate, false);
    }

    public OptionsAttribute(Integer name, Map<String, T>  options, String selected, Predicate<T> predicate, boolean enable) {
        super(name, options.get(selected), predicate, enable);
        this.options = options;
        this.selected = selected;
    }

    public Map<String, T> getOptions() {
        return options;
    }

    public String getSelected(){
        return selected;
    }

    public boolean setSelected(String selected) {
        T val = options.get(selected);
        if(val != null){
            this.selected = selected;
            return set(val);
        }
        return false;
    }
}
