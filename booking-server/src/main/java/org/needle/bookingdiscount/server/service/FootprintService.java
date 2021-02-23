package org.needle.bookingdiscount.server.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.needle.bookingdiscount.domain.member.Footprint;
import org.needle.bookingdiscount.domain.member.Footprint.JsonFootprint;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;
import org.needle.bookingdiscount.server.repository.member.FootprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FootprintService {
	
	@Autowired
	private FootprintRepository footprintRepository;
	
	public List<JsonFootprint> list(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		List<Footprint> footprintList = footprintRepository.findByUser(userId, pageable);
		
		List<JsonFootprint> list = new ArrayList<JsonFootprint>(); 
		for(Footprint footprint : footprintList) {
			JsonFootprint item = new JsonFootprint(footprint);
			Goods goods = footprint.getGoods();
			item.setAdd_time(new SimpleDateFormat("yyyy-MM-dd").format(new Date(footprint.getAddTime() * 1000)));
			item.setGoods(new JsonGoods(goods));
			
			list.add(item);
        }
        return list;
	}
	
	public String delete(Long userId, Long footprintId) {
		footprintRepository.deleteById(footprintId);
	     return "删除成功";
	}
	
}
