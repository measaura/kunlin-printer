package cn.westlan.coding.update;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent=true)
public class Version {
    private boolean needUpdate;
    private String version;
    private String description;
    private String url;
}
