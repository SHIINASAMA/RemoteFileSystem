package pers.kaoru.rfs.server.console;

import org.apache.logging.log4j.core.Logger;
import pers.kaoru.rfs.core.web.ImplHandler;
import pers.kaoru.rfs.core.web.MainHandler;
import pers.kaoru.rfs.ImplExecutable;
import pers.kaoru.rfs.Main;
import pers.kaoru.rfs.server.Config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Console implements ImplExecutable {

    private final ImplHandler handler;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Logger log;

    public Console(Config config) throws IOException {
        log = Main.log;
        log.info("service starting");
        serverSocket = new ServerSocket(config.getPort(), config.getBacklog(), InetAddress.getByName(config.getHost()));
        executorService = Executors.newFixedThreadPool(config.getThreads());
        handler = MainHandler.HandlerBuild(config.getWorkDirectory(), config.getUsers(), log);
        log.info("service start on " + config.getHost() + ":" + config.getPort());
    }

    @Override
    public void exec() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                executorService.submit(() -> handler.handle(client));
            } catch (IOException exception) {
                exception.printStackTrace();
                log.warn(exception.getMessage());
            }
        }
    }
}
