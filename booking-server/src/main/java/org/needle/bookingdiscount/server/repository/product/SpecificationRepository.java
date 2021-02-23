package org.needle.bookingdiscount.server.repository.product;

import org.needle.bookingdiscount.domain.product.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SpecificationRepository extends PagingAndSortingRepository<Specification, Long> {
	
}
