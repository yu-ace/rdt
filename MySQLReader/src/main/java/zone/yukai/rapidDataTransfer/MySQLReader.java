package zone.yukai.rapidDataTransfer;

import zone.yukai.rapidDataTransfer.common.Column;
import zone.yukai.rapidDataTransfer.common.ConfigItem;
import zone.yukai.rapidDataTransfer.common.IReader;
import zone.yukai.rapidDataTransfer.common.Row;

import java.sql.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class MySQLReader implements IReader {

    String mySQLUrl;
    String mySQLrUsername;
    String mySQLPassword;
    String tableName;
    @Override
    public void init(Map<String, Object> setting) {
        Set<String> strings = setting.keySet();
        List<String> configList = new ArrayList<>(strings);
        mySQLUrl = (String) ((Map<String, Object>) setting.get(configList.get(0))).get("url");
        mySQLrUsername = (String) ((Map<String, Object>) setting.get(configList.get(0))).get("username");
        mySQLPassword = (String) ((Map<String, Object>) setting.get(configList.get(0))).get("password");
        tableName = (String) ((Map<String, Object>) setting.get(configList.get(0))).get("tableName");
    }

    @Override
    public void read(LinkedBlockingQueue channel) {
        try {
            String sql = "SELECT * FROM " + tableName;
            Connection connection = DriverManager.getConnection(mySQLUrl, mySQLrUsername, mySQLPassword);
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
        items.add(new ConfigItem("MYSQL.URL","string","url"));
        items.add(new ConfigItem("MYSQL.USERNAME","string","用户名"));
        items.add(new ConfigItem("MYSQL.PASSWORD","string","密码"));
        items.add(new ConfigItem("MYSQL.TABLE_NAME","String","表名"));
        return items;
    }
}
