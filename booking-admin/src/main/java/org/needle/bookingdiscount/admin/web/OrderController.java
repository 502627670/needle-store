package org.needle.bookingdiscount.admin.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.needle.bookingdiscount.admin.service.MemberUserService;
import org.needle.bookingdiscount.admin.service.OrderService;
import org.needle.bookingdiscount.admin.service.RegionService;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.order.Order;
import org.needle.bookingdiscount.domain.order.OrderGoods.JsonOrderGoods;
import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.needle.bookingdiscount.domain.product.Specification;
import org.needle.bookingdiscount.domain.support.Region;
import org.needle.bookingdiscount.utils.Base64Utils;
import org.needleframe.core.model.Module;
import org.needleframe.core.web.AbstractDataController;
import org.needleframe.core.web.response.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/data")
public class OrderController extends AbstractDataController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MemberUserService memberUserService;
	
	@Autowired
	private RegionService regionService;
	
	@Override
	protected Module getModule(String moduleName) {
		return appContextService.getModuleContext().getModule(Order.class);
	}

	@RequestMapping("/order/get")
	@ResponseBody
	public ResponseMessage getData(String id, HttpServletRequest request,
			HttpServletResponse response) {
		return super.getData("order", id, request, response);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/order/list")
	@ResponseBody
	public ResponseMessage findList(String[] _vp, String _vf, String _sf, int _page, int _size,
			String _sort, String _direction, HttpServletRequest request, HttpServletResponse response) {
		ResponseMessage responseMessage = super.findList("order", _vp, _vf, _sf, _page, _size, 
				_sort, _direction, request, response);
		if(responseMessage.isSuccess()) {
			Page<Map<String,Object>> dataPage = (Page<Map<String,Object>>) responseMessage.getData();
			dataPage.getContent().forEach(data -> {
				Long id = Long.valueOf(data.get("id").toString());
				List<JsonOrderGoods> orderGoods = orderService.findGoodsByOrder(id);
				data.put("orderGoods", orderGoods);
				
				orderGoods.forEach(og -> {
					String sid = og.getGoods_specifition_ids();
					GoodsSpecification gs = orderService.getGoodsSpecificationById(Long.valueOf(sid));
					if(gs != null) {
						Specification s = gs.getSpecification();
						og.setSpecificationName(s.getName());
					}
				});
				
				Region province = regionService.findById(Long.valueOf(data.get("province").toString()));
				Region city = regionService.findById(Long.valueOf(data.get("city").toString()));
				Region district = regionService.findById(Long.valueOf(data.get("district").toString()));
				data.put("full_region", String.join("", province.getName(), city.getName(), district.getName()));
				
				Object userIdObject = data.get("user");
				if(userIdObject != null) {
					Long userId = Long.valueOf(userIdObject.toString());
					MemberUser user = memberUserService.getById(userId);
					data.put("userNickname", Base64Utils.decode(user.getNickname()));
					data.put("username", user.getName()); 
					data.put("userAvatar", user.getAvatar());
					data.put("userMobile", user.getMobile());
				}
			});
		}
		return responseMessage;
	}
	
}
