package org.needle.bookingdiscount.admin.repository;

import java.util.List;

import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GoodsSpecificationRepository extends PagingAndSortingRepository<GoodsSpecification, Long> {
	
	@Query("from GoodsSpecification t where t.goods.id=?1")
	public List<GoodsSpecification> findByGoods(Long goodsId);
	
	@Query("from GoodsSpecification t where t.specification.id=?1")
	public List<GoodsSpecification> findBySpecification(Long specificationId);
	
}
