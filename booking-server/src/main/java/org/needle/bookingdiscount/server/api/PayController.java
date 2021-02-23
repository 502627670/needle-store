package org.needle.bookingdiscount.server.api;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.service.PayService;
import org.needle.bookingdiscount.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/pay")
public class PayController {
	
	@Autowired
	private PayService payService;
	
	@RequestMapping("/preWeixinPay")
	@ResponseBody
	public Object preWeixinPay(String appid, Long orderId, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			String clientIp = RequestUtils.getClientIp(request);
			return payService.preWeixinPay(appid, orderId, clientIp);
		});
	}
	
	@RequestMapping("/notify")
	@ResponseBody
	public Object notify(@RequestBody String xml) {
		return ResponseHandler.doResponse(() -> {
			log.info("notify(..) => request={}", xml);
			String result = payService.notify(xml);
			log.info("notify(..) => response={}", result);
			return result;
		});
	}
}
