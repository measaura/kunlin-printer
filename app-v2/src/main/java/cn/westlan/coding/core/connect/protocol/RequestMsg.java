package cn.westlan.coding.core.connect.protocol;

import cn.westlan.coding.core.io.Writable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestMsg<T extends Writable> {
    private Request<T> protocol;
    private T content;
}
