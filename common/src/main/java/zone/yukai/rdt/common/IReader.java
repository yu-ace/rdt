package zone.yukai.rdt.common;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface IReader {
    void init(Map<String,Object> setting) throws SQLException;
    void read(LinkedBlockingQueue<Row> channel, String condition, BlockingQueue<String> status);
    List<ConfigItem> getConfigItems();
}
