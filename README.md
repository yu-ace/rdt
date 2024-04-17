# 简介
RDT是一个数据库迁移的工具，旨在实现将数据从任意一款数据库读取并写入到任意一款数据库的功能。

# 模块分析
| 模块 | 介绍 |
| ---- | ----|
| common | 通用类，提供其他模块所需要的封装类和接口 |
| kernel | 核心功能，实现数据库数据的写入和读取 |
| MySQLReader | MySQL数据库的读取功能 |
| MySQLWriter | MySQL数据库的写入功能 |
| Postgres | Postgres数据库的读取功能 |
| Postgres | Postgres数据库的写入功能 |

# 使用方法
## 参数配置
| 参数 | 是否需要参数 | 解释 |
| --- | --- | --- |
| -h / --help | false | 打印帮助 |
| -f / --filePath | true | jar包的文件路径，参考格式： D:\jar |
| -r / --readerPackageName | true | 数据库读取的包名，默认为:zone.yukai.rapidDataTransfer.PostgresReader |
| -w / --writerPackageName | true | 数据库写入的包名，默认为:zone.yukai.rapidDataTransfer.MySQLWriter | 
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

