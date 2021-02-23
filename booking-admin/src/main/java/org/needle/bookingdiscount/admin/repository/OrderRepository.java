package org.needle.bookingdiscount.admin.repository;

import org.needle.bookingdiscount.domain.order.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

}
