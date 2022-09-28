package httpserver;

import config.HttpServerConfig;
import org.aeonbits.owner.ConfigFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ServerLog {

	/**
	 * 同步标识
	 */
	public static final Object syn = "synchronized";

	private static boolean io;
	private static BufferedWriter log = null;
	private static BufferedWriter error = null;

	private static int count = 0;

	public static HttpServerConfig httpServerConfig;

	/**
	 * 服务器日志初始化
	 */
	public static void initServerLog(){
		// io = Config.STANDARD_IO;

		httpServerConfig = ConfigFactory.create(HttpServerConfig.class);

		io = true;
		Date date = new Date();
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
		SimpleDateFormat od = new SimpleDateFormat("MM_d_HH_mm_ss");
		if(httpServerConfig.logPath()!=null){
			try {
				log = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(httpServerConfig.logPath()+od.format(date)+".log")));
			} catch (FileNotFoundException e) {
				System.out.println("LogError("+d.format(date)+"):创建log失败");
			}
		}
		if(httpServerConfig.errorLogPath()!=null){
			try {
				error = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(httpServerConfig.errorLogPath()+od.format(date)+".log")));
			} catch (FileNotFoundException e) {
				System.out.println("LogError("+d.format(date)+"):创建error_log失败");
			}
		}
	}

	/**
	 * 写日志
	 * @param s 日志的内容
	 */
	public static void log(String s){
		Date date = new Date();
		SimpleDateFormat d = new SimpleDateFormat("yyyy/MM/d HH:mm:ss");
		if(io)
			System.out.println("Log("+d.format(date)+"):"+s);
		if(log != null){
			try {
				log.write("Log("+d.format(date)+"):"+s+"\r\n");
				count ++;
				if(count > httpServerConfig.logFlush()){
					count = 0;
					log.flush();
				}
			} catch (IOException e) {
				System.out.println("LogError("+d.format(date)+"):log输出IO异常");
			}
		}
	}

	/**
	 * 写错误信息
	 * @param s 错误信息的内容
	 */
	public static void error(String s){
		Date date = new Date();
		SimpleDateFormat d = new SimpleDateFormat("yyyy/MM/d HH:mm:ss");
		if(io)
			System.out.println("Error("+d.format(date)+"):"+s);
		if(error != null){
			try {
				error.write("Error("+d.format(date)+"):"+s+"\r\n");
				error.flush();
			} catch (IOException e) {
				System.out.println("LogError("+d.format(date)+"):error输出IO异常");
			}
		}
	}

}
