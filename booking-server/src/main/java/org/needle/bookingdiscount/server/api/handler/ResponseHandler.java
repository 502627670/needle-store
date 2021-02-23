package org.needle.bookingdiscount.server.api.handler;

import org.needle.bookingdiscount.server.data.ResponseMessage;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseHandler {
	
	@JsonIgnore
	private final static Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
	
	public static ResponseMessage doResponse(RequestHandler executor) {
		try {
			Object data = executor.doRequest();
			
			if(data instanceof ResponseMessage) {
				return (ResponseMessage) data;
			}
			
			return ResponseMessage.success(data);
		}
		catch(ServiceException e) {
			logger.error("doResponse(..) => 执行页面请求失败：msg={}", e.getMessage(), e);
			return ResponseMessage.failed(e.getCode(), e.getMessage());
		}
		catch (Exception e) {
			logger.error("doResponse(..) => 执行页面请求失败：{}", e);
			return ResponseMessage.failed(e.getMessage());
		}
	}
	
}
