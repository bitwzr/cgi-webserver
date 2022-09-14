package org.microwebserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * HTTP��Ӧ
 * @author ϐ�侉
 *
 */
public class HttpResponse {
	
	private int state;
	private String type;
	private int content_length = 0;
	private String content_type = null;
	
	private static HashMap<String,Integer> response;
	private static boolean init = false;

	/**
	 * ����HTTP��Ӧͷ
	 * @param type ��Ӧ����
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
	}
	
	/**
	 * ������Ӧ�ĵ�����
	 * @param content_type �ĵ�����
	 * @param content_length �ĵ�����
	 */
	public void setContentType(String content_type,int content_length){
		this.content_type = content_type;
		this.content_length = content_length;
	}
	
	/**
	 * ��ȡ��Ӧ�ֶ�
	 * @return ��Ӧͷ���ַ���
	 */
	public String getResponse(){
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MM yyyy HH:mm:ss");
		dateFormat.format(date);
		StringBuilder s = new StringBuilder("HTTP/1.1 "+String.valueOf(state)+" "+type+"\r\n");
		s.append("Server: "+Config.SERVER_NAME+"/"+Config.SERVER_VERSION+"\r\n");
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
