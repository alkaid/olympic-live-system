package com.coodroid.olympic.common;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
/** 此类存在的目的仅仅是为了能够返回一个线程安全的单例 */
public class HttpClientHolder {
	/** 连接超时时间 */
	private static final int TIMEOUT_CONN = 5000;
	/** 请求超时时间 */
	private static final int TIMEOUT_SOCKET = 5000;
	private HttpClientHolder(){}
	/**
	 * 获得一个线程安全的HttpClient 返回的实例可以用于单例或全局
	 * 
	 * @return
	 */
	public static DefaultHttpClient creatThreadSafeClient() {
		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_CONN);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT_SOCKET);
		HttpProtocolParams.setUserAgent(params, System.getProperties()
				.getProperty("http.agent"));
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		DefaultHttpClient client=new DefaultHttpClient(conMgr, params);
//		client.setHttpRequestRetryHandler(DEFAULT_RETRY_HANDLER);
		return client;
	}
	
	static DefaultHttpClient client;
	static {
		client = creatThreadSafeClient();
	}
	
	/**
	 * 获得一个线程安全并且是单例模式的HttpClient
	 */
	public static DefaultHttpClient getHttpClient() {
		return client;
	}
	
	public static void close(){
		DefaultHttpClient client=getHttpClient();
		client.getConnectionManager().shutdown();
	}
}
