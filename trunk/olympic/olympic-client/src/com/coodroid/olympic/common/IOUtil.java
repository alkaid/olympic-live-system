/**
 * 
 */
package com.coodroid.olympic.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;

/**
 * @author Alkaid
 * IO操作工具类
 */
public class IOUtil {
	/** 判断文件是否存在 */
	public static boolean existsFile(String pathName){
		File file = new File(pathName);
		return file.exists();
	}
	/** 判断SD卡是否存在或是否已经成功挂载 */
	public static boolean checkSDCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/** 递归删除目录或文件 没有判断SD卡是否存在 */
	public static void delFileDir(String pathName) {
		File file = new File(pathName);
		delFileDir(file);
	}
	/** 递归删除目录或文件 没有判断SD卡是否存在 */
	public static void delFileDir(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					delFileDir(files[i]);
				}
			}
			file.delete();
		}
	}
	
	/**
	 * 读取BufferReader返回字符串
	 * @param r
	 * @return
	 * @throws IOException
	 */
	public static String readBufferReader2Str(BufferedReader r) throws IOException{
		StringBuilder strb = new StringBuilder("");
		int i = 1;
		String line = null;
		try {
			while ((line = r.readLine()) != null) {
				strb.append(line).append("\n");
				i++;
			}
			return strb.toString();
		} catch (EOFException e) {
			LogUtil.d("line=" + i + " " + line);
			LogUtil.e(e);
		} finally{
			r.close();
		}
		return null;
	}
	
	/**
	 * 根据输入流和指定的编码方式读取数据
	 * 
	 * @param is
	 * @param enc
	 *            编码方式 若为null则用默认编码
	 * @return
	 * @throws IOException
	 */
	public static String readInputStrem2Str(InputStream is, String enc)
			throws IOException {
		BufferedReader r = null;
		if (enc != null)
			r = new BufferedReader(new InputStreamReader(is, enc), 1024);
		else
			r = new BufferedReader(new InputStreamReader(is), 1024);
		return readBufferReader2Str(r);
	}

	/**
	 * 根据输入管道读取字节流
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] readInputStream2Byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream outSrteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = inStream.read(buffer)) != -1) {
				outSrteam.write(buffer, 0, len);
			}
			return outSrteam.toByteArray();
		} catch (IOException e) {
			LogUtil.e(e);
		} finally{
			outSrteam.close();
			inStream.close();
		}
		return null;
	}
	
	/**
	 * 读取文件,返回字节
	 * @param filePath 文件路径
	 * @return  内容字节
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public static byte[] readFile2Byte(String filePath) throws FileNotFoundException,IOException{
		FileInputStream fi=null;
		try {
			fi = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw e;
		}
		BufferedInputStream bi=new BufferedInputStream(fi);
		return readInputStream2Byte(bi);
	}
	/**
	 * 读取文件，返回字符串
	 * @param filePath  文件路径
	 * @param enc	编码方式 若为null则用默认编码
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String readFile2Str(String filePath,String enc) 
			throws FileNotFoundException,IOException{
		FileInputStream fi=null;
		try {
			fi = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw e;
		}
		BufferedInputStream bi=new BufferedInputStream(fi);
		return readInputStrem2Str(bi, enc);
	}
	
	public static BufferedReader getBufferReader(byte[] data){
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
	}
	public static BufferedReader getBufferReader(String data){
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
	}
	
	/**
	 * 得到配置列表
	 * @param filePath  文件路径
	 * @param enc	编码方式 若为null则用默认编码
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws AlkaidException 
	 */
	public Map<String, String> getConfig(String fileName,String enc) throws FileNotFoundException, IOException {
		Map<String,String> map=new HashMap<String, String>();
		InputStream is=null;
		String str=readFile2Str(fileName, enc);
		String[] strlist=str.split("\n");
		for (String dat : strlist) {
			String[] keyvalue=dat.split("=");
			map.put(keyvalue[0], keyvalue[1]);
		}
		return map;
	}
}
