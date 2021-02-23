package org.needle.bookingdiscount.server.repository.product;

import java.util.List;

import org.needle.bookingdiscount.domain.product.GoodsGallery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GoodsGalleryRepository extends PagingAndSortingRepository<GoodsGallery, Long> {
	
	@Query("from GoodsGallery t where t.goods.id=?1 and t.isDelete=false order by t.sortOrder")
	public List<GoodsGallery> findByGoods(Long goodsId, Pageable pageable);
	
}
