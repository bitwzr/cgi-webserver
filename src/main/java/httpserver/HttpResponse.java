package httpserver;

import config.HttpServerConfig;
import org.aeonbits.owner.ConfigFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HttpResponse {

    private int state;
    private String type;
    private int content_length = 0;
    private String content_type = null;

    private static HashMap<String,Integer> response;
    private static boolean init = false;

    public HttpServerConfig httpServerConfig;

    /**
     * 构造HTTP响应头
     * @param type 响应类型
     */
    public HttpResponse(String type){
        if(!init){
            response = new HashMap<String,Integer>();
            response.put("OK", 200);
            response.put("Bad Request", 400);
            response.put("Forbidden", 403);
            response.put("Not Found", 404);
            response.put("Method Not Allowed", 405);
            response.put("Service Unavailable", 503);
            init = true;
        }
        this.type = type;
        this.state = response.get(type);
        httpServerConfig = ConfigFactory.create(HttpServerConfig.class);
    }

    /**
     * 设置响应文档类型
     * @param content_type 文档类型
     * @param content_length 文档长度
     */
    public void setContentType(String content_type,int content_length){
        this.content_type = content_type;
        this.content_length = content_length;
    }

    /**
     * 获取响应字段
     * @return 响应头部字符串
     */
    public String getResponse(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MM yyyy HH:mm:ss");
        dateFormat.format(date);
        StringBuilder s = new StringBuilder("HTTP/1.1 "+String.valueOf(state)+" "+type+"\r\n");
        s.append("Server: "+ httpServerConfig.serverName() + "\r\n");
        if(content_type!=null){
            s.append("Content-Type: "+content_type+"\r\n");
            s.append("Content-Length: "+String.valueOf(content_length)+ "\r\n");
        }
        s.append("Date: "+dateFormat.format(date)+" GMT\r\n");
        s.append("Connection: Close\r\n");
        s.append("\r\n");
        return s.toString();
    }

}
