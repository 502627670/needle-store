package org.needle.bookingdiscount.server.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/goods")
public class GoodsController {
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private SecurityContext securityContext;
	
//	// 统计商品总数
	@RequestMapping("/count")
	@ResponseBody
	public Object count() {
		return ResponseHandler.doResponse(() -> {
			return goodsService.count();
		});
	}
	
	// 获得商品的详情
	@RequestMapping("/detail")
	@ResponseBody
	public Object detail(@RequestHeader(name="X-Nideshop-Token") String token, 
			Long id, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> user = securityContext.getUserByToken(token);
			Long userId = user.isPresent() ? user.get().getId() : null;
			return goodsService.detail(id, userId);
		});
	}
	
	// 获得商品列表
	@RequestMapping("/list")
	@ResponseBody
	public Object list(@RequestHeader(name="X-Nideshop-Token") String token,
			String keyword, String sort, String order, String sales,
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> user = securityContext.getUserByToken(token);
			Long userId = user.isPresent() ? user.get().getId() : null;
			return goodsService.list(userId, keyword, sort, order, sales);
		});
	}
	
//	@RequestMapping("/list")
//	@ResponseBody
//	public Object list(@RequestHeader(name="X-Nideshop-Token") String token,
//			@RequestBody Map<String,Object> data,
//			HttpServletRequest request) {
//		String keyword = (String) data.get("keyword");
//		String sort = (String) data.get("sort");
//		String order = (String) data.get("order");
//		String sales = (String) data.get("sales");
//		return ResponseHandler.doResponse(() -> {
//			Optional<MemberUser> user = securityContext.getUserByToken(token);
//			Long userId = user.isPresent() ? user.get().getId() : null;
//			return goodsService.list(userId, keyword, sort, order, sales);
//		});
//	}
	
	// 获得商品的详情
	@RequestMapping("/goodsShare")
	@ResponseBody
	public Object goodsShare(Long id) {
		return ResponseHandler.doResponse(() -> {
			return goodsService.goodsShare(id);
		});
	}
	
	// 获得商品的详情
	@RequestMapping("/saveUserId")
	@ResponseBody
	public Object saveUserId() {
		return ResponseHandler.doResponse(() -> {
			goodsService.saveUserId();
			return "OK";
		});
	}
	
}
