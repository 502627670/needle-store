package org.needle.bookingdiscount.server.api.handler;

@FunctionalInterface
public interface RequestHandler {
	
    Object doRequest() throws Exception;
    
}