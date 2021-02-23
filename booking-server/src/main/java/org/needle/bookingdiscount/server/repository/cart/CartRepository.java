package org.needle.bookingdiscount.server.repository.cart;

import java.util.List;

import org.needle.bookingdiscount.domain.cart.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CartRepository extends PagingAndSortingRepository<Cart, Long> {
	
	@Query("from Cart t where t.user.id=?1 and t.isFast=?2 and t.isDelete=?3 order by t.id desc")
	public List<Cart> findUserCarts(Long userId, Boolean isFast, Boolean isDelete);
	
	@Query("from Cart t where t.user.id=?1 and t.isDelete=?2 ")
	public List<Cart> findByUserAndIsDelete(Long userId, Boolean isDelete);
	
	@Query("from Cart t where t.user.id=?1 and t.isDelete=?2 and t.isFast=?3 ")
	public List<Cart> findByUserAndIsDeleteAndIsFast(Long userId, Boolean isDelete, Boolean isFast);
	
	@Query("from Cart t where t.user.id=?1 and t.product.id in (?2) and t.isDelete=false")
	public List<Cart> findByUserAndProductIn(Long userId, List<Long> productIds);
	
	@Query("from Cart t where t.user.id=?1 and t.checked=?2 and t.isDelete=?3 ")
	public List<Cart> findByUserAndCheckedAndIsDelete(Long userId, int checked, Boolean isDelete);
	
	@Query("from Cart t where t.user.id=?1 and t.checked=?2 and t.isFast=?3 and t.isDelete=?4 ")
	public List<Cart> findByUserAndCheckedAndIsFastAndIsDelete(Long userId, int checked, Boolean isFast, Boolean isDelete);
	
	@Query("from Cart t where t.user.id=?1 and t.product.id=?2 and t.isDelete=false")
	public Cart getByUserAndProduct(Long userId, Long productId);
	
	@Query("from Cart t where t.user.id=?1 and t.product.id=?2 and t.checked=?3 and t.isDelete=false")
	public Cart getByUserAndProductAndChecked(Long userId, Long productId, int checked);
	
}
