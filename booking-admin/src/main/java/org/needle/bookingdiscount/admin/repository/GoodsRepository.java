package org.needle.bookingdiscount.admin.repository;

import org.needle.bookingdiscount.domain.product.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GoodsRepository extends PagingAndSortingRepository<Goods, Long> {
	
}
