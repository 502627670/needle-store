package org.needle.bookingdiscount.server.repository.product;

import java.util.List;

import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GoodsSpecificationRepository extends PagingAndSortingRepository<GoodsSpecification, Long> {
	
	@Query("from GoodsSpecification t where t.goods.id=?1 and t.isDelete=false ")
	public List<GoodsSpecification> findByGoods(Long goodsId);
	
	@Query("from GoodsSpecification t where t.goods.id=?1 and t.isDelete=?2 and t.id in (?3) ")
	public List<GoodsSpecification> findByGoodsAndIsDeleteAndIdIn(Long goodsId, Boolean isDelete, List<Long> ids);
	
	
}
