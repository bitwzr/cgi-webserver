package cgi;

import config.HttpServerConfig;
import httpserver.AnalysisHttpHeader;
import org.aeonbits.owner.ConfigFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class CGIServer {

    private static String sn(String s){
        return s!=null?s:"";
    }

    private static String[] createENV(AnalysisHttpHeader ahh, Socket s){

        HttpServerConfig httpServerConfig = ConfigFactory.create(HttpServerConfig.class);

        String cgiURL = httpServerConfig.cgiPath() + ahh.URL;
        System.out.println(cgiURL);
        File f = new File(cgiURL);

        String[] env = new String[22];
        env[0] = "CONTENT_TYPE="	+	sn(ahh.map.get("Content-Type"));
        env[1] = "PATH_TRANSLATED="	+	f.getAbsolutePath();
        env[2] = "QUERY_STRING="	+	sn(ahh.get_query);
        env[3] = "REMOTE_ADDR="		+	sn(s.getInetAddress().getHostAddress());
        env[4] = "REMOTE_HOST="		+	sn(s.getInetAddress().getHostName());
        env[5] = "REQUEST_METHOD="	+	ahh.method;
        env[6] = "SCRIPT_NAME="		+	f.getName();
        env[7] = "SERVER_NAME="		+	sn(ahh.map.get("Host"));
        env[8] = "SERVER_PORT="		+	String.valueOf(httpServerConfig.port());
        env[9] = "SERVER_SOFTWARE="	+	httpServerConfig.serverName();
        env[10]= "SERVER_PROTOCOL=HTTP/1.1";
        env[11]= "GATEWAY_INTERFACE=CGI/1.1";
        env[12]= "PATH_INFO="		+	sn( f.getAbsolutePath()
                .substring(0, f.getAbsolutePath().length()
                        - f.getName().length()) );
        env[13]= "REMOTE_IDENT=";
        env[14]= "REMOTE_USER=";
        env[15]= "AUTH_TYPE=";
        env[16]= "CONTENT_LENGTH="	+	sn(ahh.map.get("Content-Length"));
        env[17]= "ACCEPT=" 			+	sn(ahh.map.get("Accept"));
        env[18]= "ACCEPT_ENCODING="	+	sn(ahh.map.get("Accept-Encoding"));
        env[19]= "ACCEPT_LANGUAGE="	+	sn(ahh.map.get("Accept-Language"));
        env[20]= "REFFERER="		+	sn(ahh.map.get("Referer"));
        env[21]= "USER_AGENT="		+	sn(ahh.map.get("User-Agent"));
        return env;
    }

    /**
     * CGI解析
     * @param ahh HTTP头部分析结果
     * @param s 套接字
     * @param http_data HTTP Data字段
     * @return CGI的数据
     */
    public static CGIData cgi(AnalysisHttpHeader ahh, Socket s, byte[] http_data) {

        HttpServerConfig httpServerConfig = ConfigFactory.create(HttpServerConfig.class);
        String cgiURL = httpServerConfig.cgiPath() + ahh.URL;
        CGIData cgi_data = new CGIData();
        if (httpServerConfig.cgiEnable()) {
            try {
                String[] env = createENV(ahh,s);
//                System.out.println(env[16]);
//                System.out.println(cgiURL);
                File f = new File(cgiURL);
                Process cgi_pro = Runtime.getRuntime().exec(
                        f.getAbsolutePath() , env , f.getParentFile());
                writePostMessage(cgi_pro.getOutputStream(),http_data);
                //用线程控制方式程序死掉
                CgiThread cgi_thread = new CgiThread(cgi_pro);
                cgi_thread.start();
                try {
                    cgi_thread.join(httpServerConfig.maxCGITime()); //最长等待时间
                } catch (InterruptedException e) {
                }
                if(!cgi_thread.complete){
                    cgi_thread.interrupt(); //中断
//                    synchronized(ServerLog.syn){
//                        ServerLog.error("CGI 线程异常");
//                    }
                }
                cgi_data.head = new String(cgi_thread.head);
                cgi_data.data = cgi_thread.data;
            } catch (IOException e) {
            }
        }
        return cgi_data;
    }

    private static void writePostMessage(OutputStream out,byte[] http_data)throws IOException
    {
        out.write(http_data);
        out.flush();
    }

    private static class CgiThread extends Thread {
        Process cgi = null;
        public byte[] head = null;
        public byte[] data = null;
        public byte[] all_data = null;
        public boolean complete = false;
        public int head_len;
        ByteArrayOutputStream array = new ByteArrayOutputStream(2000);
        byte[] buff = new byte[512];

        public CgiThread(Process c) {
            cgi = c;
        }

        public void run() {
            try {
                readCgi(cgi.getInputStream());
                if(head_len>0){
                    head = new byte[head_len];
                    for(int i=0;i<head_len;i++){
                        head[i] = all_data[i];
                    }
                }
                data = new byte[all_data.length-head_len-4];
                for(int i=0;i<all_data.length-head_len-4;i++){
                    data[i] = all_data[head_len+4+i];
                }
                complete = true;
            } catch (IOException e) {
//                synchronized(ServerLog.syn){
//                    ServerLog.error("CGI线程内部 IO异常");
//                }
            }
        }

        private void readCgi(InputStream in) throws IOException {
            head_len = 0;
            int readc = in.read(buff);
            boolean is_head = true;
            while (readc>0) {
                array.write(buff, 0, readc);
                for(int i=0;i<readc;i++){
                    if( is_head && i<readc-3 && buff[i] == '\r' && buff[i+1] == '\n'
                            && buff[i+2] == '\r' && buff[i+3] == '\n' ){
                        head_len += i;
                        is_head = false;
                        break;
                    }
                }
                if(is_head)
                    head_len += readc;
                readc = in.read(buff);
            }
            all_data = array.toByteArray();
        }
    };

}

