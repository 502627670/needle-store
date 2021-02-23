package org.needle.bookingdiscount.server.api;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/region")
public class RegionController {
	
	@Autowired
	private RegionService regionService;
	
	@RequestMapping("/list")
	@ResponseBody
	public Object list(String parentId, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			return regionService.list(parentId);
		});
	}
	
}
