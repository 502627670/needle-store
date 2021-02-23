package org.needle.bookingdiscount.server.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/search")
public class SearchController {
	
//	Long userId = CartController.userId;
	
	@Autowired
	private SearchService seachService;
	
	@Autowired
	private SecurityContext securityContext;
	
	//搜索页面数据
	@RequestMapping("/index")
	@ResponseBody
	public Object index(@RequestHeader(name="X-Nideshop-Token") String token, 
			String keywords, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> user = securityContext.getUserByToken(token);
			return seachService.index(keywords, user.isPresent() ? user.get().getId() : null);
		});
	}
	
	//搜索帮助
	@RequestMapping("/helper")
	@ResponseBody
	public Object helper(String keywords, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			return seachService.helper(keywords);
		});
	}
	
	@RequestMapping("/clearHistory")
	@ResponseBody
	public Object clearHistory(@RequestHeader(name="X-Nideshop-Token") String token, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> user = securityContext.getUserByToken(token);
			seachService.clearHistory(user.isPresent() ? user.get().getId() : null);
			return "{}";
		});
	}
	
}
