package org.needle.bookingdiscount.server.repository.order;

import java.util.List;

import org.needle.bookingdiscount.domain.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
	
	@Query("from Order t where t.orderSn=?1")
	public Order getByOrderSn(String orderSn);
	
	@Query("from Order t where t.user.id=?1 and t.isDelete=?2 and t.orderType<?3 and t.orderStatus in (?4) order by t.addTime desc")
	public List<Order> findByUserAndIsDeleteAndOrderTypeLessThanAndOrderStatusIn(Long userId, boolean isDelete, 
			int orderType, List<Integer> orderStatus, Pageable pageable);
	
	@Query("select count(t) from Order t where t.user.id=?1 and t.isDelete=?2 and t.orderType<?3 and t.orderStatus in (?4)")
	public Long countByUserAndIsDeleteAndOrderTypeLessThanAndOrderStatusIn(Long userId, boolean isDelete, int orderType, List<Integer> orderStatus);
	
	@Query("select count(t) from Order t where t.user.id=?1 and t.isDelete=?2 and t.orderStatus in (?3)")
	public Long countByUserAndIsDeleteAndOrderStatus(Long userId, Boolean isDelete, List<Integer> orderStatus);
	
}
