package com.coodroid.olympic.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 封装AES加密解密操作
 * @author Alkaid
 *
 */
public class AES {
	
	private static final byte[] iv=new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	/**
	 * AES解密 返回byte[]
	 * @param data  待解密数据
	 * @param password  密钥
	 * @return 若发生异常返回null
	 */
	public static String decode(byte[] data, String password) {
		try {
			IvParameterSpec mIvParameterSpec = new IvParameterSpec(iv);
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(password.getBytes(),"AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
			byte[] decodeData = mCipher.doFinal(data);
			return new String(decodeData);
		} catch (Exception e) {
			LogUtil.e(e);
		}
		return null;
	}
	
	/**
	 * AES解密 返回字符串
	 * @param fileName
	 * @param password
	 * @return
	 */
	public static String decode(String fileName, String password) {
		byte[] data=null;
		try {
			data = IOUtil.readFile2Byte(fileName);
		} catch (FileNotFoundException e) {
			LogUtil.e(e);
		} catch (IOException e) {
			LogUtil.e(e);
		}
		return decode(data, password);
	}

	/**
	 * AES加密
	 * @param data 待加密的数据
	 * @param password 密钥
	 * @return
	 */
	public static byte[] encrypt(byte[] data, String password) {
		try {
			IvParameterSpec mIvParameterSpec = new IvParameterSpec(iv);
			byte[] raw = password.getBytes();
			SecretKeySpec mSecretKeySpec = new SecretKeySpec(raw,"AES");
			Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
			byte[] encryptData = mCipher.doFinal(data);
			return encryptData;
		} catch (Exception e) {
			LogUtil.e(e);
		}
		return null;
	}

	/**
	 * 读取文件并解码
	 * @param filePath  文件路径
	 * @param password  密钥
	 * @return  
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static BufferedReader read2Br(String filePath,String password) throws FileNotFoundException, IOException {
		byte[] data=IOUtil.readFile2Byte(filePath);
		String decodeStr=decode(data, password);
		return IOUtil.getBufferReader(decodeStr);
	}
}
