package httpserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class WorkerThread extends Thread {
    private static  int entryCount = 0;
    private final   int threadId;
    public  final   int mtu = 2048;
    public  final   Socket socket;

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
        byte[] readBuffer = new byte[this.mtu];
        try {
            //noinspection InfiniteLoopStatement
            while(true) {
                InputStream in = socket.getInputStream();
                int len = in.read(readBuffer);
                String msg = new String(readBuffer, 0, len);
                System.out.println(msg);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(("ACK: " + msg).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        try {
            this.socket.close();
        } catch (IOException ignored) { }
        super.interrupt();
    }
}
