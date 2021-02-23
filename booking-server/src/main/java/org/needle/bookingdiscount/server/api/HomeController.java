package org.needle.bookingdiscount.server.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.data.ResponseMessage;
import org.needle.bookingdiscount.server.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/index")
public class HomeController {
	
	@Autowired
	private HomeService homeService;
	
	// http://localhost:8080/index/appInfo
	@RequestMapping("/appInfo")
	@ResponseBody
	public Object appInfo(HttpServletRequest request, HttpServletResponse response) {
		log.debug("appInfo(..) => JSESSIONID={}", request.getSession().getId());
		return ResponseHandler.doResponse(() -> {
			ModelMap model = homeService.getHomeData();
			return ResponseMessage.success(model);
		});
	}
	
}
