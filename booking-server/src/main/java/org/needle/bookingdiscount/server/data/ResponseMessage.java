package org.needle.bookingdiscount.server.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResponseMessage implements Serializable {
	
	private static final long serialVersionUID = -8335867611256170670L;
	
	private int errno;
	
	private String errmsg;
	
	private Object data;
	
	public static ResponseMessage success(Object data) {
		return success(0, "", data);
	}
	
	public static ResponseMessage success(int errno, String errmsg, Object data) {
		ResponseMessage rm = new ResponseMessage();
		rm.setErrmsg(errmsg);
		rm.setErrno(errno);
		rm.setData(data);
		return rm;
	}
	
	public static ResponseMessage failed(int errno, String errmsg) {
		ResponseMessage rm = new ResponseMessage();
		rm.setErrmsg(errmsg);
		rm.setErrno(errno);
		return rm;
	}
	
	public static ResponseMessage failed(String errmsg) {
		ResponseMessage rm = new ResponseMessage();
		rm.setErrmsg(errmsg);
		rm.setErrno(1);
		return rm;
	}
	
	
}
