package zone.yukai.rdt.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public interface IReader {
    void init(Map<String,Object> setting);
    void read(LinkedBlockingQueue channel);
    List<ConfigItem> getConfigItems();
}
