package org.needle.bookingdiscount.error;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.needleframe.core.MessageCode;
import org.needleframe.core.web.response.ResponseMessage;
import org.needleframe.security.SecurityUtils;
import org.needleframe.security.UserDetailsServiceImpl.SessionUser;
import org.needleframe.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ExceptionController implements ErrorController {
	
	private Logger logger = LoggerFactory.getLogger(ExceptionController.class);
	
	@Autowired
    private ErrorAttributes errorAttributes;
	
    private static final String error_default = "/error";

    public String getErrorPath() {
        return error_default;
    }
    
    @RequestMapping(value = error_default,  produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseMessage error(WebRequest wRequest, 
    		HttpServletRequest request, HttpServletResponse response) {
    	logger.info("error(..) => Error: url={}, error:url={}", request.getRequestURL());
    	response.setCharacterEncoding("UTF-8");
    	Map<String, Object> body = errorAttributes.getErrorAttributes(wRequest, true);
    	logger.info("error(..) => Error: url={}, error:url={}, body={}", request.getRequestURL(), body);
        SessionUser user = SecurityUtils.getUser(); 
        if(user == null) { 
        	return ResponseMessage.failed(MessageCode.SESSION_EXPIRED, "会话已过期"); 
        }
        return ResponseMessage.failed(JsonUtils.toJSON(body));
    }
	
}
