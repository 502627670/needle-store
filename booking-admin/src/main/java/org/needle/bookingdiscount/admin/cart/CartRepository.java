package org.needle.bookingdiscount.admin.cart;

import java.math.BigDecimal;
import java.util.List;

import org.needle.bookingdiscount.domain.cart.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CartRepository extends PagingAndSortingRepository<Cart, Long> {
	
	@Query("from Cart t where t.goods.id=?1 order by t.addTime")
	public List<Cart> findByGoodsId(Long goodsId, Pageable pageable);
	
	@Query("from Cart t where t.product.id=?1 order by t.addTime")
	public List<Cart> findByProductId(Long productId, Pageable pageable);
		
	@Modifying
	@Query("update Cart t set t.checked=?1, t.isOnSale=?2, t.listPicUrl=?3, t.freightTemplateId=?4 where t.goods.id=?5")
	public void updateByGoods(int checked, boolean isOnSale, String listPicUrl, Long freightTemplateId, Long goodsId);
	
	@Modifying
	@Query("update Cart t set t.retailPrice=?1, goodsSpecificationNameValue=?2, goods_sn=?3 where t.product.id=?4")
	public void updateByProduct(BigDecimal retailPrice, String goodsSpecificationNameValue, String goodsSn, Long productId);
	
}