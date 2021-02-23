package org.needle.bookingdiscount.admin.repository;

import java.util.List;

import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.GoodsGallery;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GoodsGalleryRepository extends PagingAndSortingRepository<GoodsGallery, Long> {
	
	public List<GoodsGallery> findByGoods(Goods goods);
	
}
