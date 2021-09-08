package pers.kaoru.rfs.server.console;

import org.apache.logging.log4j.core.Logger;
import pers.kaoru.rfs.core.web.ImplHandler;
import pers.kaoru.rfs.core.web.MainHandler;
import pers.kaoru.rfs.ImplExecutable;
import pers.kaoru.rfs.Main;

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

    public Console(String host, int port, int backlog, String wordDirectory, int threads) throws IOException {
        log = Main.log;
        log.info("service starting");
        serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(host));
        executorService = Executors.newFixedThreadPool(threads);
        handler = new MainHandler(wordDirectory, log);
        log.info("service start on " + host + ":" + port);
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
