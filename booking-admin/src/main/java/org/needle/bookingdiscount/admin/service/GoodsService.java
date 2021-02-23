package org.needle.bookingdiscount.admin.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.needle.bookingdiscount.admin.cart.CartRepository;
import org.needle.bookingdiscount.admin.repository.GoodsGalleryRepository;
import org.needle.bookingdiscount.admin.repository.GoodsRepository;
import org.needle.bookingdiscount.admin.repository.GoodsSpecificationRepository;
import org.needle.bookingdiscount.admin.repository.ProductRepository;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.domain.product.GoodsGallery;
import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.domain.product.Specification;
import org.needleframe.core.exception.ServiceException;
import org.needleframe.core.model.ActionData;
import org.needleframe.core.model.Module;
import org.needleframe.core.service.AbstractDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class GoodsService extends AbstractDataService {

	@Autowired
	private GoodsRepository goodsRepository;
	
	@Autowired
	private GoodsGalleryRepository goodsGalleryRepository;
		
	@Autowired
	private GoodsSpecificationRepository goodsSpecificationRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Override
	protected Class<?> getModelClass() {
		return Goods.class;
	}
	
	public JsonGoods save(Goods goods, List<String> galleryImgs, Specification specification, List<Product> products) {
        Long id = goods.getId();
        goods.setHttpsPicUrl(goods.getListPicUrl());
        goods.setKeywords("");
        goods.setIsDelete(false);
        
        if(goods.getIsOnSale() == null) {
        	goods.setIsOnSale(false);
        }
        
        if(id != null && id > 0) {
        	goodsRepository.save(goods);
        	saveGalleryImages(goods, galleryImgs);
        	
        	int check = Boolean.TRUE.equals(goods.getIsOnSale()) ? 1 : 0;
        	cartRepository.updateByGoods(check, goods.getIsOnSale(), goods.getListPicUrl(), goods.getFreightTemplateId(), goods.getId());
        	
        	productRepository.findByGoods(goods.getId()).forEach(p -> {
        		p.setIsDelete(true);
        		productRepository.save(p);
        	});
        	
            goodsSpecificationRepository.findByGoods(goods.getId()).forEach(gs -> {
            	gs.setIsDelete(true);
            	goodsSpecificationRepository.save(gs);
            });
            
            for(Product item : products) {
            	String goodsSpecificationNameValue = item.getValue();
        		String goodsSn = item.getGoodsSn();
            	if(item.getId() != null && item.getId() > 0) {
            		cartRepository.updateByProduct(item.getRetailPrice(), goodsSpecificationNameValue, goodsSn, item.getId());
            		
            		item.setGoods(goods);
            		item.setIsDelete(false);
            		
            		List<Product> exists = productRepository.findByGoodsSn(goodsSn);
            		if(item.getId() != null && !exists.isEmpty() && exists.get(0).getId().intValue() != item.getId().intValue()) {
            			throw new ServiceException("商品SKU已存在");
            		}
            		
            		productRepository.save(item);
            		
            		Optional<GoodsSpecification> goodsSpecificationOpt = 
            			goodsSpecificationRepository.findById(Long.valueOf(item.getGoodsSpecificationIds()));
            		
            		if(goodsSpecificationOpt.isPresent()) {
            			GoodsSpecification gs = goodsSpecificationOpt.get();
            			gs.setGoods(goods);
            			gs.setValue(goodsSpecificationNameValue);
            			gs.setSpecification(specification);
            			gs.setIsDelete(false);
            			goodsSpecificationRepository.save(gs);
            		}
            	}
            	else {
            		List<Product> exists = productRepository.findByGoodsSn(goodsSn);
            		if(!exists.isEmpty()) {
            			throw new ServiceException("商品SKU已存在");
            		}
            		
            		GoodsSpecification gs = new GoodsSpecification();
            		gs.setGoods(goods);
            		gs.setValue(goodsSpecificationNameValue);
            		gs.setSpecification(specification);
            		gs.setIsDelete(false);
            		gs.setPicUrl("");
            		goodsSpecificationRepository.save(gs);
            		
            		item.setGoodsSpecificationIds(gs.getId().toString());
            		item.setGoods(goods);
            		item.setIsDelete(false);
            		productRepository.save(item);
            	}
            }
        }
        else {
        	goods.setCostPrice("0");
        	goods.setGoodsNumber(0);
        	goods.setRetailPrice("0");
            goodsRepository.save(goods);
            saveGalleryImages(goods, galleryImgs);
            
            for(Product item : products) {
            	GoodsSpecification gs = new GoodsSpecification();
            	gs.setGoods(goods);
            	gs.setSpecification(specification);
            	gs.setValue(item.getValue());
            	gs.setPicUrl("");
            	gs.setIsDelete(false);
            	goodsSpecificationRepository.save(gs);
            	
            	item.setGoodsSpecificationIds(gs.getId().toString());
                item.setGoods(goods);
                item.setIsOnSale(true);
                gs.setIsDelete(false);
                productRepository.save(item);
            }
        }
        
        List<Product> productList = productRepository.findByGoodsAndIsOnSaleAndIsDelete(goods.getId(), true, false);
        int goodsNum = 0;
        BigDecimal maxRetailPrice = new BigDecimal(0);
        BigDecimal minRetailPrice = productList.isEmpty() ? new BigDecimal(0) : new BigDecimal(Integer.MAX_VALUE);
        BigDecimal maxCost = new BigDecimal(0);
        BigDecimal minCost = new BigDecimal(0);
        for(Product p : productList) {
        	goodsNum += p.getGoodsNumber();
        	if(maxRetailPrice.doubleValue() < p.getRetailPrice().doubleValue()) {
        		maxRetailPrice = p.getRetailPrice();
        	}
        	if(minRetailPrice.doubleValue() > p.getRetailPrice().doubleValue()) {
        		minRetailPrice = p.getRetailPrice();
        	}
        	
        	if(maxCost.doubleValue() < p.getCost().doubleValue()) {
        		maxCost = p.getCost();
        	}
        	if(minCost.doubleValue() > p.getCost().doubleValue()) {
        		minCost = p.getCost();
        	}
        }
        
        String goodsPrice = minRetailPrice.toString();
        if(maxRetailPrice.subtract(minRetailPrice).doubleValue() > 0.01) {
        	goodsPrice = minRetailPrice.toString() + '~' + maxRetailPrice.toString();
        }
        String costPrice = minCost.toString() + '~' + maxCost.toString();
        
        goods.setGoodsNumber(goodsNum);
        goods.setRetailPrice(goodsPrice);
        goods.setCostPrice(costPrice);
        goods.setMinRetailPrice(minRetailPrice);
        goods.setMinCostPrice(minCost);
        goodsRepository.save(goods);
        
        return new JsonGoods(goods);
	}

//	public JsonGoods save(Goods goods, Specification specification, List<Product> products) {
//        Long id = goods.getId();
//        
//        if(id != null && id > 0) {
//        	goodsRepository.save(goods);
//        	int check = Boolean.TRUE.equals(goods.getIsOnSale()) ? 1 : 0;
//        	cartRepository.updateByGoods(check, goods.getIsOnSale(), goods.getListPicUrl(), goods.getFreightTemplateId(), goods.getId());
//        	
//        	productRepository.findByGoods(goods.getId()).forEach(p -> {
//        		p.setIsDelete(true);
//        		productRepository.save(p);
//        	});
//        	
//            goodsSpecificationRepository.findByGoods(goods.getId()).forEach(gs -> {
//            	gs.setIsDelete(true);
//            	goodsSpecificationRepository.save(gs);
//            });
//            
//            for(Product item : products) {
//            	String goodsSpecificationNameValue = item.getValue();
//        		String goodsSn = item.getGoodsSn();
//            	if(item.getId() != null && item.getId() > 0) {
//            		cartRepository.updateByProduct(item.getRetailPrice(), goodsSpecificationNameValue, goodsSn, item.getId());
//            		item.setIsDelete(false);
//            		productRepository.save(item);
//            		
//            		Optional<GoodsSpecification> goodsSpecificationOpt = 
//            			goodsSpecificationRepository.findById(Long.valueOf(item.getGoodsSpecificationIds()));
//            		
//            		if(goodsSpecificationOpt.isPresent()) {
//            			GoodsSpecification gs = goodsSpecificationOpt.get();
//            			gs.setValue(goodsSpecificationNameValue);
//            			gs.setSpecification(specification);
//            			gs.setIsDelete(false);
//            			goodsSpecificationRepository.save(gs);
//            		}
//            	}
//            	else {
//            		GoodsSpecification gs = new GoodsSpecification();
//            		gs.setValue(goodsSpecificationNameValue);
//            		gs.setGoods(goods);
//            		gs.setSpecification(specification);
//            		gs.setIsDelete(false);
//            		goodsSpecificationRepository.save(gs);
//            		
//            		item.setGoodsSpecificationIds(gs.getId().toString());
//            		item.setGoods(goods);
//            		productRepository.save(item);
//            	}
//            }
//        }
//        else {
//            goodsRepository.save(goods);
//            
//            for(Product item : products) {
//            	GoodsSpecification gs = new GoodsSpecification();
//            	gs.setGoods(goods);
//            	gs.setSpecification(specification);
//            	gs.setValue(item.getValue());
//            	goodsSpecificationRepository.save(gs);
//            	
//            	item.setGoodsSpecificationIds(gs.getId().toString());
//                item.setGoods(goods);
//                item.setIsOnSale(true);
//                productRepository.save(item);
//            }
//        }
//        
//        List<Product> productList = productRepository.findByGoodsAndIsOnSaleAndIsDelete(goods.getId(), true, false);
//        int goodsNum = 0;
//        BigDecimal maxRetailPrice = new BigDecimal(0);
//        BigDecimal minRetailPrice = new BigDecimal(Integer.MAX_VALUE);
//        BigDecimal maxCost = new BigDecimal(0);
//        BigDecimal minCost = new BigDecimal(0);
//        for(Product p : productList) {
//        	goodsNum += p.getGoodsNumber();
//        	if(maxRetailPrice.doubleValue() < p.getRetailPrice().doubleValue()) {
//        		maxRetailPrice = p.getRetailPrice();
//        	}
//        	if(minRetailPrice.doubleValue() > p.getRetailPrice().doubleValue()) {
//        		minRetailPrice = p.getRetailPrice();
//        	}
//        	
//        	if(maxCost.doubleValue() < p.getCost().doubleValue()) {
//        		maxCost = p.getCost();
//        	}
//        	if(minCost.doubleValue() > p.getCost().doubleValue()) {
//        		minCost = p.getCost();
//        	}
//        }
//        
//        String goodsPrice = minRetailPrice.toString();
//        if(maxRetailPrice.subtract(minRetailPrice).doubleValue() > 0.01) {
//        	goodsPrice = minRetailPrice.toString() + '~' + maxRetailPrice.toString();
//        }
//        String costPrice = minCost.toString() + '~' + maxCost.toString();
//        
//        goods.setGoodsNumber(goodsNum);
//        goods.setRetailPrice(goodsPrice);
//        goods.setCostPrice(costPrice);
//        goods.setMinRetailPrice(minRetailPrice);
//        goods.setMinCostPrice(minCost);
//        goodsRepository.save(goods);
//        
//        return new JsonGoods(goods);
//	}
	
	private void saveGalleryImages(Goods goods, List<String> galleryImgs) {
		if(goods.getId() != null) {
			List<GoodsGallery> persistList = goodsGalleryRepository.findByGoods(goods);
			persistList.forEach(gg -> {
				gg.setIsDelete(true);
				goodsGalleryRepository.save(gg);
			});
		}
		for(int i = 0; i < galleryImgs.size(); i++) {
			String img = galleryImgs.get(i);
			GoodsGallery gg = new GoodsGallery();
			gg.setGoods(goods);
			gg.setImgUrl(img);
			gg.setIsDelete(false);
			gg.setImgDesc("");
			gg.setSortOrder(i + 1);
			goodsGalleryRepository.save(gg);
		}
	}
	
	@Override
	protected void afterCreate(Module module, List<ActionData> dataList) {
		super.afterCreate(module, dataList);
		
		dataList.forEach(actionData -> {
			actionData.getData().forEach(data -> {
				Goods goods = module.fromData(data);
				List<GoodsGallery> persistList = goodsGalleryRepository.findByGoods(goods);
				persistList.forEach(gg -> {
					goodsGalleryRepository.delete(gg);
				});
				
				String galleryImgs = (String) data.get("galleryImgs");
				if(StringUtils.hasText(galleryImgs)) {
					String[] array = galleryImgs.split(",");
					
					for(int i = 0; i < array.length; i++) {
						if(StringUtils.hasText(array[i])) {
							GoodsGallery gg = new GoodsGallery();
							gg.setGoods(goods);
							gg.setImgUrl(array[i]);
							gg.setIsDelete(false);
							gg.setSortOrder(i + 1);
							goodsGalleryRepository.save(gg);
						}
					}
				}
			});
		});
	}

	@Override
	protected void afterUpdate(Module module, List<Map<String, Object>> dataList) {
		super.afterUpdate(module, dataList);
		
		dataList.forEach(data -> {
			Goods goods = module.fromData(data);
			List<GoodsGallery> persistList = goodsGalleryRepository.findByGoods(goods);
			persistList.forEach(gg -> {
				goodsGalleryRepository.delete(gg);
			});
			
			String galleryImgs = (String) data.get("galleryImgs");
			if(StringUtils.hasText(galleryImgs)) {
				String[] array = galleryImgs.split(",");
				
				for(int i = 0; i < array.length; i++) {
					if(StringUtils.hasText(array[i])) {
						GoodsGallery gg = new GoodsGallery();
						gg.setGoods(goods);
						gg.setImgUrl(array[i]);
						gg.setIsDelete(false);
						gg.setImgDesc("");
						gg.setSortOrder(i + 1);
						goodsGalleryRepository.save(gg);
					}
				}
			}
		});
	}
	
}
