package httpserver;

import config.HttpServerConfig;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 分析HTTP头部
 *
 */
public class AnalysisHttpHeader {

    /**
     * HTTP头部的Method
     */
    public String method;
    /**
     * 请求的URL地址（除去了?后的内容）
     */
    public String URL;
    /**
     * 请求的HTTP版本号
     */
    public String version;
    /**
     * GET里面的查询语句
     */
    public String get_query = null;
    /**
     * 请求的文件类型，实为文件后缀（强制小写，避免改变大小写而窃取文件）
     */
    public String file_type;
    /**
     * 请求的文件
     */
    public File file = null;
    /**
     * HTTP的头部字段map
     */
    public Map<String,String> map;

    private String[] KEY = { "Host",
            "User-Agent",
            "Accept",
            "Referer",
            "Accept-Language",
            "Content-Type",
            "Content-Length",
            "Cache-Control",
            "Accept-Encoding",
            "UA-CPU",
            "Date"};

    private String key;
    private String value;

    public HttpServerConfig httpServerConfig;

    public AnalysisHttpHeader(StringBuilder sb){

        httpServerConfig = ConfigFactory.create(HttpServerConfig.class);

        map = new HashMap<String,String>();
        StringTokenizer st = new StringTokenizer(sb.toString(),"\r\n"); //按换行符分隔
        String method_line = st.nextToken(); //头行
        StringTokenizer st_method_line = new StringTokenizer(method_line," "); //按空格分开
        method  = st_method_line.nextToken();
        URL     = st_method_line.nextToken();
        version = st_method_line.nextToken();

        while(st.hasMoreTokens()){
            String[] next = st.nextToken().split(": "); //按: 分隔
            if(next[0].length() == 1) break;
            key = next[0];
            value = next[1];
            for(int i=0;i<KEY.length;i++){
                if(key.equals(KEY[i])){
                    map.put(KEY[i],value);
                    break;
                }
            }
        }

        StringTokenizer query = new StringTokenizer(URL,"?");
        if(query.countTokens() == 2){
            URL = query.nextToken();
            get_query = query.nextToken();
        }

        String host;
        if((host=map.remove("Host"))!=null){
            String[] ts = host.split(":");
            map.put("Host", ts[0]);
        }

//        if(URL.endsWith("/"))
//            URL += httpServerConfig.homePage();
//        file = new File(httpServerConfig.webPath() + URL);
    }
}
