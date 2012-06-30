package com.coodroid.olympic.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * 通用Http联网类 <br/>
 * 默认配置如下：<br/>
 * 		连接超时：3000ms  请求超时:5000ms 重试次数：3次
 * 
 * @author alkaid
 *
 */
public class HttpUtils {
	public static final String TAG="HttpUtils";
	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String HEADER_SET_COOKIE = "set-cookie";
	public static final String HEADER_COOKIE = "Cookie";
	public static final String HEADER_HOST = "Host";
	public static final String HEADER_CONNECTION = "Connection";
	public static final String HEADER_REFERER = "Referer";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_ORIGIN = "Origin";
	public static final String HEADER_USER_AGENT = "User-Agent";
	/** 连接超时时间 */
	private static final int TIMEOUT_CONN = 3000;
	/** 请求超时时间 */
	private static final int TIMEOUT_SOCKET = 5000;
	/** 重试次数 */
	private static final int RETRY_COUNT=3;
	/** 异常处理类 */
	private static final RetryHandler DEFAULT_RETRY_HANDLER=new RetryHandler(RETRY_COUNT);
	/** 恢复默认配置 <br/>
	 * 连接超时：3000ms  请求超时:5000ms 重试次数：3次*/
	public static void setDefaultConfig(){
		DefaultHttpClient client=(DefaultHttpClient) getHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_CONN);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				TIMEOUT_SOCKET);
		client.setHttpRequestRetryHandler(DEFAULT_RETRY_HANDLER);
	}
	static{
		setDefaultConfig();
	}
	/** 设置连接超时时间 */
	public static void setConnectionTimeout(int time){
		DefaultHttpClient client=(DefaultHttpClient) getHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, time);
	}
	/** 设置请求超时时间 */
	public static void setSocketTimeout(int time){
		DefaultHttpClient client=(DefaultHttpClient) getHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.SO_TIMEOUT, time);
	}
	/** 设置重试次数 */
	public static void setRetryCount(int count){
		DefaultHttpClient client=(DefaultHttpClient) getHttpClient();
		client.setHttpRequestRetryHandler(new RetryHandler(count));
	}
	/** 设置请求重试异常处理 */
	public static void setRequestRetryHandler(HttpRequestRetryHandler handler){
		DefaultHttpClient client=(DefaultHttpClient) getHttpClient();
		client.setHttpRequestRetryHandler(handler);
	}

	/**
	 * form entity方式提交请求来获得字符串 <br/>
	 * 一般用于模拟html form或xml提交 
	 * 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param params
	 *            url参数 或 entity参数
	 * @param enc
	 *            编码方式
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws SearchException
	 */
	public static String getContent(String url, String method,
			Map<String, String> params, String enc)
			throws MalformedURLException, IOException {
		return IOUtil.readInputStrem2Str(openUrl(url, method, params), enc);
	}
	/**
	 * String entity方式提交请求来获得输出字符串<br/>
	 *  一般用于Json提交或者无参的xml提交 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param entityStr
	 *            entity参数
	 * @param enc
	 *            编码方式
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getContent(String url, String method,
			String entityStr, String enc)
			throws MalformedURLException, IOException {
		return IOUtil.readInputStrem2Str(openUrl(url, method, entityStr), enc);
	}

	/**
	 * form entity方式提交请求来获得字符串 <br/>
	 * 一般用于模拟html form或xml提交 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param params
	 *            url参数 或 entity参数
	 * @param header 请求头
	 * @param enc
	 *            编码方式
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getContent(String url, String method,
			Map<String, String> params, Map<String, String> header, String enc)
			throws MalformedURLException, IOException {
		return IOUtil.readInputStrem2Str(openUrl(url, method, params, header), enc);
	}
	/**
	 * String entity方式提交请求来获得输出字符串<br/>
	 *  一般用于Json提交或者无参的xml提交
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param entityStr
	 *            entity参数
	 * @param header 请求头
	 * @param enc
	 *            编码方式
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getContent(String url, String method,
			String entityStr, Map<String, String> header, String enc)
			throws MalformedURLException, IOException {
		return IOUtil.readInputStrem2Str(openUrl(url, method, entityStr, header), enc);
	}

	/**
	 * form entity方式提交请求 <br/>
	 * 一般用于模拟html form或xml提交 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param params
	 *            url参数 或 entity参数
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws SearchException
	 */
	public static InputStream openUrl(String url, String method,
			Map<String, String> params) throws MalformedURLException,
			IOException {
		HttpResponse response = request(url, method, params);
		return response.getEntity().getContent();
	}
	
	/**
	 * String entity方式提交请求<br/>
	 *  一般用于Json提交或者无参的xml提交
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param entityStr
	 *            entity参数
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream openUrl(String url, String method,
			String entityStr) throws MalformedURLException,
			IOException {
		HttpResponse response = request(url, method, entityStr);
		return response.getEntity().getContent();
	}

	/**
	 * form entity方式提交请求 <br/>
	 * 一般用于模拟html form或xml提交
	 * 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param params
	 *            url参数 或 entity参数
	 * @param header 请求头
	 * @return
	 * @throws IOException
	 */
	public static InputStream openUrl(String url, String method,
			Map<String, String> params, Map<String, String> header)
			throws IOException {
		HttpResponse response = request(url, method, params, header);
		return response.getEntity().getContent();
	}
	
	/**
	 * String entity方式提交请求<br/>
	 *  一般用于Json提交或者无参的xml提交
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param entityStr
	 *            entity参数
	 * @param header 请求头
	 * @return
	 * @throws IOException
	 */
	public static InputStream openUrl(String url, String method,
			String entityStr, Map<String, String> header)
			throws IOException {
		HttpResponse response = request(url, method, entityStr, header);
		return response.getEntity().getContent();
	}

	/**
	 * 获得一个线程安全的HttpClient 返回的实例可以用于单例或全局
	 * 
	 * @return
	 */
	public static HttpClient creatThreadSafeClient() {
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
		client.setHttpRequestRetryHandler(DEFAULT_RETRY_HANDLER);
		return client;
	}

	/** 此类存在的目的仅仅是为了能够返回一个线程安全的单例 */
	private static class clientHolder {
		static HttpClient client;
		static {
			client = creatThreadSafeClient();
		}
	}

	/**
	 * 获得一个线程安全并且是单例模式的HttpClient
	 */
	public static HttpClient getHttpClient() {
		return clientHolder.client;
	}
	
	static class RetryHandler implements HttpRequestRetryHandler{
		private int retryCount;
		public RetryHandler(int retryCount){
			this.retryCount=retryCount;
		}
		@Override
		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			if(executionCount > retryCount){
				return false;
			}
			if(exception instanceof ConnectTimeoutException){
				LogUtil.e(TAG, "http连接超时,共执行了"+executionCount+"次", exception);
				return true;
			}
			if(exception instanceof SocketTimeoutException){
				LogUtil.e(TAG, "http请求超时,共执行了"+executionCount+"次", exception);
				return true;
			}
			if (exception instanceof NoHttpResponseException) { 
                // 服务停掉则重新尝试连接 
                return true; 
            } 
            if (exception instanceof SSLHandshakeException) { 
                // SSL异常不需要重试 
                return false; 
            } 
            if (exception instanceof UnknownHostException){
            	LogUtil.e(TAG, "http找不到主机,共执行了"+executionCount+"次", exception);
            	return true;
            }
            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            }
//            if (exception instanceof InterruptedIOException) {
//                // Timeout
//                return false;
//            }
            HttpRequest request = (HttpRequest) context.getAttribute( 
                    ExecutionContext.HTTP_REQUEST); 
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);  
            if (idempotent) { 
                // 请求内容相同则重试  
                return true; 
            } 
            return false; 
		}
	}

	/**
	 * form entity方式提交请求 <br/>
	 * 一般用于模拟html form或xml提交
	 * 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param params
	 *            url参数 或 entity参数
	 * @param header 请求头
	 * @return
	 * @throws IOException
	 */
	public static HttpResponse request(String url, String method,
			Map<String, String> params, Map<String, String> header)
			throws IOException {
		LogUtil.i("---url---"+url);
		HttpClient client = getHttpClient();
		HttpUriRequest req = null;
		// url添加get参数
		if (method.equals(METHOD_GET)) {
			if(params.size()>0)
				if(url.contains("?")){
					url = url + encodeUrl(params, true);
				}else{
					url = url + "?" + encodeUrl(params, false);
				}
			req = new HttpGet(url);
		} else {
			req = new HttpPost(url);
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				postParams.add(new BasicNameValuePair(key, params.get(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
			((HttpPost) req).setEntity(entity);
		}
		// TODO 移至构造HttpClinet里
		// req.setHeader(HEADER_USER_AGENT, System.getProperties()
		// .getProperty("http.agent"));
		// 设置header
		if (null != header && header.size() > 0) {
			for (String key : header.keySet()) {
				req.setHeader(key, header.get(key));
			}
		}
		// 超时设置 TODO 移至构造HttpClinet里
		// client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
		// TIMEOUT_CONN);
		// client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
		// TIMEOUT_SOCKET);
		return client.execute(req);
	}

	/**
	 * String entity方式提交请求<br/>
	 *  一般用于Json提交或者无参的xml提交
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param entityStr  用来构造StringEntity 注意entityStr第一字符不能为'&'
	 * @param header 请求头
	 * @return
	 * @throws IOException
	 */
	public static HttpResponse request(String url, String method,
			String entityStr, Map<String, String> header)
			throws IOException {
		LogUtil.i("---url---"+url);
		HttpClient client = getHttpClient();
		HttpUriRequest req = null;
		// url添加get参数
		if (method.equals(METHOD_GET)) {
			if(url.contains("?")){
				url = url + "&"+entityStr;
			}else{
				url = url + "?" + entityStr;
			}
			req = new HttpGet(url);
		} else {
			req = new HttpPost(url);
			StringEntity strEntity=new StringEntity(entityStr);
			((HttpPost) req).setEntity(strEntity);
		}
		// TODO 移至构造HttpClinet里
		// req.setHeader(HEADER_USER_AGENT, System.getProperties()
		// .getProperty("http.agent"));
		// 设置header
		if (null != header && header.size() > 0) {
			for (String key : header.keySet()) {
				req.setHeader(key, header.get(key));
			}
		}
		// 超时设置 TODO 移至构造HttpClinet里
		// client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
		// TIMEOUT_CONN);
		// client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
		// TIMEOUT_SOCKET);
		return client.execute(req);
	}
	/**
	 * form entity方式提交请求 <br/>
	 * 一般用于模拟html form或xml提交
	 * 
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param params
	 *            url参数 或 entity参数
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse request(String url, String method,
			Map<String, String> params) throws ClientProtocolException,
			IOException {
		return request(url, method, params, null);
	}
	
	/**
	 * String entity方式提交请求<br/>
	 *  一般用于Json提交或者无参的xml提交
	 * @param url
	 * @param method
	 *            "GET"或"POST"
	 * @param entityStr
	 *            entity参数
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse request(String url, String method,
			String entityStr) throws ClientProtocolException,
			IOException {
		return request(url, method, entityStr, null);
	}
	public static void close(){
		HttpClient client=getHttpClient();
		client.getConnectionManager().shutdown();
	}

	/**
	 * 将指定的参数转换成Url参数的方式 形如 id=001&user=0002
	 * 
	 * @param params
	 *            url参数 或 entity参数
	 * @param isExistParams
	 *            是否已经有参数，若为真则返回的结果的第一个字符为'&'
	 * @return
	 */
	public static String encodeUrl(Map<String, String> params,
			boolean isExistParams) {
		StringBuilder strb = new StringBuilder("");
		if (null != params && params.size() > 0) {
			boolean first = !isExistParams;
			for (String key : params.keySet()) {
				if (first)
					first = false;
				else
					strb.append("&");
				strb.append(key).append("=").append(params.get(key));
			}
		}
		return strb.toString();
	}

	

}
