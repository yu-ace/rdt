package zone.yukai.kernel;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;
import zone.yukai.rdt.common.IReader;
import zone.yukai.rdt.common.IWriter;
import zone.yukai.rdt.common.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class KernelApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(KernelApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 创建命令行
        CommandLine commandLine = createCommandLine(args);
        // 载入JAR
        URLClassLoader urlClassLoader = loadJar(commandLine.getOptionValue("f"));
        // 配置信息
        String readerLoadClass = commandLine.getOptionValue("r");
        String writerLoadClass = commandLine.getOptionValue("w");
        Map<String, Object> setting = config(commandLine.getOptionValue("c"));
        String condition = commandLine.getOptionValue("cd");
        // 初始化任务
        LinkedBlockingQueue<Row> channel = new LinkedBlockingQueue<>(5);
        BlockingQueue<String> status = new LinkedBlockingQueue<>(1);
        Thread readerThread = new Thread(()->{
            try {
                Class<?> aClass = urlClassLoader.loadClass(readerLoadClass);
                IReader reader = (IReader) aClass.getDeclaredConstructor().newInstance();
                reader.init(setting);
                reader.read(channel, condition,status);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread writerThread = new Thread(()->{
            try {
                Class<?> aClass1 = urlClassLoader.loadClass(writerLoadClass);
                IWriter writer = (IWriter) aClass1.getDeclaredConstructor().newInstance();
                writer.init(setting);
                writer.write(channel,status);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        //执行任务
        readerThread.setName("reader-t1");
        writerThread.setName("writer-t1");
        readerThread.start();
        writerThread.start();
        readerThread.join();
        writerThread.join();
    }

    public CommandLine createCommandLine(String... args) throws Exception{
        Options options = new Options();
        options.addOption("h", "help",false,"print help");
        options.addOption("f", "filePath",true,"the path of jar");
        options.addOption("r", "readerPackageName",true,"the name of reader Package");
        options.addOption("w", "writerPackageName",true,"the name of writer Package");
        options.addOption("c", "configPath",true,"the path of config");
        options.addOption("cd", "whereCondition",true,"the condition of sql");
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
            return commandLine;
        } catch (ParseException e) {
            throw new Exception(e.getMessage());
        }
    }

    public URLClassLoader loadJar(String originalPath) throws Exception {
        File[] originalPathList = FileUtil.ls(originalPath);
        List<URL> urlList = new ArrayList<>();
        if(originalPathList != null){
            for(File file:originalPathList){
                String path = "file:///" + originalPath.replace("\\", "/") + "/" + file.getName();
                URL url = new URL(path);
                urlList.add(url);
            }
        }else {
            throw new RuntimeException("文件夹中没有数据");
        }
        URL[] urls = urlList.toArray(new URL[0]);
        return new URLClassLoader(urls);
    }

    public Map<String, Object> config(String configLoadClassPath) throws Exception {
        File[] configLoadClassPathList = FileUtil.ls(configLoadClassPath);
        File file = configLoadClassPathList[0];
        InputStream inputStream = new FileInputStream(file);
        Yaml yaml = new Yaml();
        return yaml.load(inputStream);
    }
}
