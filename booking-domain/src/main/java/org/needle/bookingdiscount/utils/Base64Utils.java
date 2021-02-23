package org.needle.bookingdiscount.utils;

import java.io.UnsupportedEncodingException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.StringUtils;

public class Base64Utils {
	
	public static String encode(String text) {
		if(!StringUtils.hasText(text)) {
			return text;
		}
		Base64 base64 = new Base64();
		try {
			return base64.encodeToString(text.getBytes("UTF-8"));
		} 
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decode(String text) {
		if(!StringUtils.hasText(text)) {
			return text;
		}
		Base64 base64 = new Base64();
		try {
			return new String(base64.decode(text), "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
