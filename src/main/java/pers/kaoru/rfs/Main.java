package pers.kaoru.rfs;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import pers.kaoru.rfs.server.Config;
import pers.kaoru.rfs.server.console.Console;

import java.io.*;
import java.util.Properties;

public class Main {

    public static Logger log;

    // 配置文件路径
    private static final String ARGS_CONFIG_PATH = "--config-path";

    // 启动模式
    private static final String ARGS_LAUNCH_MODE = "--launch-mode";
    private static final String ARGS_LAUNCH_MODE_CLIENT = "client";
    private static final String ARGS_LAUNCH_MODE_TEST = "test";
    private static final String ARGS_LAUNCH_MODE_SERVER = "server";

    public static void main(String[] args) {

        log = (Logger) LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
        log.setLevel(Level.DEBUG);

        String path = "./server.json";
        String mode = ARGS_LAUNCH_MODE_SERVER;

        if (args.length > 0) {
            String baseArg = args[0].toUpperCase();
            if (baseArg.contains("HELP")) {
                help();
                return;
            } else if (baseArg.contains("VERSION")) {
                version();
                return;
            }

            if (args.length % 2 == 1) {
                log.warn("Illegal argument");
                return;
            }

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case ARGS_CONFIG_PATH:
                        path = args[++i];
                        break;
                    case ARGS_LAUNCH_MODE:
                        mode = args[++i];
                        break;
                    default:
                        log.warn("unknown argument, near by: " + args[i]);
                        return;
                }
            }
        }

        switch (mode) {
            case ARGS_LAUNCH_MODE_TEST: {
                System.out.println("################ Test Mode ################");
                Config config;
                try {
                    config = Config.ConfigBuilder(path);
                    File workdir = new File(config.getWorkDirectory());
                    if(!workdir.exists() || !workdir.isDirectory()){
                        throw new FileNotFoundException("no directory named \"" +  config.getWorkDirectory() + "\"");
                    }
                } catch (IOException exception) {
                    log.error(exception.getMessage());
                    return;
                }
                printConfig(config);
                for (var user : config.getUsers()) {
                    System.out.println(user);
                }
                break;
            }
            case ARGS_LAUNCH_MODE_CLIENT: {
                System.out.println("################  CLIENT   ################");
                try {
                    ImplExecutable client = (ImplExecutable) Class.forName("pers.kaoru.rfs.client.Client")
                            .getDeclaredConstructor()
                            .newInstance();
                    client.exec();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                break;
            }
            case ARGS_LAUNCH_MODE_SERVER:
                System.out.println("################  SERVER   ################");
                try {
                    Config config = Config.ConfigBuilder(path);
                    File workdir = new File(config.getWorkDirectory());
                    if(!workdir.exists() || !workdir.isDirectory()){
                        throw new FileNotFoundException("no directory named \"" +  config.getWorkDirectory() + "\"");
                    }
                    printConfig(config);
                    ImplExecutable console = new Console(config);
                    console.exec();
                } catch (IOException exception) {
                    log.info("fail to start service, " + exception.getMessage());
                }
                break;
            default:
                log.warn("unknown mode: " + mode);
                break;
        }
    }

    private static void help() {
        InputStream inputStream = Main.class.getResourceAsStream("/res/help.txt");
        assert inputStream != null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int length;
        byte[] bytes = new byte[1024];
        try {
            while ((length = inputStream.read(bytes, 0, 1024)) > 0) {
                outputStream.write(bytes, 0, length);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        System.out.println(outputStream);
    }

    private static void version() {
        InputStream inputStream = Main.class.getResourceAsStream("/res/config.properties");
        assert inputStream != null;
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        String major = properties.getProperty("Major");
        String minor = properties.getProperty("Minor");
        String rev = properties.getProperty("Revision");
        String by = properties.getProperty("BuildBy");
        String date = properties.getProperty("BuildDate");

        System.out.println("Version " + major + "." + minor + "[." + rev + "]");
        System.out.println(by + " build this package on " + date);
    }

    private static void printConfig(Config config){
        System.out.println("host:           " + config.getHost());
        System.out.println("port:           " + config.getPort());
        System.out.println("backlog:        " + config.getBacklog());
        System.out.println("word directory: " + config.getWorkDirectory());
        System.out.println("threads:        " + config.getThreads());
    }
}
