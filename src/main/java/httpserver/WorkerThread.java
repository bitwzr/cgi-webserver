package httpserver;

import java.io.IOException;
import java.net.Socket;

public class WorkerThread extends Thread {
    private static int entryCount = 0;
    private final int threadId;
    public final Socket socket;

    public WorkerThread(Socket socket) {
        entryCount += 1;
        threadId = entryCount;
        this.socket = socket;
    }

    public int getThreadId() {
        return this.threadId;
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void interrupt() {
        try {
            this.socket.close();
        } catch (IOException ignored) { }
        super.interrupt();
    }
}
