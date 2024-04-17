package zone.yukai.RDT;

import zone.yukai.RDT.common.Column;
import zone.yukai.RDT.common.ConfigItem;
import zone.yukai.RDT.common.IReader;
import zone.yukai.RDT.common.Row;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class PostgresReader implements IReader {

    String postgresUrl;
    String postgresUsername;
    String postgresPassword;
    String tableName;
    @Override
    public void init(Map<String, Object> setting) {
        Set<String> strings = setting.keySet();
        List<String> configList = new ArrayList<>(strings);
        postgresUrl = (String) ((Map<String, Object>) setting.get(configList.get(1))).get("url");
        postgresUsername = (String) ((Map<String, Object>) setting.get(configList.get(1))).get("username");
        postgresPassword = (String) ((Map<String, Object>) setting.get(configList.get(1))).get("password");
        tableName = (String) ((Map<String, Object>) setting.get(configList.get(1))).get("tableName");
    }

    @Override
    public void read(LinkedBlockingQueue channel) {
        try {
            String sql = "SELECT * FROM " + tableName;
            Connection connection = DriverManager.getConnection(postgresUrl, postgresUsername, postgresPassword);
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
        items.add(new ConfigItem("POSTGRES.URL","string","url"));
        items.add(new ConfigItem("POSTGRES.USERNAME","string","用户名"));
        items.add(new ConfigItem("POSTGRES.PASSWORD","string","密码"));
        items.add(new ConfigItem("POSTGRES.TABLE_NAME","string","表名"));
        return items;
    }
}
