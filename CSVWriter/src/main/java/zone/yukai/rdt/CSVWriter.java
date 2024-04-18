package zone.yukai.rdt;

import zone.yukai.rdt.common.Column;
import zone.yukai.rdt.common.ConfigItem;
import zone.yukai.rdt.common.IWriter;
import zone.yukai.rdt.common.Row;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CSVWriter implements IWriter {
    String csvPath;
    boolean isFirst = true;
    @Override
    public void init(Map<String, Object> setting) {
        Iterator<String> iterator = setting.keySet().iterator();
        iterator.next();
        iterator.next();
        String key = iterator.next();
        Map<String, Object> config = (Map<String, Object>) setting.get(key);
        csvPath = (String) config.get("path");
    }

    @Override
    public void write(LinkedBlockingQueue<Row> channel, BlockingQueue<String> status) throws Exception {
        FileWriter fileWriter = new FileWriter(csvPath);

        while (true){
            Row row = channel.take();
            System.out.println("收到一行写入任务");
            List<Column> columnList = row.getRowList();

            if(isFirst){
                for(int i = 0;i < columnList.size();i++){
                    fileWriter.append(columnList.get(i).getName());
                    if(i < columnList.size() - 1){
                        fileWriter.append(",");
                    }
                }
                fileWriter.append("\n");
                isFirst = false;
            }

            StringBuilder columnValue = new StringBuilder();
            for(int i = 0;i < columnList.size();i++){
                columnValue.append(columnList.get(i).getValue());
                if(i < columnList.size()-1){
                    columnValue.append(',');
                }
            }

            fileWriter.append(columnValue.toString());
            fileWriter.append("\n");
            fileWriter.flush();
            if(channel.size() == 0){
                if("READER_OVER".equals(status.take())){
                    fileWriter.close();
                    break;
                }
                break;
            }
        }
    }

    @Override
    public List<ConfigItem> getConfigItems() {
        return null;
    }
}
