package com.coodroid.olympic.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CookieManager {
	/** cookie存储的本地sharedPreference文件名*/
	private static final String SP_NAME="coodroid.cookie";
	
	/*RFC2109中定义的SET-COOKIE响应报头的格式为:
	Set-Cookie: Name = Value; expires=DATE; comment = value; domain = value; max-age = value; path = Value;
	secure; version = 1 * DIGIT;*/
	private static final String ATTR_SECURE="secure";
	private static final String ATTR_EXPIRES="expires";
	private static final String ATTR_COMMENT="comment";
	private static final String ATTR_MAX_AGE="max-age";
	private static final String ATTR_PATH="Secure";
	private static final String ATTR_VERSION="version";
	
	private static abstract class CooCookieStore implements CookieStore{
		protected final ArrayList<Cookie> cookies;
		protected final Comparator<Cookie> cookieComparator;
	    // -------------------------------------------------------- Class Variables
	    /**
	     * Default constructor.
	     */
	    public CooCookieStore() {
	        super();
	        this.cookies = new ArrayList<Cookie>();
	        this.cookieComparator = new CookieIdentityComparator();
	    }
	    /**
	     * Adds an {@link Cookie HTTP cookie}, replacing any existing equivalent cookies.
	     * If the given cookie has already expired it will not be added, but existing 
	     * values will still be removed.
	     * 
	     * @param cookie the {@link Cookie cookie} to be added
	     * 
	     * @see #addCookies(Cookie[])
	     * 
	     */
	    public synchronized void addCookie(Cookie cookie) {
	        if (cookie != null) {
	            // first remove any old cookie that is equivalent
	            for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
	                if (cookieComparator.compare(cookie, it.next()) == 0) {
	                    it.remove();
	                    break;
	                }
	            }
	            if (!cookie.isExpired(new Date())) {
	                cookies.add(cookie);
	            }
	        }
	    }

	    /**
	     * Adds an array of {@link Cookie HTTP cookies}. Cookies are added individually and 
	     * in the given array order. If any of the given cookies has already expired it will 
	     * not be added, but existing values will still be removed.
	     * 
	     * @param cookies the {@link Cookie cookies} to be added
	     * 
	     * @see #addCookie(Cookie)
	     * 
	     */
	    public synchronized void addCookies(Cookie[] cookies) {
	        if (cookies != null) {
	            for (Cookie cooky : cookies) {
	                this.addCookie(cooky);
	            }
	        }
	    }

	    /**
	     * Returns an immutable array of {@link Cookie cookies} that this HTTP
	     * state currently contains.
	     * 
	     * @return an array of {@link Cookie cookies}.
	     */
	    public synchronized List<Cookie> getCookies() {
	        return Collections.unmodifiableList(this.cookies);
	    }

	    /**
	     * Removes all of {@link Cookie cookies} in this HTTP state
	     * that have expired by the specified {@link java.util.Date date}. 
	     * 
	     * @return true if any cookies were purged.
	     * 
	     * @see Cookie#isExpired(Date)
	     */
	    public synchronized boolean clearExpired(final Date date) {
	        if (date == null) {
	            return false;
	        }
	        boolean removed = false;
	        for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
	            if (it.next().isExpired(date)) {
	                it.remove();
	                removed = true;
	            }
	        }
	        return removed;
	    }

	    @Override
	    public String toString() {
	        return cookies.toString();
	    }
	    
	    /**
	     * Clears all cookies.
	     */
	    public synchronized void clear() {
	        cookies.clear();
	    }
	}
	
	public static class CookieStore4Request extends CooCookieStore{
		Context context;
		public CookieStore4Request(Context context){
			super();
			this.context=context;
			SharedPreferences sp=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
			for(String key : sp.getAll().keySet()){
				String[] values=sp.getString(key, "").split(";");
				boolean first=true;
				BasicClientCookie cookie=null;
				for(String value:values){
					String[] attr=value.split("=");
					if(first){
						cookie=new BasicClientCookie(attr[0], attr[1]);
						first=false;
					}else{
						if(attr.length==1){
							//TODO secure单独设置 过期时间不知道是否要单独设置
							if(attr[0].equalsIgnoreCase(ATTR_SECURE)){
								cookie.setSecure(true);
							}
						}else if(attr.length==2){
							cookie.setAttribute(attr[0], attr[1]);
						}
					}
				}
				if(null!=cookie)
					cookies.add(cookie);
			}
		}
		
		public CookieStore4Request(CookieStore source,Context context){
			this(context);
			for(Cookie c : source.getCookies()){
				this.addCookie(c);
			}
		}
		
		public void save(){
			Editor et=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
			for(Cookie cookie:cookies){
			}
		}
	}
	
}
