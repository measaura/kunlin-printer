package cn.westlan.coding.control.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectItem<T> {
    private final String showName;
    private final T data;
    private boolean selected;
}
