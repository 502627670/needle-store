package org.needle.bookingdiscount.admin.repository;

import java.util.List;

import org.needle.bookingdiscount.domain.order.OrderGoods;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderGoodsRepository extends PagingAndSortingRepository<OrderGoods, Long> {
	
	@Query("from OrderGoods t where t.order.id=?1 ")
	public List<OrderGoods> findByOrder(Long orderId);
	
}
