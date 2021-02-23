package org.needle.bookingdiscount.server.repository.product;

import java.util.List;

import org.needle.bookingdiscount.domain.product.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
	
	@Query("from Product t where t.goods.id=?1 ")
	public List<Product> findByGoods(Long goodsId);
	
	@Query("from Product t where t.goodsSpecificationIds=?1 and t.isDelete=false")
	public List<Product> findByGoodsSpecificationIds(String goodsSpecificationIds);
	
	@Modifying
    @Query("update Product t set t.goodsNumber=t.goodsNumber+?1 where id=?2")
    public void increment(int goodsNumber, Long id);
	
	@Modifying
    @Query("update Product t set t.goodsNumber=t.goodsNumber-?1 where id=?2")
    public void decrement(int goodsNumber, Long id);
	
}
