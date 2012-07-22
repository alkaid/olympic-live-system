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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.text.TextUtils;

/**
 * 通用Http联网类 <br/>
 * 默认配置如下：<br/>
 * 		连接超时：3000ms  请求超时:5000ms 重试次数：3次<br/>
 * 用完后必须调用 {@link #destroy()}方法来销毁对象
 * 
 * @author alkaid
 *
 */
public class HttpRequest {
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
	private static final int TIMEOUT_CONN = 5000;
	/** 请求超时时间 */
	private static final int TIMEOUT_SOCKET = 5000;
	/** 重试次数 */
	private static final int RETRY_COUNT=3;
	/** 异常处理类 */
	private static final RetryHandler DEFAULT_RETRY_HANDLER=new RetryHandler(RETRY_COUNT);
	
	//入参
	private String url;
	private String method=METHOD_GET;
	private Map<String, String> params;
	private Map<String, String> header;
	private String entityStr;
	private CookieStore cookieStore;
	private String charset="utf-8";
	//出参
	private String outString;
	private HttpResponse response;
	private InputStream inputStream;
	private DefaultHttpClient client;
	
	public HttpRequest(){
		client= HttpClientHolder.getHttpClient();
		setDefaultConfig();
	}
	/** 销毁对象 */
	public void destroy(){
		outString=null;
		response=null;
		inputStream=null;
		client=null;
	}
	/** 恢复默认配置 <br/>
	 * 连接超时：3000ms  请求超时:5000ms 重试次数：3次*/
	public HttpRequest setDefaultConfig(){
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_CONN);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				TIMEOUT_SOCKET);
		//默认异常处理
		client.setHttpRequestRetryHandler(DEFAULT_RETRY_HANDLER);
		//默认不传cookie
		client.setCookieStore(null);
		return this;
	}
	/** 设置连接超时时间 */
	public HttpRequest setConnectionTimeout(int time){
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, time);
		return this;
	}
	/** 设置请求超时时间 */
	public HttpRequest setSocketTimeout(int time){
		client.getParams().setParameter(
				CoreConnectionPNames.SO_TIMEOUT, time);
		return this;
	}
	/** 设置重试次数 */
	public HttpRequest setRetryCount(int count){
		client.setHttpRequestRetryHandler(new RetryHandler(count));
		return this;
	}
	/** 设置请求重试异常处理 */
	public HttpRequest setRequestRetryHandler(HttpRequestRetryHandler handler){
		client.setHttpRequestRetryHandler(handler);
		return this;
	}
	/** 设置请求地址*/
	public HttpRequest setUrl(String url) {
		this.url = url;
		return this;
	}
	/** 设置请求方法,值为   {@link #METHOD_GET} 或 {@link #METHOD_POST}*/
	public HttpRequest setMethod(String method) {
		this.method = method;
		return this;
	}
	/** 设置get/post参数 */
	public HttpRequest setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}
	/** 设置请求头*/
	public HttpRequest setHeader(Map<String, String> header) {
		this.header = header;
		return this;
	}
	/** 设置get/post参数 为字符串形式*/
	public HttpRequest setEntityStr(String entityStr) {
		this.entityStr = entityStr;
		return this;
	}
	/** 设置cookie*/
	public HttpRequest setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
		return this;
	}
	/** 设置响应内容的编码格式*/
	public HttpRequest setCharset(String charset) {
		this.charset = charset;
		return this;
	}
	/** 获得响应输出*/
	public String getOutString() {
		return outString;
	}
	/** 获得response*/
	public HttpResponse getResponse() {
		return response;
	}
	/** 获得输入流*/
	public InputStream getInputStream() {
		return inputStream;
	}
	/** 获得httpClient*/
	public DefaultHttpClient getClient() {
		return client;
	}
	/**
	 * 提交请求来获得输出字符串
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getContent()
			throws MalformedURLException, IOException {
		this.outString=IOUtil.readInputStrem2Str(openUrl(), charset);
		return outString;
	}
	/**
	 * 提交请求来获得输入流
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public InputStream openUrl() throws MalformedURLException,
			IOException {
		HttpResponse response = request();
		this.inputStream = response.getEntity().getContent();
		return inputStream;
	}
	/**
	 * 提交请求 返回response
	 * @return
	 * @throws IOException
	 */
	public HttpResponse request() throws IOException {
		LogUtil.i("---url---"+url);
		HttpUriRequest req = null;
		// url添加get参数
		if (method.equals(METHOD_GET)) {
			//form键值对参数方式
			if(null!=params && params.size()>0){
				if(url.contains("?")){
					url = url + encodeUrl(params, true);
				}else{
					url = url + "?" + encodeUrl(params, false);
				}
			//entity String参数方式
			}else if(!TextUtils.isEmpty(this.entityStr)){
				if(url.contains("?")){
					url = url + "&"+entityStr;
				}else{
					url = url + "?" + entityStr;
				}
			}
			req = new HttpGet(url);
		} else {
			req = new HttpPost(url);
			//form键值对参数方式
			if(null!=params && params.size()>0){
				List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					postParams.add(new BasicNameValuePair(key, params.get(key)));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams,HTTP.UTF_8);
				((HttpPost) req).setEntity(entity);
			//entity String参数方式
			}else if(!TextUtils.isEmpty(this.entityStr)){
				StringEntity strEntity=new StringEntity(entityStr);
				((HttpPost) req).setEntity(strEntity);
			}
		}
		//设置cookie
		if(null != cookieStore){
			client.setCookieStore(cookieStore);
		}
		// 设置header
		if (null != header && header.size() > 0) {
			for (String key : header.keySet()) {
				req.setHeader(key, header.get(key));
			}
		}
		this.response= client.execute(req);
		return response;
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
