package cn.westlan.coding.core.panel.attribute;

import java.util.*;
import java.util.function.Predicate;

public class StepAttribute<T> extends BaseAttribute<T>{
    private int index;
    private final int size;
    private final T[] array;

    public static  StepAttribute<Double> generate(Integer name, double val, double min, double max, double stride, Predicate<Double> predicate) {
        List<Double> list = new ArrayList<>();
        boolean find = false;
        int index = 0;
        for(double i = min; i <= max; i+=stride){
            if(!find){
                if(val<=i){
                    find = true;
                }else{
                    index++;
                }
            }
            list.add(i);
        }
        return new StepAttribute<>(name, list.toArray(new Double[0]), index, predicate);
    }

    public StepAttribute(Integer name, T[] array) {
        this(name, array, 0, null, false);
    }

    public StepAttribute(Integer name, T[] array, int index, Predicate<T> predicate) {
        this(name, array, index, predicate, false);
    }

    public StepAttribute(Integer name, T[] array, int index, Predicate<T> predicate, boolean enable) {
        super(name, array[index], predicate, enable);
        this.array = array;
        this.index = index;
        this.size = array.length;
    }

    public int status(){
        if(index == 0)return -1;
        if(index == size-1)return 1;
        return 0;
    }

    public boolean add(){
        return set(forward());
    }

    public boolean sub(){
        return set(backward());
    }

    private T forward(){
        if(index >= size-1)return array[size-1];
        return array[++index];
    }

    private T backward(){
        if(index <= 0)return array[0];
        return array[--index];
    }

}
