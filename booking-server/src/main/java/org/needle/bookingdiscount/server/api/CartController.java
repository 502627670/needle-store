package org.needle.bookingdiscount.server.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/cart")
public class CartController {
	
//	public static Long userId = 1098L;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private SecurityContext securityContext;
	
	// http://localhost:8080/cart/index
	//获取购物车的数据
	@RequestMapping("/index")
	@ResponseBody
	public Object index(@RequestHeader(name="X-Nideshop-Token") String token, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> userOpt = securityContext.getUserByToken(token);
			return userOpt.isPresent() ? 
				cartService.getIndexData(userOpt.get().getId()) : 
				new ModelMap();
		});
	}
	
	// 添加商品到购物车
	@RequestMapping("/add")
	@ResponseBody
	public Object add(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data,
			HttpServletRequest request) {
		Long goodsId = Long.valueOf(data.get("goodsId").toString());
		Long productId = Long.valueOf(data.get("productId").toString());
		int number = (int) data.get("number");
		// 0：正常加入购物车，1:立即购买，2:再来一单
		int addType = (int) data.get("addType");
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
				.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return cartService.add(user.getId(), goodsId, productId, number, addType);
		});
	}
	
	// 更新购物车的商品
	@RequestMapping("/update")
	@ResponseBody
	public Object update(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long productId = Long.valueOf(data.get("productId").toString());
		Long id = Long.valueOf(data.get("id").toString());
		Integer number = (Integer) data.get("number");
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
				.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return cartService.update(user.getId(), productId, id, number);
		});
	}
	
	// 删除购物车的商品
	@RequestMapping("/delete")
	@ResponseBody
	public Object delete(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long productIds = Long.valueOf(data.get("productIds").toString());
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
				.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return cartService.delete(user.getId(), productIds);
		});
	}
	
	// 选择或取消选择商品
	@RequestMapping("/checked")
	@ResponseBody
	public Object checked(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, 
			HttpServletRequest request) {
		String productIds = data.get("productIds").toString();
		int checked = (int) data.get("isChecked");
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return cartService.check(user.getId(), productIds, checked == 1 ? true : false);
		});
	}
	
	// 获取购物车商品件数
	@RequestMapping("/goodsCount")
	@ResponseBody
	public Object goodsCount(@RequestHeader(name="X-Nideshop-Token") String token, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> userOpt = securityContext.getUserByToken(token);
			if(userOpt.isPresent()) {
				return cartService.goodsCount(userOpt.get().getId());
			}
			
			ModelMap model = new ModelMap();
	        Map<String,Object> dataMap = new HashMap<String,Object>();
	        dataMap.put("goodsCount", 0);
	        model.addAttribute("cartTotal", dataMap);
	        return model;
		});
	}
	
	// 下单前信息确认
	@RequestMapping("/checkout")
	@ResponseBody
	public Object checkout(@RequestHeader(name="X-Nideshop-Token") String token, 
			String orderFrom, int type, String addressId, int addType,
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return cartService.checkout(user.getId(), orderFrom, type, addressId, addType);
		});
	}
	
}
