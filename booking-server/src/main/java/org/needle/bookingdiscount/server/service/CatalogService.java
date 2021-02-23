package org.needle.bookingdiscount.server.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.product.Category;
import org.needle.bookingdiscount.domain.product.Category.JsonCategory;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.server.repository.product.CategoryRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
@Transactional
public class CatalogService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private GoodsRepository goodsRepository;
	
	public ModelMap getIndexData(Long id) {
		Pageable pageable = PageRequest.of(0, 10);
		List<Category> data = categoryRepository.findByIsCategoryAndParentId(true, 0L, pageable);
		Category currentCategory = null;
		if(id != null) {
			Optional<Category> current = categoryRepository.findById(id);
			currentCategory = current.isPresent() ? current.get() : null;
		}
		if(currentCategory == null && !data.isEmpty()) {
			currentCategory = data.get(0);
		}
		
//		if(currentCategory != null) {
//			categoryList.add(new JsonCategory(currentCategory));
//		}
		return new ModelMap().addAttribute("categoryList", data.stream().map(c -> new JsonCategory(c)).collect(Collectors.toList()));
	}
	
	public ModelMap getCurrentData(Long id) {
		Category currentCategory = null;
		if(id != null) {
			Optional<Category> current = categoryRepository.findById(id);
			currentCategory = current.isPresent() ? current.get() : null;
		}
		ModelMap model = new ModelMap();
		if(currentCategory != null) {
			model.addAttribute("id", currentCategory.getId());
			model.addAttribute("name", currentCategory.getName());
			model.addAttribute("img_url", currentCategory.getImgUrl());
			model.addAttribute("p_height", currentCategory.getParentId());
		}
        return model;
	}
	
	public List<JsonGoods> getCurrentListData(int page, int size, Long id) {
        Long categoryId = id;
        Pageable pageable = PageRequest.of(page, size);
        if (categoryId == null || categoryId == 0) {
        	List<Goods> list = goodsRepository.findByIsOnSaleAndIsDelete(true, false, pageable);
        	return list.stream().map(g -> new JsonGoods(g)).collect(Collectors.toList());
        } 
        else {
        	List<Goods> list = goodsRepository.findByCategoryAndIsOnSaleAndIsDelete(categoryId, true, false, pageable);
        	return list.stream().map(g -> new JsonGoods(g)).collect(Collectors.toList());
        }
	}
	
}
