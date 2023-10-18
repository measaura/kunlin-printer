package cn.westlan.coding.core.connect.protocol;

import cn.westlan.coding.core.io.Readable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplyMsg<T extends Readable>{
    private Reply<T> protocol;
    private T content;
}
