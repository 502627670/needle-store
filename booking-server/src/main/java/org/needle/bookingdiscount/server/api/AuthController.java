package org.needle.bookingdiscount.server.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.needle.bookingdiscount.domain.member.MemberUser.JsonMemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.AuthService;
import org.needle.bookingdiscount.server.service.AuthService.WxUserInfo;
import org.needle.bookingdiscount.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private SecurityContext securityContext;
	
	@Data
	public static class SessionUser {
		private Long id;
		private String nickname;
		private String appid;
	}
	
	@Data
	static class LoginData {
		String appid;
		String code;
		WxUserInfo userInfo;
	}
	
	// http://localhost:8080/auth/loginByWeixin
	@RequestMapping("/loginByWeixin")
	@ResponseBody
	public Object loginByWeixin(
			@RequestBody LoginData data,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("loginByWeixin(..) => appid={}, code={}, userInfo={}", data.appid, data.code, data.userInfo);
		return ResponseHandler.doResponse(() -> {
			WxUserInfo fullUserInfo = data.getUserInfo();
			String clientIp = RequestUtils.getClientIp(request);
			log.info("clientIp={}", clientIp);
			ModelMap model = authService.login(fullUserInfo, data.appid, data.code, clientIp);
			JsonMemberUser user = (JsonMemberUser) model.get("userInfo");
			String token = request.getSession().getId();
			model.put("token", token);
			securityContext.setUserToken(user.getId(), token);
			log.debug("loginByWeixin(..) => JSESSIONID={}", token);
			return model;
		});
	}
	
	@RequestMapping("/logout")
	@ResponseBody
	public Object logout() {
		return ResponseHandler.doResponse(() -> {
			return new ModelMap();
		});
	}
	
}
