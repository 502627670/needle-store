package org.needle.bookingdiscount.server.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/address")
public class AddressController {
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private SecurityContext securityContext;
	
	// 收货地址详情
	@RequestMapping("/addressDetail")
	@ResponseBody
	public Object addressDetail(@RequestHeader(name="X-Nideshop-Token") String token, 
			Long id, HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return addressService.addressDetail(user.getId(), id);
		});
	}
	
	// 保存收货地址
	@RequestMapping("/deleteAddress")
	@ResponseBody
	public Object deleteAddress(@RequestHeader(name="X-Nideshop-Token") String token, Long id, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
				.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return addressService.deleteAddress(user.getId(), id);
		});
	}
	
	// 保存收货地址
	@RequestMapping("/saveAddress")
	@ResponseBody
	public Object saveAddress(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Object idObject = data.get("id");
		Long id = idObject == null ? null : Long.valueOf(idObject.toString());
		String name = (String) data.get("name");
		String mobile = (String) data.get("mobile");
		Long province_id = Long.valueOf(data.get("province_id").toString());
		Long city_id = Long.valueOf(data.get("city_id").toString());
		Long district_id = Long.valueOf(data.get("district_id").toString());
		String address = (String) data.get("address");
		Integer is_default = (Integer) data.get("is_default");
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return addressService.saveAddress(user.getId(), id, name, mobile, province_id, city_id, 
					district_id, address, 1 == is_default ? true : false);
		});
	}
	
	// 查詢收货地址
	@RequestMapping("/getAddresses")
	@ResponseBody
	public Object getAddresses(@RequestHeader(name="X-Nideshop-Token") String token, 
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			MemberUser user = securityContext.getUserByToken(token)
					.orElseThrow(() -> new ServiceException(401, "用户未登录"));
			return addressService.getAddresses(user.getId());
		});
	}
	
}
