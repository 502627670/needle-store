package org.needle.bookingdiscount.server.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.server.api.handler.ResponseHandler;
import org.needle.bookingdiscount.server.data.DataPage;
import org.needle.bookingdiscount.server.data.ResponseMessage;
import org.needle.bookingdiscount.server.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
	
	@Autowired
	private CatalogService catalogService;
	
	// http://localhost:8080/catalog/index
	@RequestMapping("/index")
	@ResponseBody
	public Object index(Long id) {
		return ResponseHandler.doResponse(() -> {
			return catalogService.getIndexData(id);
		});
	}
	
	// http://localhost:8080/catalog/current
	@RequestMapping("/current")
	@ResponseBody
	public Object current(Long id) {
		return ResponseHandler.doResponse(() -> {
			return catalogService.getCurrentData(id);
		});
	}
	
	// http://localhost:8080/catalog/currentlist?page=0&size=10
	@RequestMapping("/currentlist")
	@ResponseBody
	public Object currentlist(@RequestBody Map<String,Object> data,
			HttpServletRequest request, HttpServletResponse response) {
		int page = data.get("page") == null ? 1 : (int) data.get("page");
		int size = data.get("size") == null ? 20 : (int) data.get("size");
		Long id = Long.valueOf(data.get("id").toString());
		return ResponseHandler.doResponse(() -> {
			int pageIndex = page > 0 ? page - 1 : page;
			List<JsonGoods> list = catalogService.getCurrentListData(pageIndex, size, id);
			return ResponseMessage.success(new DataPage(page, list));
		});
	}
	
}
