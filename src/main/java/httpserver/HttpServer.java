package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;

public class HttpServer {
    private int port;
    private int maxThreads;
    private ServerSocket serverSocket;
    private ArrayDeque<WorkerThread> threads = new ArrayDeque<>();

    /**
     * Create a new HttpServer instance
     *
     * @param port web port the server listen on, integer
     * @throws IOException may throw IOException due to socket failures
     */
    public HttpServer(int port) throws IOException {
        this(port, 2);
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
        this.maxThreads = maxThreads;
        this.serverSocket = new ServerSocket(this.port);
    }

    /**
     * Start listening on server port
     */
    public void start() {
        try{
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                // on listening
                Socket socket = serverSocket.accept();

                WorkerThread workerThread = new WorkerThread(socket);
                insertNewThread(workerThread);      // insert the current thread into threadPool
                workerThread.start();               // start handling new thread
                System.out.println("accepted new connection: " + socket.getInetAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ignored) { }
        }
    }

    /**
     * insert new thread into the thread-pool, and remove the first entry
     * if thread number exceeds the maxThread limit
     * @param workerThread thread to insert
     */
    private void insertNewThread(WorkerThread workerThread) {
        this.threads.add(workerThread);
        if (this.threads.size() > this.maxThreads) {
            if (this.threads.peekFirst() != null && this.threads.peekFirst().isAlive()) {
                this.threads.peekFirst().interrupt();
            }
            this.threads.removeFirst();
        }
    }
}
