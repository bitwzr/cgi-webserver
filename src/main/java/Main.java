import httpserver.HttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpServer server = null;
        try {
            server = new HttpServer(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server running on port 8080");
        server.start();
    }
}
