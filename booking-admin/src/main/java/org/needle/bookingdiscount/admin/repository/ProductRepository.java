package org.needle.bookingdiscount.admin.repository;

import java.util.List;

import org.needle.bookingdiscount.domain.product.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
	
	@Query("from Product t where t.goods.id=?1 ")
	public List<Product> findByGoods(Long goodsId);
	
	@Query("from Product t where t.goods.id=?1 and t.isOnSale=?2 and t.isDelete=?3")
	public List<Product> findByGoodsAndIsOnSaleAndIsDelete(Long goodsId, boolean isOnSale, boolean isDelete);
	
	@Query("from Product t where t.goodsSn=?1 ")
	public List<Product> findByGoodsSn(String goodsSn);
	
}
