package zone.yukai.rapidDataTransfer.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public interface IWriter {
    void init(Map<String,Object> setting);
    void write(LinkedBlockingQueue<Row> channel);
    List<ConfigItem> getConfigItems();
}
