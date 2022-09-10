package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;

public class HttpServer {
    private int port;
    private int maxThreads;
    private ServerSocket serverSocket;
    private ArrayDeque<WorkerThread> threads;

    /**
     * Create a new HttpServer instance
     *
     * @param port web port the server listen on, integer
     * @throws IOException may throw IOException due to socket failures
     */
    public HttpServer(int port) throws IOException {
        this(port, 100);
    }

    /**
     * Create a new HttpServer instance
     *
     * @param port web port the server listen on, integer
     * @param maxThreads maximum number of working threads on the server,
                        will kill previous threads on exceeding the limit
     * @throws IOException may throw IOException due to socket failures
     */
    public HttpServer(int port, int maxThreads) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
    }

    /**
     * Start listening on server port
     */
    public void start() {
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            // on listening
            try {
                Socket socket = serverSocket.accept();

                WorkerThread workerThread = new WorkerThread(socket);
                insertNewThread(workerThread);      // insert the current thread into threadPool
                workerThread.start();               // start handling new thread
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException ignored) { }
            }
        }
    }

    private void insertNewThread(WorkerThread workerThread) {

    }
}
