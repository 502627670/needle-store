package org.needle.bookingdiscount.server.repository.freight;

import java.util.List;

import org.needle.bookingdiscount.domain.freight.FreightTemplate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FreightTemplateRepository extends PagingAndSortingRepository<FreightTemplate, Long> {
	
	@Query("from FreightTemplate t where t.isDelete=?1")
	public List<FreightTemplate> findByIsDelete(boolean isDelete);
	
}
