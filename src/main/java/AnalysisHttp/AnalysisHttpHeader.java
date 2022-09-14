package org.microwebserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ����HTTPͷ��
 * @author ϐ�侉
 *
 */
public class AnalysisHttpHeader {
	
	/**
	 * HTTPͷ����Method
	 */
	public String method;
	/**
	 * �����URL��ַ����ȥ��?������ݣ�
	 */
	public String URL;
	/**
	 * �����HTTP�汾��
	 */
	public String version;
	/**
	 * GET����Ĳ�ѯ���
	 */
	public String get_query = null;
	/**
	 * ������ļ����ͣ�ʵΪ�ļ���׺��ǿ��Сд������ı��Сд����ȡ�ļ���
	 */
	public String file_type;
	/**
	 * ������ļ�
	 */
	public File file = null;
	/**
	 * HTTP��ͷ���ֶ�map
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
    
	public AnalysisHttpHeader(StringBuilder sb){
		map = new HashMap<String,String>();
		StringTokenizer st = new StringTokenizer(sb.toString(),"\r\n"); //�����з��ָ�
		String method_line = st.nextToken(); //ͷ��
		StringTokenizer st_method_line = new StringTokenizer(method_line," "); //���ո�ֿ�
		method  = st_method_line.nextToken();
		URL     = st_method_line.nextToken();
		version = st_method_line.nextToken();
		
		while(st.hasMoreTokens()){
			String[] next = st.nextToken().split(": "); //��: �ָ�
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
		
		if(URL.endsWith("/"))
			URL += Config.HOME_PAGE;
		
		StringTokenizer st_type = new StringTokenizer(URL,".");
		while(st_type.hasMoreTokens()){
			file_type = st_type.nextToken().toLowerCase();
		}

		file = new File(Config.WEB_PATH+URL);
	}
}
