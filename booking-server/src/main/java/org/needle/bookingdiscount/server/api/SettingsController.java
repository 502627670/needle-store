package org.needle.bookingdiscount.server.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/settings")
public class SettingsController {
	
	@Autowired
	private SettingsService settingsService;
	
	@Autowired
	private SecurityContext securityContext;
	
	@RequestMapping("/showSettings")
	@ResponseBody
	public Object showSettings() {
		return ResponseHandler.doResponse(() -> {
			return settingsService.showSettings();
		});
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public Object save(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		String name = (String) data.get("name"); 
		String mobile = (String) data.get("mobile"); 
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return settingsService.save(user.getId(), name, mobile);
		});
	}
	
	@RequestMapping("/userDetail")
	@ResponseBody
	public Object userDetail(@RequestHeader(name="X-Nideshop-Token") String token, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return settingsService.userDetail(user.getId());
		});
	}
	
}
