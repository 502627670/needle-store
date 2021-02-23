package org.needle.bookingdiscount.server.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.product.Category;
import org.needle.bookingdiscount.domain.product.Category.JsonCategory;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.domain.support.Ad;
import org.needle.bookingdiscount.domain.support.Ad.JsonAd;
import org.needle.bookingdiscount.domain.support.Notice;
import org.needle.bookingdiscount.domain.support.Notice.JsonNotice;
import org.needle.bookingdiscount.server.repository.product.CategoryRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsRepository;
import org.needle.bookingdiscount.server.repository.support.AdRepository;
import org.needle.bookingdiscount.server.repository.support.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
@Transactional
public class HomeService {
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private AdRepository adRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private GoodsRepository goodsRepository;
	
	public ModelMap getHomeData() {
		List<Ad> bannerList = adRepository.findByEnabledAndIsDelete(true, false);
		List<Notice> noticeList = noticeRepository.findByIsDelete(false);
		List<Category> channelList = categoryRepository.findByIsChannelAndParent(true, 0L);
		List<Category> categoryList = categoryRepository.findByIsShowAndParent(true, 0L);
		List<Map<String,Object>> newCategoryList = new ArrayList<Map<String,Object>>();
		categoryList.forEach(category -> {
			List<Goods> categoryGoods = goodsRepository.findGoods(category.getId(), 0, true, true, false);
			Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
			dataMap.put("id", category.getId());
			dataMap.put("name", category.getName());
			dataMap.put("goodsList", categoryGoods.stream().map(goods -> new JsonGoods(goods)).collect(Collectors.toList()));
			dataMap.put("banner", category.getImgUrl());
			dataMap.put("height", category.getPHeight());
			newCategoryList.add(dataMap);
		});
		
		ModelMap model = new ModelMap();
		model.addAttribute("channel", channelList.stream().map(c -> new JsonCategory(c)).collect(Collectors.toList()));
		model.addAttribute("banner", bannerList.stream().map(ad -> new JsonAd(ad)).collect(Collectors.toList()));
		model.addAttribute("notice", noticeList.stream().map(n -> new JsonNotice(n)).collect(Collectors.toList()));
		model.addAttribute("categoryList", newCategoryList);
		return model;
	}
	
}
