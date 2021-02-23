package org.needle.bookingdiscount.server.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.needle.bookingdiscount.domain.member.Footprint.JsonFootprint;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.data.DataPage;
import org.needle.bookingdiscount.server.security.SecurityContext;
import org.needle.bookingdiscount.server.service.FootprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/footprint")
public class FootprintController {
	
	@Autowired
	private FootprintService footprintService;
	
	@Autowired
	private SecurityContext securityContext;
	
	//足迹列表
	@RequestMapping("/list")
	@ResponseBody
	public Object list(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestParam(defaultValue="1") int page, 
			@RequestParam(defaultValue="20") int size,
			HttpServletRequest request) {
		return ResponseHandler.doResponse(() -> {
			int pageIndex = page > 0 ? page - 1 : page;
			Optional<MemberUser> userOpt = securityContext.getUserByToken(token);
			List<JsonFootprint> dataList = userOpt.isPresent() ? 
					footprintService.list(userOpt.get().getId(), pageIndex, size) : new ArrayList<JsonFootprint>();
			return new DataPage(page, dataList);
		});
	}
	
	//删除足迹
	@RequestMapping("/delete")
	@ResponseBody
	public Object delete(@RequestHeader(name="X-Nideshop-Token") String token, 
			@RequestBody Map<String,Object> data, HttpServletRequest request) {
		Long footprintId = Long.valueOf(data.get("footprintId").toString());
		return ResponseHandler.doResponse(() -> {
			Optional<MemberUser> userOpt = securityContext.getUserByToken(token);
			return userOpt.isPresent() ? 
					footprintService.delete(userOpt.get().getId(), footprintId) : "未登录";
		});
	}
	
}
