package org.needle.bookingdiscount.admin.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.needle.bookingdiscount.admin.service.GoodsService;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.domain.product.Specification;
import org.needle.bookingdiscount.utils.JsonUtils;
import org.needleframe.core.exception.ServiceException;
import org.needleframe.core.model.ActionData;
import org.needleframe.core.model.Module;
import org.needleframe.core.model.ViewProp;
import org.needleframe.core.web.AbstractDataController;
import org.needleframe.core.web.handler.DataHandler;
import org.needleframe.core.web.handler.ResponseHandler;
import org.needleframe.core.web.response.DataModule;
import org.needleframe.core.web.response.DataModuleBuilder;
import org.needleframe.core.web.response.ResponseMessage;
import org.needleframe.core.web.response.ResponseModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping("/data")
public class GoodsController extends AbstractDataController {
	
	@Autowired
	private GoodsService goodsService;
	
	@Override
	protected Module getModule(String moduleName) {
		return appContextService.getModuleContext().getModule(Goods.class);
	}
	
	@RequestMapping("/goods/get")
	@ResponseBody
	public ResponseMessage getData(String id, HttpServletRequest request,
			HttpServletResponse response) {
		return ResponseHandler.doResponse(() -> {
			Module module = getModule("goods");
			DataHandler dataHandler = new DataHandler(module, om);
			List<ViewProp> viewProps = dataHandler.getDefaultViewProps();
			Map<String,Object> data = dataService.get(module, viewProps, id);
			DataModule dataModule = new DataModuleBuilder(module).build(viewProps);
			dataFormatter.format(dataModule, data);
			return ResponseModule.success(dataModule, data);
		});
	}

	@RequestMapping("/goods/create")
	@ResponseBody
	public ResponseMessage create(String _data,
			HttpServletRequest request, HttpServletResponse response) {
		return ResponseHandler.doResponse(() -> {
			Long specificationId = ServletRequestUtils.getRequiredLongParameter(request, "specificationId1");
			String galleryImgs = request.getParameter("galleryImgs");
			List<String> gimgs = new ArrayList<String>();
			if(StringUtils.hasText(galleryImgs)) {
				String[] imgs = galleryImgs.split(",");
				gimgs = Arrays.asList(imgs);
			}
			String jsonProductList = request.getParameter("productList");
			List<Product> productList = new ArrayList<Product>();
			if(StringUtils.hasText(jsonProductList)) {
				productList = JsonUtils.fromJSON(jsonProductList, new TypeReference<List<Product>>() {});
			}
			
			Module module = getModule("goods");
			DataHandler dataHandler = new DataHandler(module, om);
			List<ActionData> _dataList = dataHandler.getCreateData(_data, request);
			if(_dataList.isEmpty() || _dataList.get(0).getData().isEmpty()) {
				throw new ServiceException("请提交要保存的数据");
			}
			
			Map<String,Object> data = _dataList.get(0).getData().get(0);
			Goods goods = module.fromData(data);
			
			Specification specification = new Specification(specificationId);
			JsonGoods jsonGoods = goodsService.save(goods, gimgs, specification, productList);
			return ResponseModule.success(jsonGoods);
		});
	}
	
	// http://localhost:8080/data/goods/update?data=%5B%7B%22saleId%22%3A1%2C%22id%22%3A1%2C%22user%22%3A1%7D%5D
	@RequestMapping("/goods/update")
	@ResponseBody
	public ResponseMessage update(String _data,
			HttpServletRequest request, HttpServletResponse response) {
		return ResponseHandler.doResponse(() -> {
			Long specificationId = ServletRequestUtils.getRequiredLongParameter(request, "specificationId1");
			
			String galleryImgs = request.getParameter("galleryImgs");
			List<String> gimgs = new ArrayList<String>();
			if(StringUtils.hasText(galleryImgs)) {
				String[] imgs = galleryImgs.split(",");
				gimgs = Arrays.asList(imgs);
			}
			String jsonProductList = request.getParameter("productList");
			List<Product> productList = new ArrayList<Product>();
			if(StringUtils.hasText(jsonProductList)) {
				productList = JsonUtils.fromJSON(jsonProductList, new TypeReference<List<Product>>() {});
			}
			
			Module module = getModule("goods");
			DataHandler dataHandler = new DataHandler(module, om);
			List<Map<String,Object>> _dataList = dataHandler.getUpdateData(_data, request);
			if(_dataList.isEmpty()) {
				throw new ServiceException("请提交要更新的数据");
			}
			Map<String,Object> data = _dataList.get(0);
			Goods goods = module.fromData(data);
			
			Specification specification = new Specification(specificationId);
			JsonGoods jsonGoods = goodsService.save(goods, gimgs, specification, productList);
			return ResponseModule.success(jsonGoods);
		});
	}
	
}
