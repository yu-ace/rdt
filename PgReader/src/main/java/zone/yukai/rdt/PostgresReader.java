package zone.yukai.rdt;

import zone.yukai.rdt.common.Column;
import zone.yukai.rdt.common.ConfigItem;
import zone.yukai.rdt.common.IReader;
import zone.yukai.rdt.common.Row;

import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PostgresReader implements IReader {
    String postgresUrl;
    String postgresUsername;
    String postgresPassword;
    String tableName;
    Connection connection;
    @Override
    public void init(Map<String, Object> setting) throws SQLException {
        Iterator<String> iterator = setting.keySet().iterator();
        iterator.next();
        String key = iterator.next();
        Map<String, Object> config = (Map<String, Object>) setting.get(key);
        postgresUrl = (String) config.get("url");
        postgresUsername = (String) config.get("username");
        postgresPassword = (String) config.get("password");
        tableName = (String) config.get("tableName");
        connection = DriverManager.getConnection(postgresUrl, postgresUsername, postgresPassword);
    }

    @Override
    public void read(LinkedBlockingQueue<Row> channel, String condition, BlockingQueue<String> status) {
        try {
            String sql = "SELECT * FROM " + tableName;
            if(condition != null){
                sql = "SELECT * FROM " + tableName + " WHERE " + condition;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Row row = new Row();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for(int i = 1;i <= columnCount;i++){
                    String columnName = metaData.getColumnName(i);
                    Object value;
                    switch (metaData.getColumnTypeName(i)) {
                        case "BIGINT":
                            value = resultSet.getLong(columnName);
                            break;
                        case "VARCHAR":
                            value = resultSet.getString(columnName);
                            break;
                        case "INT":
                            value = resultSet.getInt(columnName);
                            break;
                        case "TIMESTAMP":
                            value = resultSet.getTimestamp(columnName);
                            break;
                        default:
                            value = resultSet.getObject(columnName);
                    }
                    Column column = new Column(columnName, value, metaData.getColumnTypeName(i));
                    row.addColumn(column);
                }
                channel.put(row);
                System.out.println("读到一行数据");
            }
            status.put("READ_OVER");
            connection.close();
            preparedStatement.close();
            resultSet.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("finish");
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
