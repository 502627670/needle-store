package org.needle.bookingdiscount.server.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.order.Order.JsonOrder;
import org.needle.bookingdiscount.domain.order.OrderExpress.JsonOrderExpress;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.data.DataPage;
import org.needle.bookingdiscount.server.data.ResponseMessage;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SecurityContext securityContext;
	
	// 提交订单
	@RequestMapping("/submit")
	@ResponseBody
	public Object submit(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long addressId = Long.valueOf(data.get("addressId").toString());
		BigDecimal freightPrice = new BigDecimal(data.get("freightPrice").toString());
		int offlinePay = (int) data.get("offlinePay");
		String postscript = (String) data.get("postscript");
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.submit(user.getId(), addressId, freightPrice, offlinePay, postscript);
		});
	}
	
	//订单列表
	@RequestMapping("/list")
	@ResponseBody
	public Object list(@RequestHeader(name="X-Nideshop-Token") String token, 
			int showType, 
			@RequestParam(defaultValue="1") int page,
			@RequestParam(defaultValue="10") int size,
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			int pageIndex = page > 0 ? page - 1 : page;
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			List<JsonOrder> dataList = orderService.list(user.getId(), showType, pageIndex, size);
			return new DataPage(pageIndex, dataList);
		});
	}
	
	//订单详情
	@RequestMapping("/detail")
	@ResponseBody
	public Object detail(@RequestHeader(name="X-Nideshop-Token") String token, 
			Long orderId, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.detail(orderId, user.getId());
		});
	}
	
	//订单删除
	@RequestMapping("/delete")
	@ResponseBody
	public Object delete(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long orderId = Long.valueOf(data.get("orderId").toString());
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.delete(orderId, user.getId());
		});
	}
	
	//取消订单
	@RequestMapping("/cancel")
	@ResponseBody
	public Object cancel(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long orderId = Long.valueOf(data.get("orderId").toString());
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.cancel(orderId, user.getId());
		});
	}
	
	//物流详情
	@RequestMapping("/confirm")
	@ResponseBody
	public Object confirm(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long orderId = Long.valueOf(data.get("orderId").toString());
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.confirm(orderId, user.getId());
		});
	}
	
	// 获取订单数
	@RequestMapping("/count")
	@ResponseBody
	public Object count(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		int showType = (int) data.get("showType");
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.count(showType, user.getId());
		});
	}
	
	// 我的页面获取订单数状态
	@RequestMapping("/orderCount")
	@ResponseBody
	public Object orderCount(@RequestHeader(name="X-Nideshop-Token") String token, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> userOpt = securityContext.getUserByToken(token);
			if(userOpt.isPresent()) {
				return orderService.orderCount(userOpt.get().getId());
			}
			ModelMap model = new ModelMap();
	        model.addAttribute("toPay", 0);
	        model.addAttribute("toDelivery", 0);
	        model.addAttribute("toReceive", 0);
	        return model;
		});
	}
	
	// 物流信息
	@RequestMapping("/express")
	@ResponseBody
	public Object express(Long orderId) {
		return ResponseHandler.doResponse(() -> {
			JsonOrderExpress orderExpress = null;
			try {
				orderExpress = orderService.express(orderId);
			}
			catch(ServiceException e) {
				log.error("express(..) => 查询物流信息失败：msg={}", e.getMessage());
				return ResponseMessage.failed(e.getCode(), e.getMessage());
			}
			return orderExpress;
		});
	}
	
	// 获取checkout页面的商品列表
	@RequestMapping("/orderGoods")
	@ResponseBody
	public Object orderGoods(@RequestHeader(name="X-Nideshop-Token") String token, 
			Long orderId, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "未登录"));
			return orderService.orderGoods(orderId, user.getId());
		});
	}
}
