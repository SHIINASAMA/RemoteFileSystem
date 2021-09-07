package pers.kaoru.rfs.server;

import java.io.*;
import java.util.Properties;

public class Main {

    // 配置文件路径
    private static final String ARGS_CONFIG_PATH = "--config-path";

    // 启动模式
    private static final String ARGS_LAUNCH_MODE = "--launch-mode";
    private static final String ARGS_LAUNCH_MODE_TEST = "test";
    private static final String ARGS_LAUNCH_MODE_CONSOLE = "console";
    private static final String ARGS_LAUNCH_MODE_SWING = "swing";

    public static void main(String[] args) {

        String path = "./config.json";
        String mode = "test";

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
                System.out.println("Illegal argument");
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
                        System.out.println("unknown argument, near by: " + args[i]);
                        return;
                }
            }
        }

        System.out.println("pickup option: " + ARGS_CONFIG_PATH + " " + path);
        System.out.println("pickup option: " + ARGS_LAUNCH_MODE + " " + mode);

        Config config;
        try {
            config = Config.ConfigBuild(path);
        } catch (IOException exception) {
            System.out.println("error: " + exception.getMessage());
            return;
        }

        switch (mode) {
            case ARGS_LAUNCH_MODE_TEST:
                System.out.println("################ Test Mode ################");
                System.out.println("host:           " + config.getHost());
                System.out.println("port:           " + config.getPort());
                System.out.println("word directory: " + config.getWorkDirectory());
                for (var user : config.getUsers()) {
                    System.out.println(user);
                }
                break;
            case ARGS_LAUNCH_MODE_CONSOLE:
                break;
            case ARGS_LAUNCH_MODE_SWING:
                break;
            default:
                System.out.println("unknown mode: " + mode);
                break;
        }
    }

    private static void help() {
        InputStream inputStream = Main.class.getResourceAsStream("/help.txt");
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
        InputStream inputStream = Main.class.getResourceAsStream("/config.properties");
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
        System.out.println(by + "build this package on " + date);
    }
}
