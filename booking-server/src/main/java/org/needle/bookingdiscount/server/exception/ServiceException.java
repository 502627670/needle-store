package org.needle.bookingdiscount.server.exception;

import java.text.MessageFormat;

public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 3149885123244982860L;
	
	private int code;
	
	private String message;
	
	private Throwable cause;
	
	public static ServiceException error(String message, Object... arguments) {
		ServiceException exception = new ServiceException(1, message, arguments);
		return exception;
	}
	
	public static ServiceException error(int code, String message, Object... arguments) {
		ServiceException exception = new ServiceException(code, message, arguments);
		return exception;
	}
	
	public ServiceException() {
		this.cause = null;
	}
	
	public ServiceException(String message, Object... arguments) {
		this.code = 1;
		this.message = MessageFormat.format(message, arguments);
		this.cause = null;
	}
	
	public ServiceException(int code, String message, Object... arguments) {
		this.code = code;
		this.message = MessageFormat.format(message, arguments);
		this.cause = null;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	
}
