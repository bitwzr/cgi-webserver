import config.HttpServerConfig;
import httpserver.HttpServer;
import httpserver.ServerLog;
import org.aeonbits.owner.ConfigFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpServer server = null;
        HttpServerConfig httpServerConfig = ConfigFactory.create(HttpServerConfig.class);
        ServerLog.initServerLog();
        try {
            server = new HttpServer(httpServerConfig.port(), httpServerConfig.maxThreads());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server running on port " + httpServerConfig.port() + ", maxThreads = " + httpServerConfig.maxThreads());
        server.start();
    }
}
