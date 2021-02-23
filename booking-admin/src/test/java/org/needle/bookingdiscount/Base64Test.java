package org.needle.bookingdiscount;

import java.io.UnsupportedEncodingException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.needleframe.utils.Base64Utils;

public class Base64Test {
	
	public static void base64Test() {
		Base64 base64 = new Base64();
		
		String encodedText = "6buE6YeR/5aWz";
		try {
			System.out.println(new String(base64.decode(encodedText), "UTF-8"));
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Base64Utils.decode("6buE6YeR"));
		System.out.println(Base64Utils.encode("家有儿女"));
		
	}
	
}
