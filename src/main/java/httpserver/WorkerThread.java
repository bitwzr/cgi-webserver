package httpserver;

import cgi.CGIData;
import cgi.CGIServer;
import config.HttpServerConfig;
import org.aeonbits.owner.ConfigFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class WorkerThread extends Thread {
    private static  int entryCount = 0;
    private final   int threadId;
    public  final   int mtu = 2048;
    public  final   Socket socket;

    public HttpServerConfig httpServerConfig;

    public WorkerThread(Socket socket) {
        entryCount += 1;
        threadId = entryCount;
        this.socket = socket;
        synchronized (ServerLog.syn) {
            ServerLog.log("Connection from " + socket.getInetAddress().getHostAddress() + " established.");
        }
    }

    public int getThreadId() {
        return this.threadId;
    }

    @Override
    public void run() {
        byte[] readBuffer = new byte[this.mtu];
        try {
            httpServerConfig = ConfigFactory.create(HttpServerConfig.class);

            //noinspection InfiniteLoopStatement
            while(true) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())); //获取输入流
                StringBuilder httpHeader = new StringBuilder(); //HTTP头部
                String line;
                line = br.readLine(); //按行读入
                //忽略前导空行
                while(line!=null && line.equals("")) //前导空行
                    line = br.readLine();
                //读HTTP头部
                while(line!=null){
                    if(line.equals("")){ //空行作为结束标识
                        break;
                    }
                    httpHeader.append(line).append("\r\n"); //加入到HTTP头部
                    line = br.readLine();
                }
                if(httpHeader.isEmpty()) continue;
                AnalysisHttpHeader analysisHttpHeader = new AnalysisHttpHeader(httpHeader); //交给HTTP头部分析模块分析
                String contentLengthSection = analysisHttpHeader.map.get("Content-Length"); //获取data部分长度
                int dataLen = contentLengthSection == null ? 0 : Integer.parseInt(contentLengthSection);
                //读HTTP Data字段
                char[] dataTemp = new char[dataLen*2];
                byte[] httpData;
                if(dataLen > 0){ //长度非0则读取
                    int realLen = br.read(dataTemp);
                    httpData = new String(dataTemp).getBytes(); //以字节的方式读取
                    if (realLen != dataLen) {
                        System.out.println("Data read from HTTP packet does not match the length specified in data length fields.");
                    }
                    synchronized (ServerLog.syn) {
                        ServerLog.log("Data read from " + socket.getInetAddress().getHostAddress() + " does not match the length specified in data length fields.");
                    }
                }
                else{
                    httpData = new byte[0];
                }
                OutputStream outputStream = socket.getOutputStream(); //获取输出流

                System.out.println(httpHeader);
                System.out.println(Arrays.toString(httpData));

                HttpResponse httpResponse;
                if(analysisHttpHeader.method.equals("GET")) {
                    String htmlURL = analysisHttpHeader.URL;
                    if(htmlURL.equals("/")) {
                        htmlURL += "index.html";
                    }
                    htmlURL = httpServerConfig.htmlPath() + htmlURL;
                    synchronized (ServerLog.syn) {
                        ServerLog.log("Receive a GET request from " + socket.getInetAddress().getHostAddress() + "  :   " + htmlURL);
                    }
                    File file = new File(htmlURL);
                    if(!file.exists()) {
                        httpResponse = new HttpResponse("Not Found"); //404错误
                        file = new File(httpServerConfig.notFoundPath());
                        synchronized (ServerLog.syn) {
                            ServerLog.log("Send NotFound to " + socket.getInetAddress().getHostAddress());
                        }
                    }
                    else {
                        httpResponse = new HttpResponse("OK"); //200 OK
                        synchronized (ServerLog.syn) {
                            ServerLog.log("Send OK to " + socket.getInetAddress().getHostAddress());
                        }
                    }
                    int numOfBytes = (int) file.length(); //文件长度
                    httpResponse.setContentType("text/html", numOfBytes); //设置响应头部的文件长度
                    outputStream.write(httpResponse.getResponse().getBytes()); //生成HTTP响应
                    FileInputStream fileInput = new FileInputStream(file);//创建文件输入流
                    byte[] fileInputBytes = new byte[numOfBytes];
                    fileInput.read(fileInputBytes);//将文件数据保存到字节数组中
                    outputStream.write(fileInputBytes); //输出文件
                    outputStream.flush();
                }
                else if(analysisHttpHeader.method.equals("HEAD")) {
                    synchronized (ServerLog.syn) {
                        ServerLog.log("Receive a HEAD request from " + socket.getInetAddress().getHostAddress());
                    }
                    httpResponse = new HttpResponse("OK");
                    outputStream.write(httpResponse.getResponse().getBytes()); //生成HTTP响应
                    synchronized (ServerLog.syn) {
                        ServerLog.log("Send OK to " + socket.getInetAddress().getHostAddress());
                    }
                }
                else { // ahh.method.equals("POST")
                    synchronized (ServerLog.syn) {
                        ServerLog.log("Receive a POST request from " + socket.getInetAddress().getHostAddress() + " with HTTP DATA.");
                    }
                    CGIData cgi_data = CGIServer.cgi(analysisHttpHeader, socket, httpData); //交给CGI解析模块处理
                    if(cgi_data.data != null && cgi_data.head != null){
                        outputStream.write("HTTP/1.1 200 OK".getBytes());
                        outputStream.write("\r\n".getBytes());
                        outputStream.write(("Server: " + httpServerConfig.serverName() + "\r\n").getBytes());
                        outputStream.write(cgi_data.head.getBytes());
                        outputStream.write("\r\n".getBytes());
                        outputStream.write(("Connection: Close\r\n").getBytes());
                        outputStream.write("Content-Type: ".getBytes());
                        outputStream.write(String.valueOf(cgi_data.data.length).getBytes()); //获取CGI解析结果长度
                        outputStream.write("\r\n\r\n".getBytes());
                        outputStream.write(cgi_data.data); //输出CGI部分
                        outputStream.flush();
                        synchronized (ServerLog.syn) {
                            ServerLog.log("Send OK to " + socket.getInetAddress().getHostAddress());
                        }
                    }
                }
            }
        } catch (IOException ignored) {
            synchronized (ServerLog.syn) {
                ServerLog.error("Handle request from " + socket.getInetAddress().getHostAddress() + " and get IOException.");
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            this.socket.close();
            synchronized (ServerLog.syn) {
                ServerLog.log("Connection from " + socket.getInetAddress().getHostAddress() + " closed.");
            }
        } catch (IOException ignored) {
            synchronized (ServerLog.syn) {
                ServerLog.error("Closing socket with " + socket.getInetAddress().getHostAddress() + " get IOException.");
            }
        }
        super.interrupt();
    }
}
