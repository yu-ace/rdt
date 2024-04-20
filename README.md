# 简介
### 个人博客：http://www.yukai.zone
rdt是一个数据库迁移的工具，旨在实现将数据从任意一款数据库读取并写入到任意一款数据库的功能。

# 模块分析
项目共有7个模块，启动项目需要有4个模块才能运行。其中common模块和kernel模块是必要模块，
| 模块 | 介绍 |
| ---- | ----|
| common | 通用类，提供其他模块所需要的封装类和接口 |
| kernel | 核心功能，实现数据库数据的写入和读取 |
| MySQLReader | MySQL数据库的读取功能 |
| MySQLWriter | MySQL数据库的写入功能 |
| PostgresReader | Postgres数据库的读取功能 |
| PostgresWriter | Postgres数据库的写入功能 |
| CSVWriter | CSV文件写入功能 |

# 使用方法
在启动命令时，需要配置对应的参数，例如现在有一个rdt.jar的jar包需要启动，命令如下
```
java -jar rdt.jar -f D:\jar -r zone.yukai.rdt.PostgresReader -w zone.yukai.rdt.MySQLWriter -c D:\readerConfig
```
在启动jar包时，如上四个参数是必须的，否则程序无法启动。
## 参数配置
| 参数 | 是否需要参数 | 解释 |
| --- | --- | --- |
| -h / --help | false | 打印帮助 |
| -f / --filePath | true | jar包的文件路径，参考格式： D:\jar |
| -r / --readerPackageName | true | 数据库读取的包名，默认为:zone.yukai.rdt.PostgresReader |
| -w / --writerPackageName | true | 数据库写入的包名，默认为:zone.yukai.rdt.MySQLWriter | 
| -c / --configPath | true | 数据库配置文件路径，参考格式： D:\readerConfig | 

## 配置文件格式
> MySQLDatabase:  
> > url : jdbc:mysql://xxx/xxx  
> > username : yourUsername  
> > password : yourPassword  
> > tableName: yourTableName  

> PostgresDatabase:  
> > url : jdbc:postgresql://xxx/xxx  
> > username : yourUsername  
> > password : yourPassword  
> > tableName: yourTableName

