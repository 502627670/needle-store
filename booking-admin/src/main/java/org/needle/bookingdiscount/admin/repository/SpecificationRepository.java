package org.needle.bookingdiscount.admin.repository;

import org.needle.bookingdiscount.domain.product.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SpecificationRepository extends PagingAndSortingRepository<Specification, Long> {
	
}
