package org.needle.bookingdiscount.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.member.Footprint;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.member.SearchHistory;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.domain.product.GoodsGallery;
import org.needle.bookingdiscount.domain.product.GoodsGallery.JsonGoodsGallery;
import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.needle.bookingdiscount.domain.product.GoodsSpecification.JsonGoodsSpecification;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.domain.product.Product.JsonProduct;
import org.needle.bookingdiscount.domain.product.Specification;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.member.FootprintRepository;
import org.needle.bookingdiscount.server.repository.member.SearchHistoryRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsGalleryRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsSpecificationRepository;
import org.needle.bookingdiscount.server.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class GoodsService {
		
	@Autowired
	private GoodsRepository goodsRepository;
	
	@Autowired
	private GoodsGalleryRepository goodsGalleryRepository;
	
	@Autowired
	private FootprintRepository footprintRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private GoodsSpecificationRepository goodsSpecificationRepository;
	
	@Autowired
	private SearchHistoryRepository searchHistoryRepository;
	
	//统计商品总数
	public ModelMap count() {
		long count = goodsRepository.countByIsOnSaleAndIsDelete(true, false);
		return new ModelMap().addAttribute("goodsCount", count);
	}
	
	//获得商品的详情
	public ModelMap detail(Long id, Long userId) {
		Long goodsId = id;
		Optional<Goods> goodsOpt = goodsRepository.findById(goodsId);
		if(!goodsOpt.isPresent() || Boolean.TRUE.equals(goodsOpt.get().getIsDelete())) {
			throw new ServiceException("商品不存在");
		}
		Goods info = goodsOpt.get();
		if(Boolean.FALSE.equals(info.getIsOnSale())) {
			throw new ServiceException("商品已下架");
		}
		
		List<GoodsGallery> gallery = goodsGalleryRepository.findByGoods(goodsId, PageRequest.of(0, 6));
		if(userId != null) {
			MemberUser user = new MemberUser(userId);
	        Footprint footprint = new Footprint();
	        footprint.setUser(user);
	        footprint.setGoods(info);
	        footprint.setAddTime((int)(new Date().getTime() / 1000)); 
	        footprintRepository.save(footprint);
		}
		
        List<Product> productList = productRepository.findByGoods(goodsId);
        int goodsNumber = 0;
        for(Product item : productList) {
            if(item.getGoodsNumber() > 0) {
                goodsNumber = goodsNumber + item.getGoodsNumber();
            }
        }
        info.setGoodsNumber(goodsNumber);
        ModelMap specificationList = getSpecificationList(goodsId);
        ModelMap model = new ModelMap();
        model.addAttribute("info", new JsonGoods(info));
        model.addAttribute("gallery", gallery.stream().map(g -> new JsonGoodsGallery(g)).collect(Collectors.toList()));
        model.addAttribute("specificationList", specificationList);
        model.addAttribute("productList", productList.stream().map(p -> new JsonProduct(p)).collect(Collectors.toList()));
        return model;
	}
	
	//获得商品列表
	public List<JsonGoods> list(Long userId, String keyword, String sort, String order, String sales) {
		boolean isOnSale = true;
		boolean isDelete = false;
		List<Goods> goodsData = new ArrayList<Goods>();
		Pageable pageable = PageRequest.of(0, 100);
		if(StringUtils.hasText(keyword)) {
			if(userId != null) {
				SearchHistory sh = new SearchHistory();
				sh.setUser(new MemberUser(userId));
				sh.setFrom("");
				sh.setKeyword(keyword);
				sh.setAddTime((int) (new Date().getTime() / 1000));
				searchHistoryRepository.save(sh);
			}
			
			keyword = "%" + keyword + "%";
			if("price".equals(sort)) {
				goodsData = "asc".equals(order.trim().toLowerCase()) ? 
						goodsRepository.findByIsOnSaleAndIsDeleteAndKeywordOrderByPriceAsc(isOnSale, isDelete, keyword, pageable) : 
						goodsRepository.findByIsOnSaleAndIsDeleteAndKeywordOrderByPriceDesc(isOnSale, isDelete, keyword, pageable);
			}
			else if("sales".equals(sort)) {
				goodsData = "asc".equals(sales.trim().toLowerCase()) ? 
						goodsRepository.findByIsOnSaleAndIsDeleteAndKeywordOrderBySaleAsc(isOnSale, isDelete, keyword, pageable) : 
						goodsRepository.findByIsOnSaleAndIsDeleteAndKeywordOrderBySaleDesc(isOnSale, isDelete, keyword, pageable);
			}
			else {
				goodsData = goodsRepository.findByIsOnSaleAndIsDeleteAndKeyword(isOnSale, isDelete, keyword, pageable);
			}
		}
		else {
			if("price".equals(sort)) {
				goodsData = "asc".equals(order.trim().toLowerCase()) ? 
						goodsRepository.findByIsOnSaleAndIsDeleteOrderByPriceAsc(isOnSale, isDelete, pageable) : 
						goodsRepository.findByIsOnSaleAndIsDeleteOrderByPriceDesc(isOnSale, isDelete, pageable);
			}
			else if("sales".equals(sort)) {
				goodsData = "asc".equals(sales.trim().toLowerCase()) ? 
						goodsRepository.findByIsOnSaleAndIsDeleteOrderBySaleAsc(isOnSale, isDelete, pageable) : 
						goodsRepository.findByIsOnSaleAndIsDeleteOrderBySaleDesc(isOnSale, isDelete, pageable);
			}
			else {
				goodsData = goodsRepository.findByIsOnSaleAndIsDelete(isOnSale, isDelete, pageable);
			}
		}
		
		return goodsData.stream().map(g -> new JsonGoods(g)).collect(Collectors.toList());
	}
	
	public JsonGoods goodsShare(Long id) {
		Goods info = goodsRepository.findById(id).get();
		return new JsonGoods(info);
	}
	
	public void saveUserId() {
		
	}
	
	private ModelMap getSpecificationList(Long goodsId) {
		List<GoodsSpecification> gsInfo = goodsSpecificationRepository.findByGoods(goodsId);
        for(GoodsSpecification item : gsInfo) {
        	List<Product> gsProductList = productRepository.findByGoodsSpecificationIds(item.getId().toString());
        	if(gsProductList.size() > 0) {
        		Product product = gsProductList.get(0);
        		item.setGoodsNumber(product.getGoodsNumber());
        	}
        }
        
        ModelMap model = new ModelMap();
        Long spec_id = null;
        String name = "";
        
    	for(GoodsSpecification gs : gsInfo) {
    		try {
    			Specification specification = gs.getSpecification();
        		spec_id = specification.getId();
                name = specification.getName();
    		}
    		catch(Exception e) {}
    	}
        
        model.addAttribute("specification_id", spec_id);
        model.addAttribute("name", name);
        model.addAttribute("valueList", gsInfo.stream().map(i -> new JsonGoodsSpecification(i)).collect(Collectors.toList()));
        return model;
	}
}
