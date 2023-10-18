package cn.westlan.coding.core.panel.select;

import java.util.*;

public class SelectedSet<T extends Selectable> implements Iterable<T>{
    private boolean multi = false;
    private final LinkedList<T> list = new LinkedList<>();
    private final List<SelectListener<T>> listeners = new ArrayList<>();

    public SelectedSet() {
    }

    @SafeVarargs
    public SelectedSet(SelectListener<T>... listener) {
        this.listeners.addAll(Arrays.asList(listener));
    }

    public void addListener(SelectListener<T> listener){
        if(listener == null)return;
        listener.onModeChange(multi);
        listener.onSelectedChange(list);
        this.listeners.add(listener);
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        if(this.multi == multi)return;
        if(this.multi){
            Iterator<T> iterator = list.iterator();
            if(iterator.hasNext()){
                Selectable first = iterator.next();
                first.setSelected(Selectable.Status.Single);
                while (iterator.hasNext()){
                    Selectable selectable = iterator.next();
                    selectable.setSelected(Selectable.Status.No);
                    iterator.remove();
                }
            }
            triggerListener();
        }else {
            Iterator<T> iterator = list.iterator();
            if(iterator.hasNext()){
                Selectable selectable = iterator.next();
                selectable.setSelected(Selectable.Status.Batch);
            }
            triggerListener();
        }
        this.multi = multi;
        for (SelectListener<T> listener : listeners){
            listener.onModeChange(this.multi);
        }
    }

    public T getFirst() {
        if(list.size() == 0)return null;
        return list.getFirst();
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(T o) {
        return list.contains(o);
    }

    public boolean add(T t) {
        if(list.contains(t)){
            t.setSelected(Selectable.Status.No);
            list.remove(t);
            triggerListener();
            return false;
        }else{
            if(!multi){
                removeAll();
            }
            t.setSelected(multi? Selectable.Status.Batch: Selectable.Status.Single);
            list.add(t);
            triggerListener();
            return true;
        }
    }

    public boolean remove(T t) {
        if(list.remove(t)){
            t.setSelected(Selectable.Status.No);
            triggerListener();
            return true;
        }
        return false;
    }

    private void removeAll(){
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()){
            Selectable selectable = iterator.next();
            selectable.setSelected(Selectable.Status.No);
            iterator.remove();
        }
    }

    public void clear() {
        removeAll();
        triggerListener();
    }

    private void triggerListener(){
        for (SelectListener<T> listener : listeners){
            listener.onSelectedChange(list);
        }
    }
}
