package org.needle.bookingdiscount.server.repository.order;

import java.util.List;

import org.needle.bookingdiscount.domain.order.OrderExpress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderExpressRepository extends PagingAndSortingRepository<OrderExpress, Long> {
	
	@Query("from OrderExpress t where t.order.id=?1 ")
	public List<OrderExpress> findByOrder(Long orderId);
	
}
