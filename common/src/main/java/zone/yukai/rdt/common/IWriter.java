package zone.yukai.rdt.common;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public interface IWriter {
    void init(Map<String,Object> setting) throws SQLException;
    void write(LinkedBlockingQueue<Row> channel, BlockingQueue<String> status) throws IOException, InterruptedException, Exception;
    List<ConfigItem> getConfigItems();
}
