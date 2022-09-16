package httpserver;

import cgi.CGIData;
import cgi.CGIServer;
import config.HttpServerConfig;
import org.aeonbits.owner.ConfigFactory;

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

    private StringBuilder http_header;

    private AnalysisHttpHeader ahh;

    private int data_len;

    private byte[] http_data;

    private OutputStream sos;

    public HttpServerConfig httpServerConfig;

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
            httpServerConfig = ConfigFactory.create(HttpServerConfig.class);

            while(true) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())); //获取输入流
                http_header = new StringBuilder(); //HTTP头部
                String line;
                boolean is_header = true; //头部标识
                line = br.readLine(); //按行读入
                //忽略前导空行
                while(line!=null && line.equals("")) //前导空行
                    line = br.readLine();
                //读HTTP头部
                while(line!=null){
                    if(line.equals("")){ //空行作为结束标识
                        is_header = false; //HTTP头部结束
                        break;
                    }
                    if(is_header)
                        http_header.append(line+"\r\n"); //加入到HTTP头部
                    line = br.readLine();
                }
                if(http_header.isEmpty()) continue;
                ahh = new AnalysisHttpHeader(http_header); //交给HTTP头部分析模块分析
                String s_len = ahh.map.get("Content-Length"); //获取data部分长度
                if(s_len == null)
                    data_len = 0; //长度为0
                else{
                    data_len = Integer.parseInt(s_len);
                }
                //读HTTP Data字段
                char[] data_temp = new char[data_len*2];
                if(data_len > 0){ //长度非0则读取
                    br.read(data_temp);
                    http_data = new String(data_temp).getBytes(); //以字节的方式读取
                }
                else{
                    http_data = new byte[0];
                }
                sos = socket.getOutputStream(); //获取输出流

                System.out.println(http_header);
                System.out.println(http_data.toString());

//                InputStream in = socket.getInputStream();
//                int len = in.read(readBuffer);
//                if(len <= 0) continue;
//                String msg = new String(readBuffer, 0, len + 1);
//                System.out.println(msg);
////                AnalysisHttpHeader ahh = new AnalysisHttpHeader(msg);
//                OutputStream outputStream = socket.getOutputStream();
//                sos.write(("ACK: " + http_header).getBytes(StandardCharsets.UTF_8));

                if(ahh.method.equals("GET")) {
                    String htmlURL = ahh.URL;
                    if(htmlURL.equals("/")) {
                        htmlURL += "index.html";
                    }
                    htmlURL = httpServerConfig.htmlPath() + htmlURL;
                    File f = new File(htmlURL);
                    HttpResponse hr;
                    if(!f.exists()) {
                        hr = new HttpResponse("Not Found"); //404错误
                        f = new File(httpServerConfig.notFoundPath());
                    }
                    else {
                        hr = new HttpResponse("OK"); //200 OK
                    }
                    int numOfBytes = (int) f.length(); //文件长度
                    hr.setContentType("text/html", numOfBytes); //设置响应头部的文件长度
                    sos.write(hr.getResponse().getBytes()); //生成HTTP响应
                    FileInputStream fileInput = new FileInputStream(f);//创建文件输入流
                    byte[] fileInputBytes = new byte[numOfBytes];
                    fileInput.read(fileInputBytes);//将文件数据保存到字节数组中
                    sos.write(fileInputBytes); //输出文件
                    sos.flush();
                }
                else if(ahh.method.equals("HEAD")) {

                }
                else { // ahh.method.equals("POST")
                    CGIData cgi_data = CGIServer.cgi(ahh, socket, http_data); //交给CGI解析模块处理
                    if(cgi_data.data != null && cgi_data.head != null){
                        sos.write("HTTP/1.1 200 OK".getBytes());
                        sos.write("\r\n".getBytes());
                        sos.write(("Server: " + httpServerConfig.serverName() + "\r\n").getBytes());
                        sos.write(cgi_data.head.getBytes());
                        sos.write("\r\n".getBytes());
                        sos.write(("Connection: Close\r\n").getBytes());
                        sos.write("Content-Type: ".getBytes());
                        sos.write(String.valueOf(cgi_data.data.length).getBytes()); //获取CGI解析结果长度
                        sos.write("\r\n\r\n".getBytes());
                        sos.write(cgi_data.data); //输出CGI部分
                        sos.flush();
                    }
                }
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
