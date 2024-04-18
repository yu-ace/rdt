package zone.yukai.rdt;

import zone.yukai.rdt.common.Column;
import zone.yukai.rdt.common.ConfigItem;
import zone.yukai.rdt.common.IWriter;
import zone.yukai.rdt.common.Row;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MySQLWriter implements IWriter {
    String mySQLUrl;
    String mySQLUsername;
    String mySQLPassword;
    String tableName;
    Connection connection;
    @Override
    public void init(Map<String, Object> setting) throws SQLException {
        String key = setting.keySet().iterator().next();
        Map<String, Object> config = (Map<String, Object>) setting.get(key);
        mySQLUrl = (String) config.get("url");
        mySQLUsername = (String) config.get("username");
        mySQLPassword = (String) config.get("password");
        tableName = (String) config.get("tableName");
        connection = DriverManager.getConnection(mySQLUrl, mySQLUsername, mySQLPassword);
    }

    @Override
    public void write(LinkedBlockingQueue<Row> channel, BlockingQueue<String> status) {
        while (true){
            try {
                Row row = channel.take();
                System.out.println("收到一行写入任务");
                List<Column> columnList = row.getRowList();
                StringBuilder columnName = new StringBuilder();
                StringBuilder columnValue = new StringBuilder();
                for(int i = 0;i < columnList.size();i++){
                    columnName.append(columnList.get(i).getName());

                    Object value = columnList.get(i).getValue();

                    String dataType = columnList.get(i).getDataType();
                    switch (dataType) {
                        case "BIGINT":
                            columnValue.append(value);
                            break;
                        case "VARCHAR":
                            columnValue.append("\""+value+"\"");
                            break;
                        case "varchar":
                            columnValue.append("\""+value+"\"");
                            break;
                        case "INT":
                            columnValue.append(value);
                            break;
                        case "TIMESTAMP":
                            columnValue.append("\""+value+"\"");
                            break;
                        default:
                            columnValue.append(columnList.get(i).getValue());
                    }
                    if(i < columnList.size()-1){
                        columnName.append(',');
                        columnValue.append(',');
                    }
                }
                String sql = "INSERT INTO " + tableName + "("+columnName.toString()+")values("+columnValue.toString()+");";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
                preparedStatement.close();
                if(channel.size() == 0){
                    if("READER_OVER".equals(status.take())){
                        connection.close();
                        break;
                    }
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<ConfigItem> getConfigItems() {
        List<ConfigItem> items = new ArrayList<>();
        items.add(new ConfigItem("MYSQL.URL","string","url"));
        items.add(new ConfigItem("MYSQL.USERNAME","string","用户名"));
        items.add(new ConfigItem("MYSQL.PASSWORD","string","密码"));
        items.add(new ConfigItem("MYSQL.TABLE_NAME","String","表名"));
        items.add(new ConfigItem("MYSQL.WHERE","String","查询语句"));
        return items;
    }
}
