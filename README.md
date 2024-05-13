# 简介
### 个人博客：http://www.yukai.zone
rdt是一个数据库迁移的工具，旨在实现将数据从任意一款数据库读取并写入到任意一款数据库的功能。

## 功能简介
该工具主要功能是由any to any、反压、多线程并发写入、零拷贝和断点续传，这五个功能组成的。

### any to any
该功能是指，将数据从任意一款数据库中读取，然后写入到其他数据库或者文件中，比如MySQL、Postgres、CSV等。

### 反压
反压是为了增加系统的稳定性。在数据的写入过程中，可能会涉及到数据库的事务问题，这样就会造成生产者的效率远远大于消费者的情况出现。为了应对这种情况的出现，采用了反压机制，通过降低生产者的速度，避免数据的丢失和系统崩溃的情况出现。

### 多线程并发写入
多线程并发写入的功能，主要是为了实现将一份数据同时向多个数据库写入的需求。

### 零拷贝
零拷贝是为了尽可能地减少数据的拷贝，提升整个系统的传输效率和性能。

### 断点续传
为了避免传输过程中，由于网络故障或者其他原因导致传输失败，使得文件不得不重新传输的情况出现，增加了断点续传得功能。通过这个功能，可以实现在程序恢复以后，可以继续传输的能力。

# 模块分析
项目共有7个模块，启动项目需要有4个模块才能运行。其中common模块和kernel模块是必要模块，另外还需要一个读模块和一个写模块，具体使用哪一个模块没有限制。
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
配置文件使用yml格式，写法需要按照如下格式，其中数据库配置固定，第一个为MySQL数据库，第二个为Postgres数据库，第三个为csv文件的保存路径。
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

> CSV:
> > path : D:\xxx\output.csv
